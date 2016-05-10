

DELETE FROM configuration_settings WHERE key = 'NUMBER_OF_MONTH_FOR_BATCH_TO_EXPIRE';
INSERT INTO configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
VALUES('NUMBER_OF_MONTH_FOR_BATCH_TO_EXPIRE',3,'Number of month for a product batch to expire','Used to determine the batch which is nearly to expire','VACCINE','NUMBER',true);

