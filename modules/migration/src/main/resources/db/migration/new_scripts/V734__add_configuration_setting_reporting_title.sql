
delete from configuration_settings where key = 'REPORTING_PROGRAM_TITLE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('REPORTING_PROGRAM_TITLE', 'Reporting program title', 'Report Labels', '','Immunization and Vaccine Development',  'TEXT', 1);
