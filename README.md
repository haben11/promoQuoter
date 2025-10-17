# PromoQuoter - Cart Pricing and Reservation Microservice

## Assumptions
- Promotions applied in order of `orderPriority` field (lower first).
- Pessimistic locking for stock reservation to handle high concurrency.
- Audit trail included in quote response as appliedPromotions (detailed with discounts).
- Prices rounded HALF_UP to 2 decimals, never negative.
- Idempotency prevents double-reservation.
- Structured error responses for all failures.

## How to Run
1. Clone the repo.
2. Run `mvn spring-boot:run`.
3. Access on http://localhost:8080; H2 console at /h2 (URL: jdbc:h2:mem:promo_quote).

## Error Handling
- Validation errors: 400 with detailed message
- Stock conflict: 409 with detailed message.
- Other: 500 with generic detailed message.

## Sample CURL Requests
(same as before, plus error example)
Invalid Quote:
curl -X POST http://localhost:8080/cart/quote -H "Content-Type: application/json" -d '{"items":[{"productId":1,"qty":0}],"customerSegment":"REGULAR"}'

## Extending Rules
- Add new PromotionType and rule class.
- Use factory to instantiate.
- Decorator can be toggled for more auditing.