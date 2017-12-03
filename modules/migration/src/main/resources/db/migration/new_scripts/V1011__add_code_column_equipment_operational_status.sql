DO $$
BEGIN
  ALTER TABLE equipment_operational_status ADD COLUMN code CHARACTER VARYING(1000) NULL;
  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column code already exists in equipment_operational_status table';
END;
$$;

update equipment_operational_status set code='FUNCTIONAL', name='Functional' WHERE name='Fully Operational';
update equipment_operational_status set code='NON_OPERATONAL', name='Not Functional' WHERE name='Not Operational';
update equipment_operational_status set code='OBSOLETE' WHERE name='Obsolete or Decommissioned';