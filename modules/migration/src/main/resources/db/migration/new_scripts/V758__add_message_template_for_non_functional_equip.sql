DELETE FROM configuration_settings where key = 'NON_FUNCTIONAL_EQUIPMENTS_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('NON_FUNCTIONAL_EQUIPMENTS_EMAIL_MESSAGE_TEMPLATE', 'Non Functional Equipments Notification Email template','Notification - Email','This template is used when sending EMAIL notification when there is non-functional equipments','Hi, Please note that there are non functional equipments in your supervised facilities. Thank you.', 'TEXT_AREA', 2);
DELETE FROM configuration_settings where key = 'NON_FUNCTIONAL_EQUIPMENTS_EMAIL_SUBJECT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('NON_FUNCTIONAL_EQUIPMENTS_EMAIL_SUBJECT', 'Non Functional Equipments Notification Email Subject','Notification - Email','This subject is used when sending EMAIL notification when there is non-functional equipments','NON FUNCTIONAL EQUIPMENTS', 'TEXT', 1);


DELETE FROM configuration_settings where key = 'FUNCTIONAL_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('FUNCTIONAL_EQUIPMENT_STATUS_HELP_TEXT', 'Functional help text','Cold Chain','Functional help text','Functional help text', 'TEXT_AREA', 1);

DELETE FROM configuration_settings where key = 'FUNCTIONAL_BUT_NOT_INSTALLED_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('FUNCTIONAL_BUT_NOT_INSTALLED_EQUIPMENT_STATUS_HELP_TEXT', 'Functional but not installed help text','Cold Chain','Functional not installed help text','Functional not installed help text', 'TEXT_AREA', 2);

DELETE FROM configuration_settings where key = 'NON_FUNCTIONAL_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('NON_FUNCTIONAL_EQUIPMENT_STATUS_HELP_TEXT', 'Non function help text','Cold Chain','Non function help text','Non function help text', 'TEXT_AREA', 3);

DELETE FROM configuration_settings where key = 'WAITING_FOR_REPAIR_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('WAITING_FOR_REPAIR_EQUIPMENT_STATUS_HELP_TEXT', 'Waiting for repair help text','Cold Chain','Waiting for repair help text','Waiting for repair help text', 'TEXT_AREA', 4);

DELETE FROM configuration_settings where key = 'WAITING_FOR_SPARE_PARTS_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('WAITING_FOR_SPARE_PARTS_EQUIPMENT_STATUS_HELP_TEXT', 'Waiting for spare parts help text','Cold Chain','Waiting for spare parts help text','Waiting for spare parts help text', 'TEXT_AREA', 5);

DELETE FROM configuration_settings where key = 'OBSOLETE_EQUIPMENT_STATUS_HELP_TEXT';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('OBSOLETE_EQUIPMENT_STATUS_HELP_TEXT', 'Obsolete help text','Cold Chain','Obsolete help text','Obsolete help text', 'TEXT_AREA', 6);