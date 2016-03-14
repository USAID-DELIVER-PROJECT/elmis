DELETE FROM configuration_settings WHERE key = 'CCE_VOLUME_CAPACITY_DEMOGRAPHIC_CATEGORY';
INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('CCE_VOLUME_CAPACITY_DEMOGRAPHIC_CATEGORY', 'CCE Volume Capacity Demographic Category Name', 'VACCINE', 'Population category to be used in CCE volume required calculation for a facility','Children under 1 Years',  'TEXT', 1);

DELETE FROM configuration_settings WHERE key = 'CCE_VOLUME_CAPACITY_COVERAGE_PERCENTAGE';
INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('CCE_VOLUME_CAPACITY_COVERAGE_PERCENTAGE', 'CCE Volume Capacity Coverage (%)', 'VACCINE', 'Percentage to be used in CCE volume required calculation for a facility','100',  'NUMBER', 1);

