delete from configuration_settings where key = 'VCP_GREATER_THAN_NINTY_PERCENT_COLOR';
delete from configuration_settings where key = 'VCP_GREATER_THAN_OR_EQUAL_EIGHTY_PERCENT_COLOR';
delete from configuration_settings where key = 'VCP_GREATER_THAN_OR_EQUAL_FIFTY_PERCENT_COLOR';
delete from configuration_settings where key = 'VCP_LESS_THAN_FIFTY_PERCENT_COLOR';
delete from configuration_settings where key = 'VACCINE_LATE_REPORTING_DAYS';

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_GREATER_THAN_NINTY_PERCENT_COLOR','#52C552','Color coding for vaccine coverage > 90%','color coding used for vaccine coverage report','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_GREATER_THAN_OR_EQUAL_EIGHTY_PERCENT_COLOR','#B0E8F7','Color coding for vaccine coverage >= 80%','color coding used for vaccine coverage report','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_GREATER_THAN_OR_EQUAL_FIFTY_PERCENT_COLOR','#E4E44A','Colort coding for vaccine coverage >= 50%','color coding used for vaccine coverage report','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_LESS_THAN_FIFTY_PERCENT_COLOR','#F15D5D','Colort coding for vaccine coverage < 50%','color coding used for vaccine coverage report','VACCINE','TEXT',true);

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VACCINE_LATE_REPORTING_DAYS',10,'vaccine late reporting days','vaccine late reporting days','VACCINE','TEXT',true);