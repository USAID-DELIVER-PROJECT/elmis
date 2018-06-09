ALTER TABLE vaccine_product_targets
  DROP column if exists targetDropOutGood;

  ALTER TABLE vaccine_product_targets
  ADD column targetDropOutGood FLOAT;

  ALTER TABLE vaccine_product_targets
  DROP COLUMN IF EXISTS targetDropOutWarn;

  ALTER TABLE vaccine_product_targets
  ADD COLUMN targetDropOutWarn FLOAT;

ALTER TABLE vaccine_product_targets
  DROP COLUMN IF EXISTS targetDropOutBad;

  ALTER TABLE vaccine_product_targets
  ADD COLUMN targetDropOutBad FLOAT;

  ALTER TABLE vaccine_product_targets
  DROP COLUMN IF EXISTS targetWastageClosedVialsGood;

  ALTER TABLE vaccine_product_targets
  ADD COLUMN targetWastageClosedVialsGood FLOAT;

 ALTER TABLE vaccine_product_targets
  drop COLUMN IF EXISTS targetWastageClosedVialsWarn ;

  ALTER TABLE vaccine_product_targets
  ADD COLUMN targetWastageClosedVialsWarn FLOAT;

 ALTER TABLE vaccine_product_targets
  DROP column IF EXISTS targetWastageClosedVialsBad;

  ALTER TABLE vaccine_product_targets
  ADD COLUMN targetWastageClosedVialsBad FLOAT;


DELETE from vaccine_product_targets;

INSERT INTO vaccine_product_targets (productId,
 targetWastageGood, targetWastageWarn, targetWastageBad,
 targetCoverageBad, targetCoverageWarn, targetCoverageGood,
 targetDropOutGood, targetDropOutWarn, targetDropOutBad,
 targetWastageClosedVialsGood, targetWastageClosedVialsWarn, targetWastageClosedVialsBad
 )
  SELECT
    id,
    10,
    70,
    90,

    50,
    80,
    90,

    5,
    10,
    20,

    5,
    10,
    20

  FROM products
  WHERE id IN (
    SELECT productid
    FROM program_products
    WHERE active = TRUE AND fullsupply = TRUE
  );


