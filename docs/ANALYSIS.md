# Phân tích hệ thống (dựa trên source code hiện tại)

Lưu ý: Không suy đoán ngoài phạm vi code. Tất cả nhận định bên dưới được rút ra từ các class Controller/Service/Entity/Repository đang có.

## 1) Kiến trúc tổng thể
- Layer hóa theo chuẩn Spring:
  - Controller: CheckoutController, CartController, OrderController, SepayController.
  - Service: CheckoutService, OrderService, CartService (tham chiếu qua controller), SepayService, StockReservationService (được dùng trong CheckoutService).
  - Repository: CartRepository, CartItemRepository, OrderRepository, OrderItemRepository, ProductVariantRepository, PaymentRepository, PaymentTransactionRepository, CategoryRepository, AttributeValueRepository, ...
  - Entity (JPA): Category, Product, ProductImage, ProductVariant, VariantImage, Attribute, AttributeValue, Cart, CartItem, Order, OrderItem, Payment, PaymentTransaction, StockReservation.
- Giao tiếp theo luồng: Controller -> Service -> Repository -> DB. Mapper: OrderMapper dùng để chuyển Entity -> DTO trong OrderService.
- Transaction/rollback:
  - CheckoutService.checkout(UUID, CheckoutRequest) được annotate @Transactional (jakarta.transaction.Transactional): to\àn bộ quy trình checkout chạy trong 1 giao dịch. Nếu có exception (BusinessException hoặc runtime), lệnh `throw e` trong catch sẽ khiến transaction rollback.
  - OrderService được annotate @Transactional ở class: các thao tác cập nhật trạng thái trong service này chạy trong giao dịch.
  - SepayService.handleIpnRaw(Map) annotate @Transactional: ghi PaymentTransaction và cập nhật Payment/Order trong cùng giao dịch; có kiểm tra idempotency theo transactionCode.

## 2) Xử lý tồn kho & tránh oversell
- Cơ chế đặt chỗ (reservation) qua StockReservationService (được gọi trong CheckoutService):
  - Bước đầu: duyệt từng CartItem và gọi `reserve(cartToken, variantId, quantity)`.
  - Khi commit stock: dùng `variantRepo.findByIdForUpdate(id)` để khóa hàng (LOCK FOR UPDATE) ở mức DB, lấy số lượng đã reserve `getReservedQuantity(variantId)`, tính `available = stockQuantity - reserved`. Nếu `available < item.quantity` -> throw BusinessException (rollback toàn bộ checkout).
  - Nếu đủ, trừ `variant.stockQuantity -= item.quantity` rồi lưu. Sau khi commit xong, release reservation theo biến thể `releaseByVariant(variantId)`.
- Cơ chế này kết hợp: reservation tạm thời + khóa hàng khi trừ kho -> ngăn oversell khi có concurrent checkout.

## 3) Nghiệp vụ chính theo code
- Catalog sản phẩm có biến thể:
  - ProductVariant có các thuộc tính: sku (unique, not null), price, stockQuantity, status, @Version; thuộc Product (ManyToOne). Product có ProductImage, ProductVariant.
  - ManyToMany giữa ProductVariant và AttributeValue (size, color...). AttributeValue unique theo (attribute_id, value).
- Giỏ hàng (Cart, CartItem):
  - Cart có cartToken (UUID unique). CartItem tham chiếu Variant và quantity. CartController expose API: xem, thêm, cập nhật, xóa item, clear cart (qua CartService).
- Checkout & tạo đơn hàng (CheckoutService.checkout):
  - Load cart; nếu rỗng -> BusinessException.
  - Reserve stock theo từng CartItem.
  - Tạo Order (đặt CREATED, sinh orderCode/trackingToken thời điểm này trong Service). Tính tổng tiền từ CartItem (price * qty) và set `totalAmount`, lưu OrderItem tương ứng.
  - Tạo Payment: nếu COD -> SUCCESS, ngược lại PENDING. Với non-COD, log PaymentTransaction PENDING.
  - Commit stock: findByIdForUpdate, check available >= qty, trừ stock, lưu.
  - Release reservation từng variant, clear cart, save cart. Toàn bộ trong 1 transaction, có catch + rethrow để rollback khi lỗi.
- Giữ tồn kho (Stock Reservation): entity StockReservation có quantity, expiredAt, status (enum), cartToken, variant (ManyToOne). Dịch vụ `StockReservationService` được dùng nhưng chi tiết không hiển thị ở đây; từ usage: có các hàm reserve, getReservedQuantity, releaseByVariant.
- Thanh toán COD & SePay:
  - COD: Payment.status = SUCCESS ngay khi checkout.
  - Cổng SePay:
    - SepayController.handleIpn(SepayIpnRequest): log req, tìm Order theo orderCode trong req. Nếu tìm thấy và transactionStatus == APPROVED -> set Order.status = PAID và save.
    - SepayService.handleIpnRaw(Map): tìm Order, Payment; idempotency: nếu transactionCode đã tồn tại trong PaymentTransactionRepository thì return. Ngược lại save PaymentTransaction, nếu status SUCCESS/PAID -> Payment.status = SUCCESS và Order.status = PAID.
- Tracking đơn hàng không cần đăng nhập:
  - OrderController.getByTrackingToken(UUID): gọi OrderService.getByTrackingToken -> OrderRepository.findByTrackingToken; trả về OrderResponse qua OrderMapper.
- Admin xử lý trạng thái đơn hàng:
  - OrderController.updateStatus(Long id, OrderStatus status): gọi OrderService.updateStatus; trong Service tìm order, setStatus, trả DTO. @Transactional đảm bảo flush/commit.

## 4) Ghi chú về trạng thái/enum
- OrderStatus: CREATED, CONFIRMED, PAID, SHIPPING, COMPLETED, CANCELLED.
- PaymentStatus: (hiện diện trong code, dùng SUCCESS/PENDING). PaymentMethod: COD, các phương thức khác (ví dụ SEPAY) theo enum.

## 5) Diagram (tham chiếu file trong /docs)
- ERD.puml — ERD dựa theo JPA annotations, có comment ngắn cho từng entity.
- SequenceCheckout.puml — Luồng checkout và commit stock với reservation + lock for update.
- SequenceSepayIpn.puml — Luồng IPN qua SepayController.
- SequenceSepayServiceIpnRaw.puml — Luồng IPN qua SepayService với idempotency.
- SequenceOrderTracking.puml — Tra cứu đơn bằng trackingToken.
- SequenceAdminUpdateStatus.puml — Admin cập nhật trạng thái đơn.
