ALTER TABLE vaccine_product_doses
  ADD useForWastageCalculations BOOLEAN NOT NULL DEFAULT TRUE;

----------------   End  --------------------
DROP TABLE IF EXISTS vaccine_product_targets;

CREATE TABLE vaccine_product_targets
(
  id                 SERIAL PRIMARY KEY,
  productId          INT   NOT NULL REFERENCES products (id),

  targetWastageGood  FLOAT NULL,
  targetWastageWarn  FLOAT NULL,
  targetWastageBad   FLOAT NULL,

  targetCoverageBad  FLOAT NULL,
  targetCoverageWarn FLOAT NULL,
  targetCoverageGood FLOAT NULL
);

DELETE from vaccine_product_targets;

INSERT INTO vaccine_product_targets (productId, targetWastageGood, targetWastageWarn, targetWastageBad, targetCoverageBad, targetCoverageWarn, targetCoverageGood)
  SELECT
    id,
    10,
    70,
    90,

    10,
    70,
    90
  FROM products
  WHERE id IN (
    SELECT productid
    FROM program_products
    WHERE active = TRUE AND fullsupply = TRUE
  );

----------------   End  --------------------

ALTER TABLE custom_reports
  DROP CONSTRAINT IF EXISTS unique_report_key;

ALTER TABLE custom_reports
  ADD CONSTRAINT  unique_report_key UNIQUE (reportkey);

ALTER TABLE custom_reports
  DROP  IF EXISTS meta;

ALTER TABLE custom_reports
  ADD meta VARCHAR(5000) NULL;

ALTER TABLE custom_reports
  DROP COLUMN IF EXISTS active;

ALTER TABLE custom_reports
  ADD active BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE custom_reports
  SET active = true;
