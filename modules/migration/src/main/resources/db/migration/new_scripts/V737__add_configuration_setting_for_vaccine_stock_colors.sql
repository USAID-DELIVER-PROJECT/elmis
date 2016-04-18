delete from configuration_settings where key = 'STOCK_GREATER_THAN_MAXIMUM_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('STOCK_GREATER_THAN_MAXIMUM_COLOR','#dce6f1','Blue color for stock > maximum','Graph color when stock is grater than maximum stock','VACCINE','TEXT',true);

delete from configuration_settings where key = 'STOCK_GREATER_THAN_REORDER_LEVEL_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('STOCK_GREATER_THAN_REORDER_LEVEL_COLOR','#006600','Green color for stock > re-order level','Graph color when stock is grater than re-order level','VACCINE','TEXT',true);

delete from configuration_settings where key = 'STOCK_GREATER_THAN_BUFFER_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('STOCK_GREATER_THAN_BUFFER_COLOR','#ffdb00','Yellow color for stock < re-order level','Graph color when stock is less than re-order level','VACCINE','TEXT',true);

delete from configuration_settings where key = 'STOCK_LESS_THAN_BUFFER_COLOR';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('STOCK_LESS_THAN_BUFFER_COLOR','#ff0d00','Red color for stock < buffer stock','Graph color when stock is less than buffer stock','VACCINE','TEXT',true);
