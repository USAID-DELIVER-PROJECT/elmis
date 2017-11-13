DO $$
BEGIN
  ALTER TABLE equipment_operational_status ADD COLUMN isObsolete boolean NULL;
  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column isObsolete already exists in equipment_operational_status.';
END;
$$;


update equipment_operational_status
  set name='Obsolete or Decommissioned', isObsolete = TRUE
where name = 'Partially Operational';
