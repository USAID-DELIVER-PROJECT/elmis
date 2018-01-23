delete from rights where name = 'VIEW_VIMS_MAIN_DASHBOARD';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('VIEW_VIMS_MAIN_DASHBOARD','REPORT','right.view.vims.main.dashboard', 25,'Permission to view Vims Main Dashboard');