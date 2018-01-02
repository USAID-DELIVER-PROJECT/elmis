DELETE from configuration_settings where key ='IL_USERNAME';

INSERT INTO configuration_settings(key, value, name, groupname, valuetype)
values('IL_USERNAME','','Username to Access IL Interface','GENERAL','TEXT');

DELETE from configuration_settings where key ='IL_PASSWORD';
INSERT INTO configuration_settings(key, value, name, groupname, valuetype)
values('IL_PASSWORD','','Password to Access IL Interface','GENERAL','TEXT');

DELETE from configuration_settings where key ='IL_URL';
INSERT INTO configuration_settings(key, value, name, groupname, valuetype)
values('IL_URL','','URL of IL Interface','GENERAL','TEXT');