DO $$
BEGIN
  ALTER TABLE equipment_model  ADD COLUMN equipmenttypeid INT;
  EXCEPTION
  WHEN duplicate_column THEN RAISE NOTICE 'column equipmenttypeid already exists in equipment_model table';
END;
$$;

DO $$
BEGIN
    ALTER TABLE equipment_model
    ADD CONSTRAINT equipment_types_equipment_model_id_fk FOREIGN KEY (equipmenttypeid)
    REFERENCES equipment_types (id)  ON UPDATE NO ACTION ON DELETE NO ACTION;
  EXCEPTION
   WHEN duplicate_column THEN RAISE NOTICE 'unable to create equipment_types_equipment_model_id_fk index';
END;
$$;
