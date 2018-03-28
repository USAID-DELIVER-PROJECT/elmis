DO $$
  BEGIN
ALTER TABLE products  ADD COLUMN mslpacksize integer;
    EXCEPTION
 WHEN duplicate_column THEN RAISE NOTICE 'column createdby already exists in products';
  END;
$$;