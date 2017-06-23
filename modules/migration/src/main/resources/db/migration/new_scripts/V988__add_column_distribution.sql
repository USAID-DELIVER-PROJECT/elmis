DO $$
    BEGIN
        BEGIN
            ALTER TABLE vaccine_distributions ADD COLUMN isNotificationSent Boolean default false;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column isNotificationSent already exists in vaccine_distributions.';
        END;
    END;
$$