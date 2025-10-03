# Carts API

A reactive Spring Boot application built with Kotlin for managing shopping carts using MongoDB. It exposes RESTful APIs for creating and retrieving carts.

## Project Structure

- **dao**: Data Access Objects (e.g., Cart entity with MongoDB annotations).
- **model**: Domain models (e.g., Item).
- **dto**: Data Transfer Objects (e.g., CreateCartRequest for API inputs).
- **repository**: Reactive repositories (e.g., CartRepository extending ReactiveMongoRepository).
- **controller**: REST controllers (e.g., CartController with suspend functions).

## Prerequisites

- JDK 21 (as specified in build.gradle.kts).
- MongoDB instance (local on port 27017 or Docker: `docker run -d -p 27017:27017 mongo`). The app connects to `mongodb://localhost:27017/cartsdb` by default; override in `src/main/resources/application.properties` if needed (e.g., `spring.data.mongodb.uri=mongodb://localhost:27017/cartsdb`).

## Running the Application

1. Navigate to the project root.
2. Build and run:
   ```
   ./gradlew bootRun
   ```
3. The application starts on `http://localhost:8080`.

## API Endpoints

- **POST /carts**: Creates a new cart with userId and items (optional, can be empty), returns the generated cartId.
- **GET /carts/{cartId}**: Retrieves the cart by cartId, returns cart details or 404 if not found.

## Testing the APIs

Use curl (or Postman/browser for GET). Replace `{cartId}` with the ID from POST response.

### Create Cart (POST /carts)
```
curl -X POST http://localhost:8080/carts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "items": [
      {
        "productId": "product1",
        "quantity": 2
      },
      {
        "productId": "product2",
        "quantity": 1
      }
    ]
  }'
```
Expected response: `{"id": "generated-uuid"}`

### Retrieve Cart (GET /carts/{cartId})
```
curl http://localhost:8080/carts/generated-uuid-here
```
Expected response (if found):
```
{
  "id": "generated-uuid",
  "userId": "user123",
  "items": [
    {
      "productId": "product1",
      "quantity": 2
    },
    {
      "productId": "product2",
      "quantity": 1
    }
  ]
}
```
If not found: 404 Not Found.

## Load Testing

This project includes comprehensive load testing capabilities using K6. The load testing setup can scale up to 1000 concurrent users with realistic ramp-up patterns.

**Quick Start:**
```bash
cd load-testing
./test.sh
```

For detailed load testing documentation, see [load-testing/README.md](load-testing/README.md).

## Running Integration Tests

Integration tests for the endpoints are provided in `src/test/kotlin/com/example/carts/controller/CartControllerTest.kt` using Spring Boot's WebTestClient for reactive testing.

To run the tests:
```
./gradlew test
```

The tests cover:
- Creating a cart and verifying the returned cartId.
- Retrieving a created cart and validating its contents.
- Handling non-existent cart (404).

Tests use an in-memory MongoDB or the configured instance; ensure MongoDB is running if not using embedded.

## Troubleshooting

- **Compilation errors**: Ensure JDK 21 is installed and `JAVA_HOME` points to it.
- **MongoDB connection issues**: Check logs for errors; verify MongoDB is running.
- **Reactive behavior**: APIs are non-blocking; responses are asynchronous.
- **Test failures**: Ensure MongoDB is accessible; tests clean up data before/after each run.

## Dependencies

Managed via build.gradle.kts:
- spring-boot-starter-data-mongodb-reactive
- spring-boot-starter-webflux
- kotlinx-coroutines-reactor
- Others for Kotlin, Jackson, etc.