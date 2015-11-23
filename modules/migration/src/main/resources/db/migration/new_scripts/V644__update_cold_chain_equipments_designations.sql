DO $$
BEGIN
BEGIN
ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "hasEnergy" boolean DEFAULT TRUE;
  EXCEPTION
    WHEN duplicate_column THEN RAISE NOTICE 'column hasEnergy already exists in equipment_cold_chain_equipment_designations.';
END;
BEGIN
ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "isRefrigerator" boolean;
   EXCEPTION
      WHEN duplicate_column THEN RAISE NOTICE 'column isRefrigerator already exists in equipment_cold_chain_equipment_designations.';
END;
BEGIN
ALTER TABLE equipment_cold_chain_equipment_designations
  ADD COLUMN "isFreezer" boolean;
  EXCEPTION
        WHEN duplicate_column THEN RAISE NOTICE 'column isFreezer already exists in equipment_cold_chain_equipment_designations.';
END;
BEGIN
ALTER TABLE equipment_cold_chain_equipments
ADD COLUMN "capacity" numeric(8,2);
 EXCEPTION
        WHEN duplicate_column THEN RAISE NOTICE 'column capacity already exists in equipment_cold_chain_equipment_designations.';

END;
END;
$$