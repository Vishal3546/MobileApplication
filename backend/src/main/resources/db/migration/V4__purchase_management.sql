-- Alter Customer Consents for Generic Reference
ALTER TABLE customer_consents ADD COLUMN reference_type VARCHAR(50);
ALTER TABLE customer_consents ADD COLUMN reference_id UUID;

CREATE INDEX idx_customer_consents_reference ON customer_consents(reference_type, reference_id);

-- Purchase Number Sequence
CREATE SEQUENCE purchase_number_seq START 1;

-- Table: purchase_transactions
CREATE TABLE purchase_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE RESTRICT,
    employee_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE RESTRICT,
    suggested_price DECIMAL(15,2),
    negotiated_price DECIMAL(15,2),
    final_price DECIMAL(15,2),
    notes TEXT,
    transaction_status VARCHAR(50) NOT NULL DEFAULT 'INITIATED',
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_transactions_customer_id ON purchase_transactions(customer_id);
CREATE INDEX idx_purchase_transactions_device_id ON purchase_transactions(device_id);
CREATE INDEX idx_purchase_transactions_branch_id ON purchase_transactions(branch_id);
CREATE INDEX idx_purchase_transactions_status ON purchase_transactions(transaction_status);
CREATE INDEX idx_purchase_transactions_created_at ON purchase_transactions(created_at);

-- Table: purchase_payments
CREATE TABLE purchase_payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_transaction_id UUID NOT NULL REFERENCES purchase_transactions(id) ON DELETE CASCADE,
    payment_mode VARCHAR(50) NOT NULL, -- CASH, UPI, BANK_TRANSFER, CARD, OTHER
    amount DECIMAL(15,2) NOT NULL,
    reference_number VARCHAR(255),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED, CANCELLED
    transaction_time TIMESTAMP WITH TIME ZONE,
    media_id UUID REFERENCES media_files(id) ON DELETE SET NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    processed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_payments_transaction_id ON purchase_payments(purchase_transaction_id);
CREATE INDEX idx_purchase_payments_status ON purchase_payments(payment_status);
CREATE INDEX idx_purchase_payments_reference ON purchase_payments(reference_number);

-- Table: purchase_receipts
CREATE TABLE purchase_receipts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_transaction_id UUID NOT NULL REFERENCES purchase_transactions(id) ON DELETE RESTRICT,
    receipt_number VARCHAR(100) NOT NULL UNIQUE,
    media_id UUID NOT NULL REFERENCES media_files(id) ON DELETE RESTRICT,
    generated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    generated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_receipts_transaction_id ON purchase_receipts(purchase_transaction_id);

-- Table: purchase_status_history
CREATE TABLE purchase_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    purchase_transaction_id UUID NOT NULL REFERENCES purchase_transactions(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    reason TEXT,
    changed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    branch_id UUID REFERENCES branches(id) ON DELETE SET NULL,
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_status_history_transaction_id ON purchase_status_history(purchase_transaction_id);

-- Permissions
INSERT INTO permissions (name, description) VALUES
('VIEW_PURCHASES', 'Can view purchase transactions'),
('UPDATE_PURCHASE', 'Can update an open purchase transaction'),
('TRANSITION_PURCHASE', 'Can transition purchase statuses'),
('CANCEL_PURCHASE', 'Can cancel an open purchase transaction'),
('CREATE_PURCHASE_PAYMENT', 'Can process purchase payments'),
('VIEW_PURCHASE_PAYMENTS', 'Can view purchase payments'),
('COMPLETE_PURCHASE', 'Can finalize and complete a purchase transaction'),
('VIEW_PURCHASE_RECEIPT', 'Can view purchase receipts');

-- Assign to SUPER_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPER_ADMIN' 
AND p.name IN (
    'VIEW_PURCHASES', 'UPDATE_PURCHASE', 
    'TRANSITION_PURCHASE', 'CANCEL_PURCHASE', 'CREATE_PURCHASE_PAYMENT', 
    'VIEW_PURCHASE_PAYMENTS', 'COMPLETE_PURCHASE', 'VIEW_PURCHASE_RECEIPT'
);

-- Assign to ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' 
AND p.name IN (
    'VIEW_PURCHASES', 'CREATE_PURCHASE', 'UPDATE_PURCHASE', 
    'TRANSITION_PURCHASE', 'CANCEL_PURCHASE', 'CREATE_PURCHASE_PAYMENT', 
    'VIEW_PURCHASE_PAYMENTS', 'COMPLETE_PURCHASE', 'VIEW_PURCHASE_RECEIPT'
);

-- Assign to PURCHASE_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'PURCHASE_MANAGER' 
AND p.name IN (
    'VIEW_PURCHASES', 'CREATE_PURCHASE', 'UPDATE_PURCHASE', 
    'TRANSITION_PURCHASE', 'CANCEL_PURCHASE', 'CREATE_PURCHASE_PAYMENT', 
    'VIEW_PURCHASE_PAYMENTS', 'COMPLETE_PURCHASE', 'VIEW_PURCHASE_RECEIPT'
);

-- Assign to ACCOUNTANT
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ACCOUNTANT' 
AND p.name IN (
    'VIEW_PURCHASES', 'VIEW_PURCHASE_PAYMENTS', 'CREATE_PURCHASE_PAYMENT', 'VIEW_PURCHASE_RECEIPT'
);
