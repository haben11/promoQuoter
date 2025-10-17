# PromoQuoter - Cart Pricing and Reservation Microservice

A Spring Boot microservice for calculating cart prices with dynamic promotions and managing inventory reservations.

---

## Assumptions

- Promotion Priority: Promotions are applied in order of the `orderPriority` field (lower values first i.e. pipeline)
- Concurrency: Pessimistic locking on products ensures data consistency during stock reservation
- Audit Trail: Quote responses include `appliedPromotions` with detailed discount information
- Pricing: All prices are rounded using HALF_UP to 2 decimal places; negative prices are prevented
- Idempotency: Duplicate order submissions are prevented using `Idempotency-Key` headers
- Error Responses: All failures return structured error responses with appropriate HTTP status codes

---

## How to Run

### Prerequisites
- Java 21+
- Maven 3.6+

### Steps

1. **Clone and navigate to the project**
   ```bash
   git clone <repository-url>
   cd promotion_quoter
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
    - API: `http://localhost:8080`
    - H2 Console: `http://localhost:8080/h2`
        - JDBC URL: `jdbc:h2:mem:promo_quote`
        - Username: `kifiya`

---

## Sample CURL Requests

### 1. Get Cart Quote

Calculate total price with promotions applied:

```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "feb5456b-6a27-4447-a32b-a856fa3840a7",
        "quantity": 2
      },
      {
        "productId": "8c7b9e3a-1f4d-4e5c-9a2b-3d6f8e1c4a5b",
        "quantity": 1
      }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Response:**
```json
{
  "itemPrices": {
    "feb5456b-6a27-4447-a32b-a856fa3840a7": 2400.00,
    "8c7b9e3a-1f4d-4e5c-9a2b-3d6f8e1c4a5b": 500.00
  },
  "totalPrice": 2900.00,
  "appliedPromotions": [
    "Applied BUY_X_GET_Y promotion: Buy 2, Get 1 free on feb5456b-6a27-4447-a32b-a856fa3840a7"
  ]
}
```

---

### 2. Confirm Order

Reserve stock and create order:

```bash
curl -X POST http://localhost:8080/cart/confirm \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: unique-key-123" \
  -d '{
    "items": [
      {
        "productId": "feb5456b-6a27-4447-a32b-a856fa3840a7",
        "quantity": 1
      }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Response:**
```json
{
  "finalPrice": 1200.00,
  "orderId": "38a47490-9abc-4b27-bb49-908b3c3c27ef"
}
```

---

### 3. Error Scenarios

**Invalid quantity (returns 400):**
```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "feb5456b-6a27-4447-a32b-a856fa3840a7",
        "quantity": 0
      }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Non-existent product (returns 400):**
```bash
curl -X POST http://localhost:8080/cart/quote \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "00000000-0000-0000-0000-000000000000",
        "quantity": 1
      }
    ],
    "customerSegment": "REGULAR"
  }'
```

**Insufficient stock (returns 409):**
```bash
curl -X POST http://localhost:8080/cart/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "feb5456b-6a27-4447-a32b-a856fa3840a7",
        "quantity": 10000
      }
    ],
    "customerSegment": "REGULAR"
  }'
```

---

## Error Response Format

All errors return a structured response:

```json
{
  "timeStamp": "2025-10-17T17:24:21",
  "status": 400,
  "error": "Bad Request",
  "errorMessage": "Product not found",
  "path": "/cart/quote",
  "errors": []
}
```

**Status Codes:**
- `400` - Validation errors or resource not found
- `409` - Stock conflicts (insufficient inventory)
- `500` - Internal server errors


## Technology Stack

- Spring Boot 3.5.6, Java 21
- H2 Database (in-memory)
- JUnit 5, Mockito
- Maven

---

## Testing

Run all tests:
```bash
./mvnw test
```

Test Coverage: 62 tests with 100% pass rate