-- Create Sequences
CREATE SEQUENCE sale_number_seq START 1;
CREATE SEQUENCE invoice_number_seq START 1;

-- Table: sale_transactions
CREATE TABLE sale_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id),
    employee_id UUID NOT NULL REFERENCES users(id),
    branch_id UUID NOT NULL REFERENCES branches(id),
    selling_price DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    final_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    sale_status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    warranty_start_date TIMESTAMP WITH TIME ZONE,
    warranty_end_date TIMESTAMP WITH TIME ZONE,
    return_policy_code VARCHAR(100),
    notes TEXT,
    created_by UUID NOT NULL REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: sale_payments
CREATE TABLE sale_payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_transaction_id UUID NOT NULL REFERENCES sale_transactions(id) ON DELETE CASCADE,
    payment_mode VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    transaction_time TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: sales_invoices
CREATE TABLE sales_invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_transaction_id UUID NOT NULL REFERENCES sale_transactions(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    media_id UUID REFERENCES media_files(id),
    issued_by UUID NOT NULL REFERENCES users(id),
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: sale_status_histories
CREATE TABLE sale_status_histories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_transaction_id UUID NOT NULL REFERENCES sale_transactions(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    reason TEXT,
    changed_by UUID NOT NULL REFERENCES users(id),
    branch_id UUID NOT NULL REFERENCES branches(id),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_sale_tx_number ON sale_transactions(sale_number);
CREATE INDEX idx_sale_tx_customer ON sale_transactions(customer_id);
CREATE INDEX idx_sale_tx_inventory ON sale_transactions(inventory_item_id);
CREATE INDEX idx_sale_tx_branch ON sale_transactions(branch_id);
CREATE INDEX idx_sale_tx_status ON sale_transactions(sale_status);
CREATE INDEX idx_sale_tx_pay_status ON sale_transactions(payment_status);
CREATE INDEX idx_sale_tx_created_at ON sale_transactions(created_at);

CREATE INDEX idx_sale_pay_tx_id ON sale_payments(sale_transaction_id);
CREATE INDEX idx_sale_pay_idempotency ON sale_payments(idempotency_key);
CREATE INDEX idx_sale_pay_reference ON sale_payments(reference_number);

CREATE INDEX idx_sale_inv_number ON sales_invoices(invoice_number);
CREATE INDEX idx_sale_inv_tx_id ON sales_invoices(sale_transaction_id);

-- Insert new permissions
INSERT INTO permissions (name, description) VALUES
('VIEW_SALES', 'Can view sales'),
('CREATE_SALE', 'Can create sales'),
('UPDATE_SALE', 'Can update sales'),
('TRANSITION_SALE', 'Can transition sales statuses'),
('CANCEL_SALE', 'Can cancel sales'),
('CREATE_SALE_PAYMENT', 'Can create sale payments'),
('VIEW_SALE_PAYMENTS', 'Can view sale payments'),
('COMPLETE_SALE', 'Can complete sales'),
('VIEW_SALE_INVOICE', 'Can view sales invoices'),
('APPLY_SALE_DISCOUNT', 'Can apply sale discount'),
('OVERRIDE_SALE_PRICE', 'Can override sale price'),
('MANAGE_WARRANTY_POLICY', 'Can manage warranty policy'),
('VIEW_SALE_HISTORY', 'Can view sale history')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to roles
DO $$
DECLARE
    super_admin_id UUID;
    admin_id UUID;
    sales_manager_id UUID;
    employee_id UUID;
    accountant_id UUID;
    viewer_id UUID;
BEGIN
    SELECT id INTO super_admin_id FROM roles WHERE name = 'SUPER_ADMIN';
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO sales_manager_id FROM roles WHERE name = 'SALES_MANAGER';
    SELECT id INTO employee_id FROM roles WHERE name = 'EMPLOYEE';
    SELECT id INTO accountant_id FROM roles WHERE name = 'ACCOUNTANT';
    SELECT id INTO viewer_id FROM roles WHERE name = 'VIEWER';

    -- SUPER_ADMIN gets all new permissions
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT super_admin_id, id FROM permissions WHERE name IN (
        'VIEW_SALES', 'CREATE_SALE', 'UPDATE_SALE', 'TRANSITION_SALE', 'CANCEL_SALE',
        'CREATE_SALE_PAYMENT', 'VIEW_SALE_PAYMENTS', 'COMPLETE_SALE', 'VIEW_SALE_INVOICE',
        'APPLY_SALE_DISCOUNT', 'OVERRIDE_SALE_PRICE', 'MANAGE_WARRANTY_POLICY', 'VIEW_SALE_HISTORY'
    ) ON CONFLICT DO NOTHING;

    -- ADMIN gets mostly all
    IF admin_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT admin_id, id FROM permissions WHERE name IN (
            'VIEW_SALES', 'CREATE_SALE', 'UPDATE_SALE', 'TRANSITION_SALE', 'CANCEL_SALE',
            'CREATE_SALE_PAYMENT', 'VIEW_SALE_PAYMENTS', 'COMPLETE_SALE', 'VIEW_SALE_INVOICE',
            'APPLY_SALE_DISCOUNT', 'OVERRIDE_SALE_PRICE', 'MANAGE_WARRANTY_POLICY', 'VIEW_SALE_HISTORY'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- SALES_MANAGER gets sales operation permissions
    IF sales_manager_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT sales_manager_id, id FROM permissions WHERE name IN (
            'VIEW_SALES', 'CREATE_SALE', 'UPDATE_SALE', 'TRANSITION_SALE', 'CANCEL_SALE',
            'CREATE_SALE_PAYMENT', 'VIEW_SALE_PAYMENTS', 'COMPLETE_SALE', 'VIEW_SALE_INVOICE',
            'APPLY_SALE_DISCOUNT', 'OVERRIDE_SALE_PRICE', 'VIEW_SALE_HISTORY'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- EMPLOYEE gets basic creation and payment
    IF employee_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT employee_id, id FROM permissions WHERE name IN (
            'VIEW_SALES', 'CREATE_SALE', 'UPDATE_SALE', 'TRANSITION_SALE', 
            'CREATE_SALE_PAYMENT', 'VIEW_SALE_PAYMENTS', 'COMPLETE_SALE', 'VIEW_SALE_INVOICE',
            'APPLY_SALE_DISCOUNT', 'VIEW_SALE_HISTORY'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- ACCOUNTANT gets visibility into payments
    IF accountant_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT accountant_id, id FROM permissions WHERE name IN (
            'VIEW_SALES', 'VIEW_SALE_PAYMENTS', 'VIEW_SALE_INVOICE'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- VIEWER gets read only
    IF viewer_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT viewer_id, id FROM permissions WHERE name IN (
            'VIEW_SALES', 'VIEW_SALE_PAYMENTS', 'VIEW_SALE_INVOICE', 'VIEW_SALE_HISTORY'
        ) ON CONFLICT DO NOTHING;
    END IF;
END $$;
