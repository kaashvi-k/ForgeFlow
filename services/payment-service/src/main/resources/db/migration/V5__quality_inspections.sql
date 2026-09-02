CREATE TABLE order_quality_context (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    sku_profile TEXT NOT NULL,
    correlation_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE quality_inspections (
    inspection_id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PASSED', 'FAILED')),
    reason_code VARCHAR(64),
    reason_detail VARCHAR(500),
    correlation_id UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE quality_inspection_history (
    history_id UUID PRIMARY KEY,
    inspection_id UUID NOT NULL REFERENCES quality_inspections (inspection_id),
    order_id UUID NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    detail VARCHAR(500),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_quality_history_inspection_id
    ON quality_inspection_history (inspection_id, occurred_at);
