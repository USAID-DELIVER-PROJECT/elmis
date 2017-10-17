DO $$
BEGIN
  BEGIN
    ALTER TABLE equipment_status_line_items ADD COLUMN ElectrolytesDaysOutOfUse INTEGER;
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column ElectrolytesDaysOutOfUse already exists in equipment_status_line_items.';
  END;
END;
$$;


DO $$
BEGIN
  BEGIN
    ALTER TABLE equipment_status_line_items ADD COLUMN AnalytesDaysOutOfUse INTEGER;
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column AnalytesDaysOutOfUse already exists in equipment_status_line_items.';
  END;
END;
$$