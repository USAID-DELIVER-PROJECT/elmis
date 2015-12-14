delete from configuration_settings where key = 'VCP_GREEN';
delete from configuration_settings where key = 'VCP_BLUE';
delete from configuration_settings where key = 'VCP_RED';
delete from configuration_settings where key = 'VCP_ORANGE';

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_GREEN','#52C552','Color used for minimum values','Color used for minimum values','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_BLUE','#dce6f1','Blue Color Used for Average VAlues','Color used for average values','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_RED','#ff0000','Red Color for max values','color coding used for max valudes','VACCINE','TEXT',true);
insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_ORANGE','#E4E44A','Red Color for max values','color coding used for max valudes','VACCINE','TEXT',true);

