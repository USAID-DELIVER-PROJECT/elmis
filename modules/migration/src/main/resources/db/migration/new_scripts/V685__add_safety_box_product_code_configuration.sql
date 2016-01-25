DELETE FROM configuration_settings WHERE key = 'VACCINE_STOCK_REQUIREMENTS_SAFETY_BOX_CODE';
INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('VACCINE_STOCK_REQUIREMENTS_SAFETY_BOX_CODE', 'Safety Box Product Code', 'VACCINE', '','V015',  'TEXT', 1);