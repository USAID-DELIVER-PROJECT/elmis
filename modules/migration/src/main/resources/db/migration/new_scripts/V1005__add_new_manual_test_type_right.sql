DELETE FROM Rights where name='MANAGE_MANUAL_TEST_TYPES';
INSERT INTO Rights values('MANAGE_MANUAL_TEST_TYPES', 'ADMIN', 'Permission to access manual test type', NOW(), null, 'right.manual.test.type');
