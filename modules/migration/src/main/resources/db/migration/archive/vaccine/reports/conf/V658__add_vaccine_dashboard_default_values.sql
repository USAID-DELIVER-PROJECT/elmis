delete from configuration_settings where key = 'VACCINE_DASHBOARD_DEFAULT_PRODUCT';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('VACCINE_DASHBOARD_DEFAULT_PRODUCT', 'Configure Default vaccine product', 'Dashboard', '','2412',  'TEXT', 1);




delete from configuration_settings where key = 'VACCINE_DASHBOARD_DEFAULT_PERIOD_TREND';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('VACCINE_DASHBOARD_DEFAULT_PERIOD_TREND', 'Configure Default vaccine period trend', 'Dashboard', '','4',  'NUMBER', 1);

