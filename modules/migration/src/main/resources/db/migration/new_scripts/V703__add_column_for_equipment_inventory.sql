ALTER TABLE equipment_inventories DROP COLUMN IF EXISTS nameOfSparePart RESTRICT;
ALTER TABLE equipment_inventories ADD COLUMN nameOfSparePart varchar(250);