-- Create extension for UUID generation if not exists
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: roles
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Table: permissions
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Table: role_permissions
CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Table: users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: user_roles
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Table: branches
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    address TEXT,
    contact_number VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: employee_profiles
CREATE TABLE employee_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    branch_id UUID REFERENCES branches(id) ON DELETE SET NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    designation VARCHAR(100),
    CONSTRAINT fk_employee_user UNIQUE (user_id)
);

-- Indexes
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_employee_branch ON employee_profiles(branch_id);

-- Initial Roles Seeding
INSERT INTO roles (name, description) VALUES
('SUPER_ADMIN', 'Super Administrator with all access'),
('ADMIN', 'Administrator'),
('PURCHASE_MANAGER', 'Purchase Manager'),
('SALES_MANAGER', 'Sales Manager'),
('ACCOUNTANT', 'Accountant'),
('EMPLOYEE', 'Standard Employee'),
('VIEWER', 'Read Only Viewer');

-- Initial Permissions Seeding
INSERT INTO permissions (name, description) VALUES
('VIEW_USERS', 'Can view users'),
('CREATE_USER', 'Can create users'),
('UPDATE_USER', 'Can update users'),
('VIEW_BRANCH', 'Can view branches'),
('CREATE_BRANCH', 'Can create branches'),
('UPDATE_BRANCH', 'Can update branches'),
('VIEW_CUSTOMERS', 'Can view customers'),
('CREATE_PURCHASE', 'Can create purchases'),
('VIEW_PURCHASE', 'Can view purchases'),
('CREATE_SALE', 'Can create sales'),
('VIEW_SALE', 'Can view sales'),
('VIEW_INVENTORY', 'Can view inventory'),
('MANAGE_INVENTORY', 'Can manage inventory'),
('VIEW_REPORTS', 'Can view reports');

-- Assign all permissions to SUPER_ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'SUPER_ADMIN';
