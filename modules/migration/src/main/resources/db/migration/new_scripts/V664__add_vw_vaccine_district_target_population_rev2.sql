-- View: vw_vaccine_district_target_population

DROP VIEW IF EXISTS vw_vaccine_district_target_population;

CREATE OR REPLACE VIEW vw_vaccine_district_target_population AS 
 SELECT e.year,
    e.programid program_id,
    e.districtid AS geographic_zone_id,
    c.id AS category_id,
    c.name AS category_name,
    e.value AS target_value_annual,
    round((e.value / 12)::double precision) AS target_value_monthly
   FROM demographic_estimate_categories c
     JOIN district_demographic_estimates e ON c.id = e.demographicestimateid;

ALTER TABLE vw_vaccine_district_target_population
  OWNER TO postgres;
