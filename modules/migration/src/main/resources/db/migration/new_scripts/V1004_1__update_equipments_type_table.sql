DO $$
BEGIN
  BEGIN
    ALTER TABLE equipment_types ADD COLUMN IsBioChemistry Boolean default false;
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column IsBioChemistry already exists in equipment_types.';
  END;
END;
$$