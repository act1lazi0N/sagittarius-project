CREATE SCHEMA IF NOT EXISTS order_service;

CREATE TABLE IF NOT EXISTS order_service.orders
(
    id           UUID PRIMARY KEY,
    customer_id  VARCHAR(50)    NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    status       VARCHAR(20)    NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_service.outbox_events
(
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   VARCHAR(50) NOT NULL,
    type           VARCHAR(50) NOT NULL,
    payload        TEXT        NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE SCHEMA IF NOT EXISTS inventory_service;

CREATE TABLE IF NOT EXISTS inventory_service.products
(
    id                 VARCHAR(50) PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    available_quantity INTEGER      NOT NULL CHECK (available_quantity >= 0)
);

CREATE TABLE IF NOT EXISTS inventory_service.processed_orders
(
    order_id   VARCHAR(50) PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory_service.outbox_events
(
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   VARCHAR(50) NOT NULL,
    type           VARCHAR(50) NOT NULL,
    payload        TEXT        NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO inventory_service.products (id, name, available_quantity)
VALUES ('IPHONE-15', 'iPhone 15 Pro Max', 100),
       ('MACBOOK-PRO', 'MacBook Pro M3', 5),
       ('TEST-ITEM', 'Test Item Unlimited', 9999)
ON CONFLICT (id) DO UPDATE
    SET available_quantity = EXCLUDED.available_quantity;


CREATE SCHEMA IF NOT EXISTS payment_service;

CREATE TABLE IF NOT EXISTS payment_service.customer_balances
(
    customer_id VARCHAR(50) PRIMARY KEY,
    balance     DECIMAL(19, 2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS payment_service.payments
(
    id          UUID PRIMARY KEY,
    order_id    VARCHAR(50)    NOT NULL,
    customer_id VARCHAR(50)    NOT NULL,
    amount      DECIMAL(19, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payment_service.processed_orders
(
    order_id   VARCHAR(50) PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payment_service.outbox_events
(
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   VARCHAR(50) NOT NULL,
    type           VARCHAR(50) NOT NULL,
    payload        TEXT        NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

TRUNCATE TABLE payment_service.customer_balances;
INSERT INTO payment_service.customer_balances (customer_id, balance)
VALUES ('TEST-USER-01', 5000.00),
       ('RICH-KID', 1000000.00),
       ('POOR-USER', 10.00);

