-- V2__customer_kyc_media.sql

CREATE TABLE media_files (
    id UUID PRIMARY KEY,
    storage_provider VARCHAR(50) NOT NULL,
    bucket VARCHAR(100) NOT NULL,
    object_key VARCHAR(255) NOT NULL UNIQUE,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    checksum VARCHAR(255),
    original_file_name VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID,
    updated_by UUID
);

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL REFERENCES branches(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    alt_phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    verification_status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_branch_id ON customers(branch_id);

CREATE TABLE customer_documents (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    id_type VARCHAR(50) NOT NULL,
    id_number_encrypted TEXT NOT NULL,
    id_number_hash VARCHAR(64) NOT NULL,
    id_number_masked VARCHAR(50) NOT NULL,
    front_media_id UUID REFERENCES media_files(id),
    back_media_id UUID REFERENCES media_files(id),
    photo_media_id UUID REFERENCES media_files(id),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verification_notes TEXT,
    verified_at TIMESTAMP WITHOUT TIME ZONE,
    verified_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_customer_documents_customer_id ON customer_documents(customer_id);
CREATE INDEX idx_customer_documents_hash ON customer_documents(id_number_hash);

CREATE TABLE customer_consents (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    consent_type VARCHAR(50) NOT NULL,
    consent_text_version VARCHAR(50) NOT NULL,
    signature_media_id UUID REFERENCES media_files(id),
    video_media_id UUID REFERENCES media_files(id),
    ip_address VARCHAR(45),
    device_info VARCHAR(255),
    captured_by UUID NOT NULL REFERENCES users(id),
    captured_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_customer_consents_customer_id ON customer_consents(customer_id);

-- PERMISSIONS
INSERT INTO permissions (id, name, description) VALUES 
(gen_random_uuid(), 'CREATE_CUSTOMER', 'Create a new customer'),
(gen_random_uuid(), 'UPDATE_CUSTOMER', 'Update customer details'),
(gen_random_uuid(), 'MANAGE_CUSTOMER_STATUS', 'Block or unblock customers'),
(gen_random_uuid(), 'VIEW_KYC', 'View KYC documents (masked)'),
(gen_random_uuid(), 'UPLOAD_KYC', 'Upload customer KYC documents'),
(gen_random_uuid(), 'VERIFY_KYC', 'Approve KYC documents and view unmasked data'),
(gen_random_uuid(), 'REJECT_KYC', 'Reject KYC documents'),
(gen_random_uuid(), 'VIEW_CUSTOMER_CONSENT', 'View customer consent records'),
(gen_random_uuid(), 'UPLOAD_MEDIA', 'Upload media files'),
(gen_random_uuid(), 'VIEW_MEDIA', 'View secure media files'),
(gen_random_uuid(), 'MANAGE_MEDIA', 'Delete or modify media metadata');

-- ASSIGN SUPER_ADMIN (All permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN' AND p.name IN (
    'CREATE_CUSTOMER', 'UPDATE_CUSTOMER', 'MANAGE_CUSTOMER_STATUS',
    'VIEW_KYC', 'UPLOAD_KYC', 'VERIFY_KYC', 'REJECT_KYC',
    'VIEW_CUSTOMER_CONSENT', 'UPLOAD_MEDIA', 'VIEW_MEDIA', 'MANAGE_MEDIA'
);

-- ASSIGN ADMIN (All permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name IN (
    'VIEW_CUSTOMERS', 'CREATE_CUSTOMER', 'UPDATE_CUSTOMER', 'MANAGE_CUSTOMER_STATUS',
    'VIEW_KYC', 'UPLOAD_KYC', 'VERIFY_KYC', 'REJECT_KYC',
    'VIEW_CUSTOMER_CONSENT', 'UPLOAD_MEDIA', 'VIEW_MEDIA', 'MANAGE_MEDIA'
);

-- ASSIGN PURCHASE_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'PURCHASE_MANAGER' AND p.name IN (
    'VIEW_CUSTOMERS', 'CREATE_CUSTOMER', 'UPDATE_CUSTOMER',
    'VIEW_KYC', 'UPLOAD_KYC', 'VERIFY_KYC', 'REJECT_KYC',
    'VIEW_CUSTOMER_CONSENT', 'VIEW_MEDIA', 'UPLOAD_MEDIA'
);

-- ASSIGN SALES_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SALES_MANAGER' AND p.name IN (
    'VIEW_CUSTOMERS', 'CREATE_CUSTOMER', 'UPDATE_CUSTOMER',
    'VIEW_KYC', 'VIEW_MEDIA'
);

-- ASSIGN EMPLOYEE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'EMPLOYEE' AND p.name IN (
    'VIEW_CUSTOMERS', 'CREATE_CUSTOMER', 'UPDATE_CUSTOMER',
    'UPLOAD_KYC', 'VIEW_KYC', 'VIEW_CUSTOMER_CONSENT', 'UPLOAD_MEDIA'
);

-- ASSIGN ACCOUNTANT
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ACCOUNTANT' AND p.name IN (
    'VIEW_CUSTOMERS'
);

-- ASSIGN VIEWER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'VIEWER' AND p.name IN (
    'VIEW_CUSTOMERS'
);
