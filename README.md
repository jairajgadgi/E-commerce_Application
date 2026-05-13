# E-Commerce Application

A portfolio-ready Spring Boot e-commerce backend designed to demonstrate enterprise-level backend engineering skills.

## Highlights

- Spring Boot 3.4.x on Java 17
- Layered architecture: controller, service, repository, entity, DTO, exception, security, config
- JPA/Hibernate persistence with MySQL 8
- Validation with Jakarta Bean Validation
- Centralized exception handling
- JWT authentication with role-based authorization (ADMIN/CUSTOMER)
- Swagger/OpenAPI documentation with bearer auth support
- Redis-backed caching for catalog reads
- Kafka order event publishing and consumption
- Email notifications for registration and order confirmation
- Dockerfile and Docker Compose for containerized local runs
- Actuator health and info endpoints
- Seeded sample data for quick demos

## Core Domain

- Categories
- Products
- Customers
- Customer addresses
- Cart and cart items
- Orders and order items
- Payment transactions

## Demo Credentials

Configured in `src/main/resources/application.properties`:

- Admin: `admin / admin123`
- Customer: `customer / customer123`

## Run Locally

```bash
mvn clean spring-boot:run
```

Then open:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MySQL: `localhost:3306` (db: `ecommerce_db`, user: `ecommerce`, password: `ecommerce`)
- Health: `http://localhost:8080/actuator/health`

## Docker

Build and start everything:

```bash
docker compose up --build
```

Kafka is exposed on `localhost:29092` for host tools, while the app container uses the internal broker address `kafka:9092`.

MySQL is exposed on `localhost:3306`, Redis on `localhost:6379`, and MailHog UI is available at `http://localhost:8025`.

## Example APIs

### Public
- `GET /api/v1/categories`
- `GET /api/v1/products`
- `GET /api/v1/products/search?query=laptop`

### Authentication
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register/customer`

### Admin
- `POST /api/v1/admin/categories`
- `POST /api/v1/admin/products`
- `PUT /api/v1/admin/products/{id}`

### Customer
- `POST /api/v1/customers`
- `POST /api/v1/customers/{id}/addresses`
- `GET /api/v1/carts/{customerId}`
- `POST /api/v1/carts/{customerId}/items`
- `POST /api/v1/orders/checkout`

## Resume-Friendly Talking Points

- Designed a modular backend with clear separation of concerns and reusable DTO/entity mapping
- Implemented inventory-aware cart and checkout flows with transactional order persistence
- Added Kafka-based order event publishing/consumption for asynchronous order workflows
- Implemented JWT-based security and role-based API authorization
- Added Redis caching for read-heavy product/category APIs and cache eviction on writes
- Integrated email notifications for onboarding and order confirmation events
- Containerized the stack using Docker and Compose for reproducible local execution
