DELETE FROM rights WHERE name = 'DELETE_EQUIPMENT_INVENTORY';

INSERT INTO rights (name, rightType, displaynamekey, description, displayOrder) VALUES
  ('DELETE_EQUIPMENT_INVENTORY','ADMIN','right.delete.equipment.inventory','Permission to delete equipment inventory', 30);