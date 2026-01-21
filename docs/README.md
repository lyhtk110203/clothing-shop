# Documentation (UML)

This folder contains PlantUML diagrams generated strictly from the current codebase (no external assumptions).

Files:
- ERD.puml — Entity Relationship Diagram matching JPA annotations in src/main/java/com/shop/clothing_shop/entity.
- SequenceCheckout.puml — Checkout flow via CheckoutController and CheckoutService.
- SequenceSepayIpn.puml — SePay IPN flow via SepayController.handleIpn.
- SequenceSepayServiceIpnRaw.puml — SePay IPN handling via SepayService.handleIpnRaw.
- SequenceOrderTracking.puml — Order tracking by trackingToken (no login).
- SequenceAdminUpdateStatus.puml — Admin updates order status.
- ANALYSIS.md — System analysis: architecture, transactions, stock & oversell prevention, main flows.

How to render:
1. Install PlantUML (or use an online renderer).
2. Open each .puml file and render to view the diagram.

Scope & accuracy:
- Entities, fields, relationships, and cardinalities reflect the exact JPA annotations present in the code.
- Sequence steps use actual class and method names from the code and show conditions/loops explicitly present.

Notes:
- Many-to-many between ProductVariant and AttributeValue is shown because of @ManyToMany in ProductVariant.
- Order↔Payment is one-to-one as per @OneToOne in Payment.
- Payment→PaymentTransaction is one-to-many as per @ManyToOne in PaymentTransaction.
