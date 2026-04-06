# Sagittarius

**Enterprise E-commerce Platform** built with Microservices and Event-Driven Architecture.

> Xử lý triệt để các bài toán phân tán phức tạp: Quản lý giao dịch (Saga Pattern), Đồng bộ dữ liệu đa nền tảng, Định danh tập trung, và Chống gian lận giá.

---

## Features

| Feature | Technology | Description |
|---------|-----------|-------------|
| **Distributed Transactions** | Kafka (Saga & Outbox) | Đảm bảo tính nhất quán dữ liệu giữa Đặt hàng, Trừ kho và Thanh toán |
| **Polyglot Persistence** | Postgres, Mongo, Redis, ES | Áp dụng CQRS, sử dụng DB chuyên biệt cho từng loại dữ liệu |
| **Centralized Auth** | Keycloak, OAuth2 | API Gateway tự động Offload JWT, Identity quản lý user tập trung |
| **Anti-fraud System** | Inter-service REST | Cơ chế tự động đối chiếu và chống thay đổi giá từ phía Client |
| **Cart Management** | Redis In-memory | Quản lý giỏ hàng siêu tốc, tự động dọn dẹp qua Kafka Events |
| **Observability** | Zipkin, Resilience4j | Truy vết Request (Distributed Tracing) và Circuit Breaking chịu lỗi |

---

## Architecture

    ┌─────────────────────────────────────────────────────────┐
    │                 API Gateway (:8080)                     │
    │  ┌──────────────┐  ┌───────────────┐  ┌──────────────┐  │
    │  │ JWT Offload  │  │Circuit Breaker│  │ Load Balancer│  │
    │  └──────┬───────┘  └───────┬───────┘  └──────┬───────┘  │
    └─────────┼──────────────────┼─────────────────┼──────────┘
              │                  │                 │
    ┌─────────┼──────────────────┼─────────────────┼──────────┐
    │         │        Microservices Layer         │          │
    │ ┌───────┴──────┐ ┌─────────┴───────┐ ┌───────┴──────┐   │
    │ │Order Service │ │Inventory Service│ │Payment Serv  │   │
    │ └───────┬──────┘ └─────────┬───────┘ └───────┬──────┘   │
    │         │                  │                 │          │
    │ ┌───────┴──────┐ ┌─────────┴───────┐ ┌───────┴──────┐   │
    │ │ProductService│ │  Cart Service   │ │Identity Serv │   │
    │ └───────┬──────┘ └─────────┬───────┘ └───────┬──────┘   │
    └─────────┼──────────────────┼─────────────────┼──────────┘
              │                  │                 │
        ┌─────┴──────┐     ┌─────┴─────┐     ┌─────┴──────┐
        │ Kafka & DBs│     │Redis Cache│     │  Keycloak  │
        └────────────┘     └───────────┘     └────────────┘

---

## Project Structure

    sagittarius-project/
    ├── api-gateway/            # Spring Cloud Gateway, Resilience4j, JWT Auth
    ├── cart-service/           # Quản lý giỏ hàng tạm thời (Redis)
    ├── discovery-server/       # Netflix Eureka (Service Registry)
    ├── identity-service/       # Quản lý User & Đồng bộ Keycloak
    ├── inventory-service/      # Quản lý tồn kho (PostgreSQL)
    ├── notification-service/   # Gửi Email qua SMTP (Kafka Consumer)
    ├── order-service/          # Quản lý Đơn hàng (PostgreSQL, Outbox Pattern)
    ├── payment-service/        # Quản lý Ví điện tử khách hàng (PostgreSQL)
    ├── product-service/        # Quản lý Sản phẩm (MongoDB, Elasticsearch, Redis)
    ├── sagittarius-common/     # Core Library (Exceptions, DTOs, Events)
    ├── docker-compose.yml      # Hạ tầng 14 Containers
    └── pom.xml                 # Parent POM

---

## Getting Started

### Prerequisites

- [Java 21](https://aws.amazon.com/corretto/) (Nếu chạy qua IDE)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Bắt buộc để chạy Databases & Message Broker)

### 1. Configuration (.env)

Tạo file .env tại thư mục gốc của dự án và điền các cấu hình bảo mật:

    DB_USERNAME=admin
    DB_PASSWORD=password
    KC_ADMIN_USER=admin
    KC_ADMIN_PASS=admin
    JWT_SECRET_KEY=SagittariusSuperSecretKeyThatIsVeryLongAndSecure1234567890!
    JWT_EXPIRATION_TIME=86400000
    MAIL_USERNAME=your_email@gmail.com
    MAIL_PASSWORD=your_app_password
    MAIL_NAME=Sagittarius E-commerce
    TEST_RECIPIENT_EMAIL=your_email@gmail.com

### 2. Build Services

Sử dụng Maven Wrapper có sẵn để đóng gói toàn bộ mã nguồn:

    # Windows
    .\mvnw clean package -DskipTests

    # macOS / Linux
    ./mvnw clean package -DskipTests

### 3. Run Full Stack

Khởi động hệ thống hạ tầng (Postgres, Mongo, Redis, Kafka, Keycloak) và 9 Microservices bằng Docker:

    docker-compose up -d --build

---

## Admin Dashboards & API Reference

Sau khi hệ thống khởi động hoàn tất (Healthy), bạn có thể truy cập các hệ thống monitor:

| Service / Tool | Endpoint | Description |
|--------|----------|-------------|
| **API Gateway / Swagger** | http://localhost:8080/swagger-ui.html | Test API tập trung toàn hệ thống |
| **Eureka Registry** | http://localhost:8761 | Xem danh sách các Service đang sống |
| **Keycloak Admin** | http://localhost:8181 | Quản lý Users, Clients, Roles (OAuth2) |
| **Kafka UI** | http://localhost:8090 | Quản lý Topic, theo dõi Message/Events |
| **Zipkin** | http://localhost:9411 | Truy vết Request (Distributed Tracing) |

### End-to-End Saga Flow

| Event | Direction | Description |
|-------|-----------|-------------|
| OrderCreated | Order -> Kafka -> Inventory | Báo hiệu đơn hàng mới, kích hoạt trừ kho |
| InventoryReserved | Inventory -> Kafka -> Payment | Kho đã giữ chỗ thành công, gọi trừ tiền |
| InventoryFailed | Inventory -> Kafka -> Order | Kho hết hàng, gọi Order để Hủy đơn |
| PaymentCompleted | Payment -> Kafka -> Order | Trừ tiền thành công, Order chuyển sang PAID |
| PaymentFailed | Payment -> Kafka -> Order/Inv | Thiếu tiền, Hủy đơn & Hoàn lại kho |
| OrderCompleted | Order -> Kafka -> Notif/Cart | Báo cáo chốt đơn, Gửi Email & Dọn giỏ hàng |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend Core** | Java 21, Spring Boot 3.2, Spring Cloud 2023.x |
| **Databases** | PostgreSQL 15, MongoDB 7, Elasticsearch 8 |
| **Caching & Fast Data** | Redis 7 |
| **Event Streaming** | Apache Kafka, Zookeeper |
| **Security & Auth** | Spring Security, Keycloak 24, JWT |
| **DevOps** | Docker, Docker Compose, Maven Wrapper |

---

## License

This project is for educational purposes.