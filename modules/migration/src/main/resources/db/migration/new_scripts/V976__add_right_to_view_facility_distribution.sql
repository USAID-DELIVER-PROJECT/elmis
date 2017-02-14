delete from rights where name = 'MANAGE_FACILITY_DISTRIBUTION';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('MANAGE_FACILITY_DISTRIBUTION','REQUISITION','right.manage.facility.distribution', 21,'Permission to manage facility Distribution');