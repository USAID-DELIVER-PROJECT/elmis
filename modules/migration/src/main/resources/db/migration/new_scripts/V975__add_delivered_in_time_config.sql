delete from configuration_settings where key = 'DELIVERED_ON_TIME_CONFIG_NUMBER';


INSERT INTO configuration_settings(
             key, value, name, description, groupname, displayorder, valuetype,
            valueoptions, isconfigurable)
    VALUES ('DELIVERED_ON_TIME_CONFIG_NUMBER', 20,'Delivered On Time Number of Days', 'Delivered On Time Number of Days', 'VACCINE', 101, 'NUMBER',
            '', true);
