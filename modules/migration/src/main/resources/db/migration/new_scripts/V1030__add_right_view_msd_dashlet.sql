DELETE from role_rights where rightname = 'VIEW_MSD_DASHLET_REPORT';
DELETE FROM rights where name = 'VIEW_MSD_DASHLET_REPORT';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('VIEW_MSD_DASHLET_REPORT','REPORT','right.view.msd.dashboard', 26,'Permission to view MSD Dashboard');
