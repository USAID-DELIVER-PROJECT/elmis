delete from configuration_settings where key = 'VIMS_TIMR_INTERGRATION';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('VIMS_TIMR_INTERGRATION','https://ec2-54-187-21-117.us-west-2.compute.amazonaws.com/SVC/HealthFacilityManagement.svc/receiveDelivery',
'TIMR URL TO POST DATA','Used Send Notification to TIMR','VACCINE','TEXT',true);

delete from configuration_settings where key = 'VIMS_TIMR_USERNAME';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('VIMS_TIMR_USERNAME','vimstiis','username','Used for authentication','VACCINE','TEXT',true);

delete from configuration_settings where key = 'VIMS_TIMR_PASSWORD';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('VIMS_TIMR_PASSWORD','Arusha12','Password','Used for authentication','VACCINE','TEXT',true);
