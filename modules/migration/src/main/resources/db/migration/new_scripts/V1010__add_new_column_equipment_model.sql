DO $$
BEGIN
  ALTER TABLE equipments ADD COLUMN modelid INT NULL;

  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column modelid already exists in equipments table';
END;
$$;

DO $$
BEGIN
  ALTER TABLE equipment_inventories ADD COLUMN remark character varying(1000);
  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column remark already exists in equipments table';
END;
$$;

ALTER TABLE equipments
ADD CONSTRAINT equipments_equipment_model_id_fk
FOREIGN KEY (modelid) REFERENCES equipment_model (id);