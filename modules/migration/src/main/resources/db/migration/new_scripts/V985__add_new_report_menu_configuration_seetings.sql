DELETE FROM configuration_settings WHERE key = 'USE_NEW_REPORT_MENU';

INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype, displayOrder)
values('USE_NEW_REPORT_MENU',false,'Use page based report navigation menu','Use page based report navigation menu.','GENERAL','BOOLEAN', 60);