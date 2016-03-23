delete from rights where name = 'INITIALIZE_STOCK';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('INITIALIZE_STOCK','REQUISITION','right.initialize.stock', 19,'Permission to initialize stock');
