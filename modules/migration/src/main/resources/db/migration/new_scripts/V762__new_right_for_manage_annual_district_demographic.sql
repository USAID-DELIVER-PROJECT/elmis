DELETE FROM rights WHERE name = 'MANAGE_DISTRICT_DEMOGRAPHIC_ESTIMATES';

INSERT INTO rights (name, rightType, displaynamekey, description, displayOrder) VALUES
  ('MANAGE_DISTRICT_DEMOGRAPHIC_ESTIMATES','REQUISITION','right.manage.district.demographic.estimates','Permission to manage district demographic estimates', 30);