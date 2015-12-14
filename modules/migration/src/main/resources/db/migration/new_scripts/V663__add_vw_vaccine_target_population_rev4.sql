-- View: vw_vaccine_target_population

DROP VIEW IF EXISTS vw_vaccine_target_population;

CREATE OR REPLACE VIEW vw_vaccine_target_population AS 
 SELECT e.year,
    e.programid program_id,
    e.facilityid AS facility_id,
    f.geographiczoneid AS geographic_zone_id,
    c.id AS category_id,
    c.name AS category_name,
    e.value AS target_value_annual,
    round((e.value / 12)::double precision) AS target_value_monthly
   FROM demographic_estimate_categories c
     LEFT JOIN facility_demographic_estimates e ON c.id = e.demographicestimateid
    JOIN facilities f ON e.facilityid = f.id;

ALTER TABLE vw_vaccine_target_population
  OWNER TO postgres;
