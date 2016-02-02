delete from configuration_settings where key = 'TREND_TEMP_MIN_VALUE';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable)
VALUES ('TREND_TEMP_MIN_VALUE', '2', 'Trend Tem Max', 'Used in vaccine reports for calculating min value', 'VACCINE', '1', 'NUMBER', NULL, 't');
delete from configuration_settings where key = 'TREND_TEMP_MAX_VALUE';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable)
VALUES ('TREND_TEMP_MAX_VALUE', '8', 'Trend Tem Min', 'Used in vaccine reports for calculating max value', 'VACCINE', '1', 'NUMBER', NULL, 't');
delete from configuration_settings where key = 'TREND_MIN_EPISODE_VALUE';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable)
VALUES ('TREND_MIN_EPISODE_VALUE', '2', 'Trend Min Episode', 'Used in vaccine reports for calculating max value', 'VACCINE', '1', 'NUMBER', NULL, 't');

delete from configuration_settings where key = 'TREND_MAX_EPISODE_VALUE';
INSERT INTO configuration_settings (key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable)
VALUES ('TREND_MAX_EPISODE_VALUE', '8', 'Trend Max Episode', 'Used in vaccine reports for calculating max value', 'VACCINE', '1', 'NUMBER', NULL, 't');