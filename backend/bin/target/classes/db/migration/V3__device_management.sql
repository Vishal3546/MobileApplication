-- Table: devices
CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    imei1 VARCHAR(50) NOT NULL UNIQUE,
    imei2 VARCHAR(50),
    serial_number VARCHAR(100),
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    variant VARCHAR(100),
    color VARCHAR(100),
    storage_gb INTEGER,
    ram_gb INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, BLOCKED
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_devices_imei2_unique ON devices (imei2) WHERE imei2 IS NOT NULL;
CREATE UNIQUE INDEX idx_devices_serial_number_unique ON devices (serial_number) WHERE serial_number IS NOT NULL;
CREATE INDEX idx_devices_brand ON devices(brand);
CREATE INDEX idx_devices_model ON devices(model);
CREATE INDEX idx_devices_status ON devices(status);

-- Table: device_conditions
CREATE TABLE device_conditions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    battery_health INTEGER, -- 0-100
    display_condition VARCHAR(50),
    body_condition VARCHAR(50),
    camera_condition VARCHAR(50),
    speaker_condition VARCHAR(50),
    microphone_condition VARCHAR(50),
    charging_condition VARCHAR(50),
    biometric_status VARCHAR(50),
    network_lock VARCHAR(50),
    has_original_bill BOOLEAN DEFAULT FALSE,
    has_box BOOLEAN DEFAULT FALSE,
    has_charger BOOLEAN DEFAULT FALSE,
    accessories TEXT,
    notes TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_conditions_device_id ON device_conditions(device_id);
CREATE INDEX idx_device_conditions_created_at ON device_conditions(created_at);

-- Table: device_inspections
CREATE TABLE device_inspections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    display_test VARCHAR(20), -- PASS, FAIL, NOT_TESTED, NOT_APPLICABLE
    touch_test VARCHAR(20),
    camera_test VARCHAR(20),
    speaker_test VARCHAR(20),
    microphone_test VARCHAR(20),
    charging_test VARCHAR(20),
    wifi_test VARCHAR(20),
    bluetooth_test VARCHAR(20),
    sim_test VARCHAR(20),
    fingerprint_test VARCHAR(20),
    face_id_test VARCHAR(20),
    battery_test VARCHAR(20),
    flash_test VARCHAR(20),
    vibration_test VARCHAR(20),
    network_test VARCHAR(20),
    notes TEXT,
    final_status VARCHAR(20) NOT NULL, -- PASS, FAIL, PARTIAL
    inspected_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_inspections_device_id ON device_inspections(device_id);
CREATE INDEX idx_device_inspections_created_at ON device_inspections(created_at);

-- Table: device_media
CREATE TABLE device_media (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    media_id UUID NOT NULL REFERENCES media_files(id) ON DELETE RESTRICT,
    view_type VARCHAR(50) NOT NULL, -- FRONT, BACK, LEFT, RIGHT, TOP, BOTTOM, SCREEN_ON, SCREEN_OFF, IMEI_SCREEN, DAMAGE, OTHER
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_media_device_id ON device_media(device_id);

-- Table: device_lifecycle_history
CREATE TABLE device_lifecycle_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL, -- DEVICE_CREATED, DEVICE_UPDATED, IMEI_VERIFIED, CONDITION_RECORDED, INSPECTION_CREATED, MEDIA_ADDED, MEDIA_REMOVED, DEVICE_BLOCKED, DEVICE_UNBLOCKED
    event_reference VARCHAR(255),
    description TEXT,
    performed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    branch_id UUID REFERENCES branches(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_lifecycle_history_device_id ON device_lifecycle_history(device_id);
CREATE INDEX idx_device_lifecycle_history_created_at ON device_lifecycle_history(created_at);

-- Insert Permissions
INSERT INTO permissions (name, description) VALUES
('VIEW_DEVICES', 'Can view devices'),
('CREATE_DEVICE', 'Can create a new device'),
('UPDATE_DEVICE', 'Can update device details'),
('VIEW_DEVICE_IMEI', 'Can view device IMEI (masked or unmasked based on other permissions)'),
('VIEW_FULL_DEVICE_IMEI', 'Can view full unmasked device IMEI'),
('MANAGE_DEVICE_CONDITION', 'Can add device conditions'),
('VIEW_DEVICE_INSPECTION', 'Can view device inspections'),
('CREATE_DEVICE_INSPECTION', 'Can create device inspections'),
('UPLOAD_DEVICE_MEDIA', 'Can upload device media'),
('VIEW_DEVICE_MEDIA', 'Can view device media'),
('VIEW_DEVICE_LIFECYCLE', 'Can view device lifecycle history'),
('BLOCK_DEVICE', 'Can block or unblock devices');

-- Assign new permissions to SUPER_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPER_ADMIN' 
AND p.name IN (
    'VIEW_DEVICES', 'CREATE_DEVICE', 'UPDATE_DEVICE', 
    'VIEW_DEVICE_IMEI', 'VIEW_FULL_DEVICE_IMEI', 'MANAGE_DEVICE_CONDITION',
    'VIEW_DEVICE_INSPECTION', 'CREATE_DEVICE_INSPECTION', 
    'UPLOAD_DEVICE_MEDIA', 'VIEW_DEVICE_MEDIA', 
    'VIEW_DEVICE_LIFECYCLE', 'BLOCK_DEVICE'
);

-- Assign some permissions to ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' 
AND p.name IN (
    'VIEW_DEVICES', 'CREATE_DEVICE', 'UPDATE_DEVICE', 
    'VIEW_DEVICE_IMEI', 'VIEW_FULL_DEVICE_IMEI', 'MANAGE_DEVICE_CONDITION',
    'VIEW_DEVICE_INSPECTION', 'CREATE_DEVICE_INSPECTION', 
    'UPLOAD_DEVICE_MEDIA', 'VIEW_DEVICE_MEDIA', 
    'VIEW_DEVICE_LIFECYCLE', 'BLOCK_DEVICE'
);

-- Assign basic permissions to PURCHASE_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'PURCHASE_MANAGER' 
AND p.name IN (
    'VIEW_DEVICES', 'CREATE_DEVICE', 'UPDATE_DEVICE', 
    'VIEW_DEVICE_IMEI', 'VIEW_FULL_DEVICE_IMEI', 'MANAGE_DEVICE_CONDITION',
    'VIEW_DEVICE_INSPECTION', 'CREATE_DEVICE_INSPECTION', 
    'UPLOAD_DEVICE_MEDIA', 'VIEW_DEVICE_MEDIA', 
    'VIEW_DEVICE_LIFECYCLE'
);
