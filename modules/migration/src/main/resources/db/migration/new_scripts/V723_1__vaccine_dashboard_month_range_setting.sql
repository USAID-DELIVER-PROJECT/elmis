delete from configuration_settings where key = 'VCP_DASHBOARD_MONTHS_RANGE';

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_DASHBOARD_MONTHS_RANGE',6,'Months range to be used for monthly dashlets','Months range to be used for monthly dashlets','VACCINE','NUMBER',true);