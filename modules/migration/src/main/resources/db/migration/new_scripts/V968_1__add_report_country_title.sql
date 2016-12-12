
delete from configuration_settings where key = 'REPORT_COUNTRY_TITLE_TEXT';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('REPORT_COUNTRY_TITLE_TEXT', 'Report Country Title', 'Report Labels', '','The United Republic of Tanzania',  'TEXT', 3);
