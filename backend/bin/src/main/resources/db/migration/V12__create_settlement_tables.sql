CREATE SEQUENCE settlement_number_seq START 1;

CREATE TABLE shop_settlements (
    id UUID PRIMARY KEY,
    settlement_number VARCHAR(100) NOT NULL UNIQUE,
    source_shop_id UUID NOT NULL REFERENCES shops(id),
    destination_shop_id UUID NOT NULL REFERENCES shops(id),
    transfer_id UUID NOT NULL REFERENCES stock_transfers(id) UNIQUE,
    gross_amount NUMERIC(19, 2) NOT NULL,
    paid_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    remaining_amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(50) NOT NULL,
    due_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shop_settlements_source_shop ON shop_settlements(source_shop_id);
CREATE INDEX idx_shop_settlements_destination_shop ON shop_settlements(destination_shop_id);
CREATE INDEX idx_shop_settlements_status ON shop_settlements(status);

CREATE TABLE settlement_payments (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL REFERENCES shop_settlements(id),
    amount NUMERIC(19, 2) NOT NULL,
    payment_mode VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    idempotency_key VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_settlement_payments_idempotency ON settlement_payments(settlement_id, idempotency_key);

CREATE TABLE settlement_disputes (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL REFERENCES shop_settlements(id),
    raised_by_id UUID NOT NULL REFERENCES users(id),
    reason VARCHAR(100) NOT NULL,
    claimed_amount NUMERIC(19, 2),
    status VARCHAR(50) NOT NULL,
    resolution TEXT,
    resolved_by_id UUID REFERENCES users(id),
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reconciliation_records (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL REFERENCES shop_settlements(id),
    shop_id UUID NOT NULL REFERENCES shops(id),
    expected_amount NUMERIC(19, 2) NOT NULL,
    actual_amount NUMERIC(19, 2) NOT NULL,
    difference NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    reconciled_by_id UUID REFERENCES users(id),
    reconciled_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
