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
		'VIEW_DASHBOARD',
		'REPORT',
		'Permission to view dashboard poc',
		now(),
		NULL,
		'right.report.dashboard'
	);
