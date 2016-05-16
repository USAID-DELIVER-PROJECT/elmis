DELETE FROM rights where name ='VIEW_ELMIS_DASHBOARD';
INSERT INTO rights (
	NAME,
	righttype,
	description,
	createddate,
	displayorder,
	displaynamekey
)
VALUES
	(
		'VIEW_ELMIS_DASHBOARD',
		'REPORT',
		'Permission to view eLMIS dashboard poc',
		now(),
		NULL,
		'right.report.dashboard'
	);
