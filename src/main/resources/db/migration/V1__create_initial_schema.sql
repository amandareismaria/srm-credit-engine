CREATE TABLE currency
(
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(3)  NOT NULL,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_currency_code UNIQUE (code)
);


CREATE TABLE receivable_type
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)    NOT NULL,
    spread_rate NUMERIC(10, 6) NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_receivable_type_name UNIQUE (name),
    CONSTRAINT ck_receivable_type_spread_rate
        CHECK (spread_rate >= 0)
);


CREATE TABLE exchange_rate
(
    id               BIGSERIAL PRIMARY KEY,
    from_currency_id BIGINT         NOT NULL,
    to_currency_id   BIGINT         NOT NULL,
    rate             NUMERIC(19, 6) NOT NULL,
    effective_at     TIMESTAMP      NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_exchange_rate_from_currency
        FOREIGN KEY (from_currency_id)
            REFERENCES currency (id),

    CONSTRAINT fk_exchange_rate_to_currency
        FOREIGN KEY (to_currency_id)
            REFERENCES currency (id),

    CONSTRAINT ck_exchange_rate_rate
        CHECK (rate > 0),

    CONSTRAINT ck_exchange_rate_different_currency
        CHECK (from_currency_id <> to_currency_id)
);


CREATE TABLE receivable
(
    id                 BIGSERIAL PRIMARY KEY,
    assignor           VARCHAR(150)   NOT NULL,
    face_value         NUMERIC(19, 6) NOT NULL,
    currency_id        BIGINT         NOT NULL,
    receivable_type_id BIGINT         NOT NULL,
    due_date           DATE           NOT NULL,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_receivable_currency
        FOREIGN KEY (currency_id)
            REFERENCES currency (id),

    CONSTRAINT fk_receivable_type
        FOREIGN KEY (receivable_type_id)
            REFERENCES receivable_type (id),

    CONSTRAINT ck_receivable_face_value
        CHECK (face_value > 0)
);


CREATE TABLE settlement
(
    id                  BIGSERIAL PRIMARY KEY,
    receivable_id       BIGINT         NOT NULL,
    payment_currency_id BIGINT         NOT NULL,
    exchange_rate       NUMERIC(19, 6),
    base_rate           NUMERIC(10, 6) NOT NULL,
    spread_rate         NUMERIC(10, 6) NOT NULL,
    present_value       NUMERIC(19, 6) NOT NULL,
    settled_amount      NUMERIC(19, 6) NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    settled_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_settlement_receivable
        FOREIGN KEY (receivable_id)
            REFERENCES receivable (id),

    CONSTRAINT fk_settlement_payment_currency
        FOREIGN KEY (payment_currency_id)
            REFERENCES currency (id),

    CONSTRAINT ck_settlement_base_rate
        CHECK (base_rate >= 0),

    CONSTRAINT ck_settlement_spread_rate
        CHECK (spread_rate >= 0),

    CONSTRAINT ck_settlement_present_value
        CHECK (present_value > 0),

    CONSTRAINT ck_settlement_amount
        CHECK (settled_amount > 0),

    CONSTRAINT ck_settlement_status
        CHECK (
            status IN (
                       'PENDING',
                       'SETTLED',
                       'FAILED',
                       'CANCELLED'
                )
            )
);

CREATE INDEX idx_exchange_rate_currencies
    ON exchange_rate (from_currency_id, to_currency_id);

CREATE INDEX idx_exchange_rate_effective_at
    ON exchange_rate (effective_at);

CREATE INDEX idx_receivable_due_date
    ON receivable (due_date);

CREATE INDEX idx_receivable_assignor
    ON receivable (assignor);

CREATE INDEX idx_receivable_currency
    ON receivable (currency_id);

CREATE INDEX idx_receivable_type
    ON receivable (receivable_type_id);

CREATE INDEX idx_settlement_settled_at
    ON settlement (settled_at);

CREATE INDEX idx_settlement_payment_currency
    ON settlement (payment_currency_id);

CREATE INDEX idx_settlement_status
    ON settlement (status);