DELETE FROM rights where name = 'CREATE_LOT';
INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
('CREATE_LOT','REQUISITION','right.create.lot','Permission to create lot');

DELETE FROM rights where name = 'OPEN_STOCK_RECEIVING_PROCESS';
INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
('OPEN_STOCK_RECEIVING_PROCESS','REQUISITION','right.open.stock.receiving','Permission to create open stock receiving');