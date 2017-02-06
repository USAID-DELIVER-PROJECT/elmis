delete from rights where name = 'USE_BARCODE_FEATURE';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('USE_BARCODE_FEATURE','REQUISITION','right.use.barcode.feature', 20,'Permission to use barcode functionality');