# Sagittarius Microservices Project

Dự án mô phỏng hệ thống E-commerce áp dụng kiến trúc Microservices & Event-Driven (Saga Pattern).

## Architecture
* **Service Discovery:** Netflix Eureka
* **API Gateway:** Spring Cloud Gateway
* **Message Broker:** Apache Kafka
* **Database:** PostgreSQL (Database per Service)
* **Distributed Tracing:** Zipkin & Micrometer
* **Resilience:** Resilience4j (Circuit Breaker)

## Services
1.  **Order Service:** Quản lý đơn hàng.
2.  **Inventory Service:** Quản lý kho.
3.  **Payment Service:** Quản lý số dư ví.
4.  **Discovery Server:** Danh bạ dịch vụ.
5.  **API Gateway:** Cổng giao tiếp duy nhất (Port 8080).

## Tech Stack
* Java 21
* Spring Boot 3.x
* Spring Cloud 2023.x
* Docker & Docker Compose

## How to Run

### 1. Start Infrastructure
Chạy Database, Kafka, Zipkin bằng Docker:
```bash
docker-compose up -d