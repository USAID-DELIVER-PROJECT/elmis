delete from rights where name = 'SYSTEM_DEPLOYMENT_INSTANCE';

insert into configuration_settings (key, value, name, description, groupname, valuetype, isconfigurable)
values ('SYSTEM_DEPLOYMENT_INSTANCE', 'ZAMBIA', 'Deployment instance operator', 'The country in which the system installation is made', 'ADMIN', 'TEXT', true);