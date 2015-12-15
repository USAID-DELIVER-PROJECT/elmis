

delete from configuration_settings where key = 'VACCINE_DASHBOARD_DEFAULT_MONTHLY_PERIOD';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('VACCINE_DASHBOARD_DEFAULT_MONTHLY_PERIOD', 'Configure Default vaccine period trend', 'Dashboard', '','36',  'TEXT', 1);

