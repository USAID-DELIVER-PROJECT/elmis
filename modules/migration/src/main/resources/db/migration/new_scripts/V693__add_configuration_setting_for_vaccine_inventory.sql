delete from configuration_settings where key = 'VACCINE_FACILITY_TYPE_CODE';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('VACCINE_FACILITY_TYPE_CODE','rvs','Vaccine Facility Type Code','Used to compare the region level of Vaccine Store','VACCINE','TEXT',true);
