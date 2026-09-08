-- Reporting Permissions
INSERT INTO permissions (name, description) VALUES
('VIEW_DASHBOARD', 'Can view the main dashboard metrics'),
('VIEW_SALES_REPORT', 'Can view sales reports'),
('VIEW_PURCHASE_REPORT', 'Can view purchase reports'),
('VIEW_INVENTORY_REPORT', 'Can view inventory and stock reports'),
('VIEW_PROFIT_REPORT', 'Can view gross profit and margin reports'),
('VIEW_PAYMENT_REPORT', 'Can view payment summaries'),
('VIEW_BRANCH_REPORT', 'Can view branch performance reports'),
('VIEW_EMPLOYEE_REPORT', 'Can view employee performance reports'),
('VIEW_ANALYTICS', 'Can view advanced analytics (trends, brands, models)'),
('EXPORT_REPORT', 'Can export reports to CSV/XLSX')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to roles
DO $$
DECLARE
    super_admin_id UUID;
    admin_id UUID;
    sales_manager_id UUID;
    purchase_manager_id UUID;
    accountant_id UUID;
    employee_id UUID;
    viewer_id UUID;
BEGIN
    SELECT id INTO super_admin_id FROM roles WHERE name = 'SUPER_ADMIN';
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO sales_manager_id FROM roles WHERE name = 'SALES_MANAGER';
    SELECT id INTO purchase_manager_id FROM roles WHERE name = 'PURCHASE_MANAGER';
    SELECT id INTO accountant_id FROM roles WHERE name = 'ACCOUNTANT';
    SELECT id INTO employee_id FROM roles WHERE name = 'EMPLOYEE';
    SELECT id INTO viewer_id FROM roles WHERE name = 'VIEWER';

    -- SUPER_ADMIN gets all reporting permissions
    INSERT INTO role_permissions (role_id, permission_id)
    SELECT super_admin_id, id FROM permissions WHERE name IN (
        'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_PURCHASE_REPORT', 
        'VIEW_INVENTORY_REPORT', 'VIEW_PROFIT_REPORT', 'VIEW_PAYMENT_REPORT', 
        'VIEW_BRANCH_REPORT', 'VIEW_EMPLOYEE_REPORT', 'VIEW_ANALYTICS', 'EXPORT_REPORT'
    ) ON CONFLICT DO NOTHING;

    -- ADMIN gets all reporting permissions
    IF admin_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT admin_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_PURCHASE_REPORT', 
            'VIEW_INVENTORY_REPORT', 'VIEW_PROFIT_REPORT', 'VIEW_PAYMENT_REPORT', 
            'VIEW_BRANCH_REPORT', 'VIEW_EMPLOYEE_REPORT', 'VIEW_ANALYTICS', 'EXPORT_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- SALES_MANAGER gets sales, inventory, profit, analytics, export
    IF sales_manager_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT sales_manager_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_INVENTORY_REPORT', 
            'VIEW_PROFIT_REPORT', 'VIEW_ANALYTICS', 'EXPORT_REPORT', 'VIEW_EMPLOYEE_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- PURCHASE_MANAGER gets purchase, inventory, analytics, export
    IF purchase_manager_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT purchase_manager_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_PURCHASE_REPORT', 'VIEW_INVENTORY_REPORT', 
            'VIEW_ANALYTICS', 'EXPORT_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- ACCOUNTANT gets payments, sales, purchase, profit, export
    IF accountant_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT accountant_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_PURCHASE_REPORT',
            'VIEW_PROFIT_REPORT', 'VIEW_PAYMENT_REPORT', 'EXPORT_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- EMPLOYEE gets basic dashboard, basic reports, NO export, NO profit, NO employee report
    IF employee_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT employee_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_INVENTORY_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

    -- VIEWER gets basic dashboard, basic reports, NO export, NO profit, NO employee report
    IF viewer_id IS NOT NULL THEN
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT viewer_id, id FROM permissions WHERE name IN (
            'VIEW_DASHBOARD', 'VIEW_SALES_REPORT', 'VIEW_PURCHASE_REPORT', 'VIEW_INVENTORY_REPORT'
        ) ON CONFLICT DO NOTHING;
    END IF;

END $$;

-- Add Reporting Indexes for frequently queried aggregations
CREATE INDEX IF NOT EXISTS idx_sale_transactions_created_status ON sale_transactions (created_at, sale_status);
CREATE INDEX IF NOT EXISTS idx_purchase_transactions_created_status ON purchase_transactions (created_at, transaction_status);
CREATE INDEX IF NOT EXISTS idx_sale_transactions_branch_created ON sale_transactions (branch_id, created_at);
CREATE INDEX IF NOT EXISTS idx_purchase_transactions_branch_created ON purchase_transactions (branch_id, created_at);
CREATE INDEX IF NOT EXISTS idx_inventory_items_status ON inventory_items (status);
