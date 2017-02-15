delete from rights where name = 'VIEW_DISTRIBUTION_SUMMARY_REPORT';
INSERT INTO rights (name, rightType, displaynamekey, displayOrder, description) VALUES
('VIEW_DISTRIBUTION_SUMMARY_REPORT','REPORT','right.view.distribution.summary.report', 22,'Permission to view Distribution summary report');