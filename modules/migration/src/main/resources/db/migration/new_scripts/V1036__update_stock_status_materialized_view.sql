DROP MATERIALIZED VIEW public.vw_vaccine_inventory_stock_status_dashboard;

CREATE MATERIALIZED VIEW public.vw_vaccine_inventory_stock_status_dashboard AS
 SELECT sr.year,
    f.id AS facility_id,
    p.id AS product_id,
    f.name AS facility_name,
    gz.name AS geographic_zone_name,
    gz.id AS geographic_zone_id,
    gz.parentid AS geographic_zone_parent_id,
    p.primaryname AS product,
        CASE
            WHEN sr.isavalue > 0 THEN round(sc.totalquantityonhand::numeric(10,2) / sr.isavalue::numeric(10,2), 2)
            ELSE 0::numeric
        END AS mos,
    sr.isavalue AS monthly_stock,
    sr.maximumstock AS maximum_stock,
    sr.reorderlevel AS reorder_level,
    sr.bufferstock AS buffer_stock,
    sc.totalquantityonhand AS soh,
    du.code AS unity_of_measure,
    ( SELECT fn_get_vaccine_stock_color(COALESCE(sr.maximumstock::integer, 0), COALESCE(sr.reorderlevel::integer, 0), COALESCE(sr.bufferstock::integer, 0), COALESCE(sc.totalquantityonhand, 0)) AS fn_get_vaccine_stock_color) AS color,
    pc.name AS product_category
   FROM stock_cards sc
     JOIN facilities f ON f.id = sc.facilityid
     JOIN  products p ON p.id = sc.productid
     JOIN  stock_requirements sr ON sr.productid = sc.productid AND sr.facilityid = f.id AND sr.year = (SELECT EXTRACT(year from now()))
     JOIN  dosage_units du ON du.id = p.dosageunitid
     JOIN  program_products pp ON pp.programid = (( SELECT fn_get_vaccine_program_id() AS fn_get_vaccine_program_id)) AND pp.productid = sc.productid
     JOIN  product_categories pc ON pc.id = pp.productcategoryid
     JOIN geographic_zones gz ON f.geographiczoneid = gz.id
  ORDER BY pc.displayorder, p.id
WITH DATA;
