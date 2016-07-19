delete from rights where name = 'VIEW_VACCINE_REPORT';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('VIEW_VACCINE_REPORT','REPORT','right.view.vaccine.report',20,'Permission to view vaccine report');