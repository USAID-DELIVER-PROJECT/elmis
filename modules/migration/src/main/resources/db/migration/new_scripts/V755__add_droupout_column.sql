ALTER TABLE vaccine_inventory_product_configurations DROP COLUMN IF EXISTS dropout RESTRICT;
ALTER TABLE vaccine_inventory_product_configurations ADD COLUMN dropout  numeric(6,3);


