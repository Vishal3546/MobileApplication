-- V11__shop_network_foundation.sql

-- 1. Create shops table
CREATE TABLE shops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    legal_name VARCHAR(150),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    owner_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shops_status ON shops(status);
CREATE INDEX idx_shops_shop_code ON shops(shop_code);

-- 2. Create shop_memberships table
CREATE TABLE shop_memberships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shop_id, user_id)
);

CREATE INDEX idx_shop_memberships_shop_id ON shop_memberships(shop_id);
CREATE INDEX idx_shop_memberships_user_id ON shop_memberships(user_id);

-- 3. Modify branches table
ALTER TABLE branches ADD COLUMN shop_id UUID;

-- 4. Data Migration: Create default shop and assign existing branches
DO $$
DECLARE
    main_shop_id UUID;
BEGIN
    -- Only create if not exists (defensive)
    IF NOT EXISTS (SELECT 1 FROM shops WHERE shop_code = 'MAIN-001') THEN
        INSERT INTO shops (shop_code, name, status)
        VALUES ('MAIN-001', 'Main Shop', 'ACTIVE')
        RETURNING id INTO main_shop_id;

        -- Update all existing branches to belong to the main shop
        UPDATE branches SET shop_id = main_shop_id WHERE shop_id IS NULL;
    END IF;
END $$;

-- Make shop_id NOT NULL after data migration
ALTER TABLE branches ALTER COLUMN shop_id SET NOT NULL;
ALTER TABLE branches ADD CONSTRAINT fk_branches_shop FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE;

CREATE INDEX idx_branches_shop_id ON branches(shop_id);

-- 5. Modify inventory_items table
ALTER TABLE inventory_items ADD COLUMN visibility VARCHAR(50) NOT NULL DEFAULT 'PRIVATE';
CREATE INDEX idx_inventory_items_visibility ON inventory_items(visibility);

-- 6. Modify stock_transfers table
ALTER TABLE stock_transfers ADD COLUMN transfer_type VARCHAR(50) NOT NULL DEFAULT 'INTERNAL';
CREATE INDEX idx_stock_transfers_type ON stock_transfers(transfer_type);

-- 7. Add new Permissions for Shop Network
INSERT INTO permissions (name, description) VALUES
('VIEW_SHOPS', 'Can view list of shops (Super Admin)'),
('CREATE_SHOP', 'Can create a new shop'),
('UPDATE_SHOP', 'Can update shop details'),
('APPROVE_SHOP', 'Can approve shop onboarding (Super Admin)'),
('SUSPEND_SHOP', 'Can suspend a shop (Super Admin)'),
('VIEW_NETWORK_INVENTORY', 'Can search and view network inventory'),
('REQUEST_STOCK_TRANSFER', 'Can initiate cross-shop stock requests'),
('APPROVE_STOCK_TRANSFER', 'Can approve cross-shop stock requests'),
('VIEW_NETWORK_TRANSFERS', 'Can view cross-shop transfers'),
('MANAGE_SHOP_USERS', 'Can manage users within a shop'),
('VIEW_SHOP_USERS', 'Can view users within a shop')
ON CONFLICT (name) DO NOTHING;

-- 8. Assign new permissions to SUPER_ADMIN
DO $$
DECLARE
    super_admin_id UUID;
    admin_id UUID;
BEGIN
    SELECT id INTO super_admin_id FROM roles WHERE name = 'SUPER_ADMIN';
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';

    -- SUPER_ADMIN gets all new permissions
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT super_admin_id, id FROM permissions WHERE name IN (
        'VIEW_SHOPS', 'CREATE_SHOP', 'UPDATE_SHOP', 'APPROVE_SHOP', 'SUSPEND_SHOP',
        'VIEW_NETWORK_INVENTORY', 'REQUEST_STOCK_TRANSFER', 'APPROVE_STOCK_TRANSFER',
        'VIEW_NETWORK_TRANSFERS', 'MANAGE_SHOP_USERS', 'VIEW_SHOP_USERS'
    ) ON CONFLICT DO NOTHING;

    -- ADMIN gets shop management permissions (managing their own shop & users)
    IF admin_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT admin_id, id FROM permissions WHERE name IN (
            'UPDATE_SHOP', 'VIEW_NETWORK_INVENTORY', 'REQUEST_STOCK_TRANSFER',
            'APPROVE_STOCK_TRANSFER', 'VIEW_NETWORK_TRANSFERS', 'MANAGE_SHOP_USERS', 'VIEW_SHOP_USERS'
        ) ON CONFLICT DO NOTHING;
    END IF;
END $$;
