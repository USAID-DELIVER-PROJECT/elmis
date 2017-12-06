  DO $$
  BEGIN
    ALTER TABLE manual_test_types ADD COLUMN createdby integer NULL;
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column createdby already exists in manual_test_types.';
  END;
  $$;

  DO $$
  BEGIN
    ALTER TABLE manual_test_types ADD COLUMN modifiedby integer NULL;
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column modifiedby already exists in manual_test_types.';
  END;
  $$;

  DO $$
  BEGIN
    ALTER TABLE manual_test_types ADD COLUMN modifieddate timestamp without time zone DEFAULT now();
    EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column modifieddate already exists in manual_test_types.';
  END;
  $$;
