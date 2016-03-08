-- View: vw_vaccine_population_denominator

DROP VIEW IF EXISTS vw_vaccine_population_denominator;

CREATE OR REPLACE VIEW vw_vaccine_population_denominator AS 
 WITH temp AS (
         SELECT pd.programid,
            ps.facilityid,
            e.year,
            pd.productid,
            sum(COALESCE(fn_get_vaccine_coverage_denominator(pd.programid, ps.facilityid, e.year, pd.productid, pd.doseid), 0)) AS denominator
           FROM vaccine_product_doses pd
             JOIN programs_supported ps ON ps.programid = pd.programid 
             JOIN facilities f_1 ON f_1.id = ps.facilityid
             JOIN vw_districts d_1 ON f_1.geographiczoneid = d_1.district_id
             JOIN ( SELECT DISTINCT facility_demographic_estimates.year
                   FROM facility_demographic_estimates) e ON 1 = 1
          GROUP BY pd.programid, ps.facilityid, e.year, pd.productid
        )
 SELECT t.year,
    d.region_name,
    d.district_name,
    f.name AS facility_name,
    d.region_id,
    d.district_id,
    t.programid,
    t.facilityid,
    t.productid,
    t.denominator,
    tp.target_value_monthly population
   FROM temp t
     JOIN products p ON p.id = t.productid
     JOIN facilities f ON t.facilityid = f.id
     JOIN vw_districts d ON f.geographiczoneid = d.district_id
     LEFT JOIN vw_vaccine_target_population tp on tp.program_id = t.programid and tp.facility_id = t.facilityid and tp.year = t.year and category_id = 1;

ALTER TABLE vw_vaccine_population_denominator
  OWNER TO postgres;
