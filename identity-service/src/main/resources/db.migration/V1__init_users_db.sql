CREATE TABLE t_users
(
    id                VARCHAR(255)                              NOT NULL,
    username          VARCHAR(50)                             NOT NULL,
    email             VARCHAR(100)                            NOT NULL,
    role              VARCHAR(20)                             NOT NULL,
    password          VARCHAR(255)                             NOT NULL,
    full_name         VARCHAR(100),
    phone_number      VARCHAR(20),
    is_active         BOOLEAN                                 NOT NULL,
    is_email_verified BOOLEAN                                 NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_t_users PRIMARY KEY (id)
);

ALTER TABLE t_users
    ADD CONSTRAINT uc_t_users_email UNIQUE (email);

ALTER TABLE t_users
    ADD CONSTRAINT uc_t_users_phone_number UNIQUE (phone_number);

ALTER TABLE t_users
    ADD CONSTRAINT uc_t_users_username UNIQUE (username);