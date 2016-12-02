delete from configuration_settings where key = 'REPORT_SUB_TITLE_TEXT';


INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('REPORT_SUB_TITLE_TEXT', 'Report Sub Title', 'Report Labels', '','Immunity and Vaccine Development',  'TEXT', 3);
