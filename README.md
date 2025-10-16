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
3. Access on http://localhost:8080; H2 console at /h2-console (URL: jdbc:h2:mem:testdb).

## Sample CURL Requests
(same as before, plus error example)
Invalid Quote:
curl -X POST http://localhost:8080/cart/quote -H "Content-Type: application/json" -d '{"items":[{"productId":1,"qty":0}],"customerSegment":"REGULAR"}'
