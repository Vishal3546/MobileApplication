-- Inventory Stock Code Sequence
CREATE SEQUENCE inventory_stock_code_seq START 1;

-- Stock Transfer Number Sequence
CREATE SEQUENCE stock_transfer_number_seq START 1;

-- Table: inventory_items
CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stock_code VARCHAR(100) NOT NULL UNIQUE,
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE RESTRICT,
    purchase_transaction_id UUID NOT NULL REFERENCES purchase_transactions(id) ON DELETE RESTRICT,
    branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    cost_price DECIMAL(15,2) NOT NULL,
    selling_price DECIMAL(15,2),
    reserved_until TIMESTAMP WITH TIME ZONE,
    reserved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    condition_summary TEXT,
    notes TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Active device inventory uniqueness
CREATE UNIQUE INDEX idx_inventory_active_device ON inventory_items(device_id) WHERE status NOT IN ('SOLD', 'RETURNED', 'DAMAGED');

-- Purchase idempotency uniqueness
CREATE UNIQUE INDEX idx_inventory_purchase_unique ON inventory_items(purchase_transaction_id);

CREATE INDEX idx_inventory_items_stock_code ON inventory_items(stock_code);
CREATE INDEX idx_inventory_items_branch_id ON inventory_items(branch_id);
CREATE INDEX idx_inventory_items_status ON inventory_items(status);
CREATE INDEX idx_inventory_items_created_at ON inventory_items(created_at);

-- Table: inventory_status_history
CREATE TABLE inventory_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    reason TEXT,
    reference_type VARCHAR(50),
    reference_id UUID,
    performed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    branch_id UUID REFERENCES branches(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_status_history_item ON inventory_status_history(inventory_item_id);

-- Table: stock_transfers
CREATE TABLE stock_transfers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transfer_number VARCHAR(100) NOT NULL UNIQUE,
    from_branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE RESTRICT,
    to_branch_id UUID NOT NULL REFERENCES branches(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    requested_by UUID REFERENCES users(id) ON DELETE SET NULL,
    approved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    requested_at TIMESTAMP WITH TIME ZONE,
    approved_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_different_branches CHECK (from_branch_id != to_branch_id)
);

CREATE INDEX idx_stock_transfers_from_branch ON stock_transfers(from_branch_id);
CREATE INDEX idx_stock_transfers_to_branch ON stock_transfers(to_branch_id);
CREATE INDEX idx_stock_transfers_status ON stock_transfers(status);

-- Table: stock_transfer_items
CREATE TABLE stock_transfer_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stock_transfer_id UUID NOT NULL REFERENCES stock_transfers(id) ON DELETE CASCADE,
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id) ON DELETE RESTRICT,
    UNIQUE(stock_transfer_id, inventory_item_id)
);

CREATE INDEX idx_stock_transfer_items_transfer ON stock_transfer_items(stock_transfer_id);
CREATE INDEX idx_stock_transfer_items_inventory ON stock_transfer_items(inventory_item_id);

-- Permissions
INSERT INTO permissions (name, description) VALUES
('VIEW_INVENTORY_HISTORY', 'Can view inventory history'),
('CHANGE_INVENTORY_STATUS', 'Can change inventory status manually'),
('CREATE_INVENTORY', 'Can manually create inventory (Admin)'),
('UPDATE_INVENTORY', 'Can update inventory notes/condition'),
('RESERVE_INVENTORY', 'Can reserve inventory'),
('RELEASE_INVENTORY', 'Can release inventory reservation'),
('CREATE_STOCK_TRANSFER', 'Can create stock transfer'),
('VIEW_STOCK_TRANSFER', 'Can view stock transfers'),
('APPROVE_STOCK_TRANSFER', 'Can approve stock transfers'),
('COMPLETE_STOCK_TRANSFER', 'Can complete stock transfers'),
('CANCEL_STOCK_TRANSFER', 'Can cancel stock transfers'),
('UPDATE_SELLING_PRICE', 'Can update inventory selling price'),
('VIEW_INVENTORY_SUMMARY', 'Can view inventory summary');

-- Assign to SUPER_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPER_ADMIN' 
AND p.name IN (
    'VIEW_INVENTORY_HISTORY', 'CHANGE_INVENTORY_STATUS', 
    'CREATE_INVENTORY', 'UPDATE_INVENTORY', 'RESERVE_INVENTORY', 
    'RELEASE_INVENTORY', 'CREATE_STOCK_TRANSFER', 'VIEW_STOCK_TRANSFER', 
    'APPROVE_STOCK_TRANSFER', 'COMPLETE_STOCK_TRANSFER', 'CANCEL_STOCK_TRANSFER', 
    'UPDATE_SELLING_PRICE', 'VIEW_INVENTORY_SUMMARY'
);

-- Assign to ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' 
AND p.name IN (
    'VIEW_INVENTORY', 'VIEW_INVENTORY_HISTORY', 'CHANGE_INVENTORY_STATUS', 
    'UPDATE_INVENTORY', 'RESERVE_INVENTORY', 
    'RELEASE_INVENTORY', 'CREATE_STOCK_TRANSFER', 'VIEW_STOCK_TRANSFER', 
    'APPROVE_STOCK_TRANSFER', 'COMPLETE_STOCK_TRANSFER', 'CANCEL_STOCK_TRANSFER', 
    'UPDATE_SELLING_PRICE', 'VIEW_INVENTORY_SUMMARY'
);

-- Assign to SALES_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SALES_MANAGER' 
AND p.name IN (
    'VIEW_INVENTORY', 'VIEW_INVENTORY_HISTORY', 'UPDATE_INVENTORY', 'RESERVE_INVENTORY', 
    'RELEASE_INVENTORY', 'CREATE_STOCK_TRANSFER', 'VIEW_STOCK_TRANSFER', 
    'APPROVE_STOCK_TRANSFER', 'COMPLETE_STOCK_TRANSFER', 'CANCEL_STOCK_TRANSFER', 
    'UPDATE_SELLING_PRICE', 'VIEW_INVENTORY_SUMMARY'
);

-- Assign to EMPLOYEE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'EMPLOYEE' 
AND p.name IN (
    'VIEW_INVENTORY', 'RESERVE_INVENTORY', 'VIEW_STOCK_TRANSFER', 'CREATE_STOCK_TRANSFER', 'COMPLETE_STOCK_TRANSFER'
);
