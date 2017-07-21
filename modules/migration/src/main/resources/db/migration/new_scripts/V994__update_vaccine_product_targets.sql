
  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetDropOutGood FLOAT;
  
  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetDropOutWarn FLOAT;

  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetDropOutBad FLOAT;
  
  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetWastageClosedVialsGood FLOAT;

  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetWastageClosedVialsWarn FLOAT;

  ALTER TABLE vaccine_product_targets
  ADD IF NOT EXISTS targetWastageClosedVialsBad FLOAT;


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