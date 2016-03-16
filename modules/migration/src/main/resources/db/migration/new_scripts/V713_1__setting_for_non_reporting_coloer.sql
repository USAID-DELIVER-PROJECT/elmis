delete from configuration_settings where key = 'VCP_NON_REPORTING';

insert into configuration_settings(key, value, name, description, groupname, valuetype, isconfigurable)
values('VCP_NON_REPORTING','#527756','Color used for non reporting values','Color used for non reporting values','VACCINE','TEXT',true);