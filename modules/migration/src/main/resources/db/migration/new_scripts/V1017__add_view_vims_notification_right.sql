delete from rights where name = 'VIEW_VIMS_NOTIFICATION';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('VIEW_VIMS_NOTIFICATION','REPORT','right.view.vims.notification', 61,'Permission to view vims dashboard notification');