-- =====================================================================
-- V15: Fix permissions referenced by controllers but never created,
--      and assign a default branch to branchless users.
--
-- Problems fixed:
--   1. BranchController uses hasAuthority('VIEW_BRANCHES') but only
--      VIEW_BRANCH existed -> GET /api/v1/branches was ALWAYS 403.
--   2. Settlement/Role controllers reference 8 more permissions that
--      never existed in the permissions table -> always 403.
--   3. Users without a branch (e.g. the bootstrapped 'admin') get a
--      misleading 403 on every branch-scoped endpoint (inventory list,
--      dashboard, reports, ...) because getCurrentBranch() throws.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Missing permissions used in code
-- ---------------------------------------------------------------------
INSERT INTO permissions (name, description)
SELECT v.name, v.description
FROM (VALUES
    ('VIEW_BRANCHES',                'Can view branch list'),
    ('VIEW_PERMISSIONS',             'Can view permission list'),
    ('MANAGE_ROLES',                 'Can create roles and assign permissions'),
    ('VIEW_SETTLEMENTS',             'Can view all settlements'),
    ('VIEW_OWN_SETTLEMENTS',         'Can view own settlements'),
    ('CREATE_SETTLEMENT_PAYMENT',    'Can record settlement payments'),
    ('RAISE_SETTLEMENT_DISPUTE',     'Can raise a settlement dispute'),
    ('RESOLVE_SETTLEMENT_DISPUTE',   'Can resolve a settlement dispute'),
    ('RECONCILE_SETTLEMENT',         'Can reconcile settlements')
) AS v(name, description)
WHERE NOT EXISTS (SELECT 1 FROM permissions p WHERE p.name = v.name);

-- ---------------------------------------------------------------------
-- 2. VIEW_BRANCHES: grant to every role that already had VIEW_BRANCH
-- ---------------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT DISTINCT rp.role_id, p.id
FROM role_permissions rp
JOIN permissions old_p ON old_p.id = rp.permission_id AND old_p.name = 'VIEW_BRANCH'
JOIN permissions p ON p.name = 'VIEW_BRANCHES'
WHERE NOT EXISTS (
    SELECT 1 FROM role_permissions x
    WHERE x.role_id = rp.role_id AND x.permission_id = p.id
);

-- ---------------------------------------------------------------------
-- 3. Settlement + role-management permissions for admin-level roles
-- ---------------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'VIEW_PERMISSIONS', 'MANAGE_ROLES',
    'VIEW_SETTLEMENTS', 'CREATE_SETTLEMENT_PAYMENT',
    'RAISE_SETTLEMENT_DISPUTE', 'RESOLVE_SETTLEMENT_DISPUTE', 'RECONCILE_SETTLEMENT'
)
WHERE r.name IN ('SUPER_ADMIN', 'ADMIN')
AND NOT EXISTS (
    SELECT 1 FROM role_permissions x
    WHERE x.role_id = r.id AND x.permission_id = p.id
);

-- ---------------------------------------------------------------------
-- 4. VIEW_OWN_SETTLEMENTS for staff roles
-- ---------------------------------------------------------------------
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name = 'VIEW_OWN_SETTLEMENTS'
WHERE r.name IN ('SUPER_ADMIN', 'ADMIN', 'EMPLOYEE', 'PURCHASE_MANAGER', 'SALES_MANAGER')
AND NOT EXISTS (
    SELECT 1 FROM role_permissions x
    WHERE x.role_id = r.id AND x.permission_id = p.id
);

-- ---------------------------------------------------------------------
-- 5. Default branch for single-shop deployments (attached to MAIN-001)
-- ---------------------------------------------------------------------
INSERT INTO shops (shop_code, name, status)
SELECT 'MAIN-001', 'Main Shop', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM shops WHERE shop_code = 'MAIN-001');

INSERT INTO branches (name, address, shop_id)
SELECT 'Main Branch', 'Default branch created for single-shop setup', s.id
FROM shops s
WHERE s.shop_code = 'MAIN-001'
  AND NOT EXISTS (SELECT 1 FROM branches);

-- ---------------------------------------------------------------------
-- 6. Employee profiles for users that have none (assign first branch)
-- ---------------------------------------------------------------------
INSERT INTO employee_profiles (user_id, branch_id, first_name, last_name, phone, email)
SELECT u.id, b.id,
       COALESCE(NULLIF(SPLIT_PART(u.username, '@', 1), ''), 'Staff'),
       '-',
       '9' || RIGHT(REPLACE(u.id::text, '-', ''), 14),
       u.username || '@branch.local'
FROM users u
CROSS JOIN (SELECT id FROM branches ORDER BY created_at LIMIT 1) b
WHERE NOT EXISTS (SELECT 1 FROM employee_profiles ep WHERE ep.user_id = u.id);

-- ---------------------------------------------------------------------
-- 7. Existing profiles without a branch -> assign first branch
-- ---------------------------------------------------------------------
UPDATE employee_profiles
SET branch_id = (SELECT id FROM branches ORDER BY created_at LIMIT 1)
WHERE branch_id IS NULL
  AND EXISTS (SELECT 1 FROM branches);
