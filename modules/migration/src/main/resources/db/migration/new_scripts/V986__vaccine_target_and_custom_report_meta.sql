ALTER TABLE vaccine_product_doses
  ADD IF NOT EXISTS useForWastageCalculations BOOLEAN NOT NULL DEFAULT TRUE;

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
  ADD CONSTRAINT unique_report_key UNIQUE (reportkey);


ALTER TABLE custom_reports
  ADD IF NOT EXISTS meta VARCHAR(5000) NULL;
