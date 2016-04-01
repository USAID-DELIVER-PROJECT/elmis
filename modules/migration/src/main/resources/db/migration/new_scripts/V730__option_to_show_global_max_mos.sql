DELETE FROM configuration_settings where key = 'USE_GLOBAL_MAX_MOS_ON_DISPLAY';

INSERT INTO configuration_settings(
             key, value, name, description, groupname, displayorder, valuetype,
            valueoptions, isconfigurable)
    VALUES ('USE_GLOBAL_MAX_MOS_ON_DISPLAY', 'true','Show Global Max MOS', '', 'R & R', 60, 'BOOLEAN',
            '', true);