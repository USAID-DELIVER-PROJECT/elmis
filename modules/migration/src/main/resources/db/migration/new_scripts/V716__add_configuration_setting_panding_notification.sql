delete from configuration_settings where key = 'NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT';

INSERT INTO configuration_settings(
             key, value, name, description, groupname, displayorder, valuetype,
            valueoptions, isconfigurable)
    VALUES ('NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT', 5,'Number Of Days to receive consignment', 'Limit of Days for consignment to be received after being issued to the store', 'VACCINE', 60, 'NUMBER',
            '', true);
