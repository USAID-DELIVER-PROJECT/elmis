delete from configuration_settings where key = 'YEARS_TO_REPLACE_EQUIPMENT';

INSERT INTO configuration_settings(
             key, value, name, description, groupname, displayorder, valuetype,
            valueoptions, isconfigurable)
    VALUES ('YEARS_TO_REPLACE_EQUIPMENT', 10,'Number Of Years to Replace Equipment', 'Number Of Years to Replace Equipment', 'VACCINE', 61, 'NUMBER',
            '', true);
