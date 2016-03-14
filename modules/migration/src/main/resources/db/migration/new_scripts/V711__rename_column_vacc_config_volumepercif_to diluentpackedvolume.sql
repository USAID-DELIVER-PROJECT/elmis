ALTER TABLE vaccine_inventory_product_configurations RENAME volumepercif  TO diluentpackedvolumeperdose;
ALTER TABLE vaccine_inventory_product_configurations
   ALTER COLUMN packedvolumeperdose TYPE numeric(5,2);
ALTER TABLE vaccine_inventory_product_configurations
   ALTER COLUMN diluentpackedvolumeperdose TYPE numeric(5,2);