delete from configuration_settings where key = 'ON_TIME_INFULL_CONF_NUMBER';
delete from configuration_settings where key = 'ON_TIME_IN_FULL_CONF_NUMBER';


INSERT INTO configuration_settings(
             key, value, name, description, groupname, displayorder, valuetype,
            valueoptions, isconfigurable)
    VALUES ('ON_TIME_IN_FULL_CONF_NUMBER', 10,'On Time and In Full (%) ', 'On Time and In Full (%)', 'VACCINE', 100, 'NUMBER',
            '', true);
