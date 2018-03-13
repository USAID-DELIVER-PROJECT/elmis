
delete from configuration_settings where key = 'MSD_OVERSTOCKED_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('MSD_OVERSTOCKED_COLOR','#dce6f1','If the value is greater than 9 for HQ and 3 for Zone ','If the value is greater than 9 for HQ and 3 for Zone ','GENERAL','TEXT',true);

delete from configuration_settings where key = 'MSD_ADEQUATELY_STOCKED_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('MSD_ADEQUATELY_STOCKED_COLOR','#006600','Green color for MOS > 7 < 9(hq) and mos>2 <3 for Zone','If the value is greater than 7 for HQ and 2 for zone and less than 9 for HQ and 3 for zones','GENERAL','TEXT',true);

delete from configuration_settings where key = 'MSD_INADEQUATELY_STOCKED_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('MSD_INADEQUATELY_STOCKED_COLOR','#ffdb00','Yellow color for value < 7 fOR hq AND 2 FOR ZONE ','Graph color when stock is less than re-order level','GENERAL','TEXT',true);

delete from configuration_settings where key = 'MSD_STOCKED_OUT_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('MSD_STOCKED_OUT_COLOR','#ff0d00','Red color IF MOS=0','MSD Color when stock is equal to zero','GENERAL','TEXT',true);

