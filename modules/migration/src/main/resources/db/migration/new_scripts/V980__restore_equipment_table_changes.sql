
ALTER TABLE equipment_type_products
  DROP CONSTRAINT if EXISTS program_equipment_products_programequipmentid_fkey;

ALTER TABLE equipment_type_products
  RENAME TO equipment_products;

ALTER TABLE equipment_products
  RENAME COLUMN programequipmenttypeid TO equipmentid;

ALTER table equipment_products
  ADD CONSTRAINT fk_equipment_product_equipmentid FOREIGN KEY (equipmentId) REFERENCES equipments(id);
