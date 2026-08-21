-- Update the active-device inventory index to include IN_TRANSIT
DROP INDEX IF EXISTS idx_inventory_device_active;

CREATE UNIQUE INDEX idx_inventory_device_active 
ON inventory_items (device_id) 
WHERE status IN ('AVAILABLE', 'RESERVED', 'BLOCKED', 'IN_TRANSIT');
