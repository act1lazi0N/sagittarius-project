CREATE TABLE t_customer_balances
(
    customer_id VARCHAR(255) NOT NULL,
    balance     DECIMAL      NOT NULL,
    CONSTRAINT pk_t_customer_balances PRIMARY KEY (customer_id)
);

CREATE TABLE t_outbox_events
(
    id             UUID         NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        TEXT         NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_t_outbox_events PRIMARY KEY (id)
);

CREATE TABLE t_payments
(
    id          UUID         NOT NULL,
    order_id    VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    amount      DECIMAL      NOT NULL,
    status      VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_t_payments PRIMARY KEY (id)
);

CREATE TABLE t_processed_orders
(
    order_id   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_t_processed_orders PRIMARY KEY (order_id)
);