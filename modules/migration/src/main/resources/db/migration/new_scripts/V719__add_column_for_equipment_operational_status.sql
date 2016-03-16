ALTER TABLE equipment_operational_status DROP COLUMN IF EXISTS needSparePart RESTRICT;
ALTER TABLE equipment_operational_status ADD COLUMN needSparePart BOOLEAN NOT NULL DEFAULT false;