DROP VIEW if EXISTS  vw_vaccine_inventory_stock_status;
CREATE OR REPLACE VIEW vw_vaccine_inventory_stock_status AS
(
select  f.id as facility_id
       ,p.id as product_id
       ,f.name as facility_name
       ,gz.name as geographic_zone_name
       ,gz.id as geographic_zone_id
       ,gz.parentid as geographic_zone_parent_id
       ,p.primaryName as product
       ,CASE WHEN sr.isavalue > 0 THEN ROUND((sc.totalquantityonhand::numeric(10,2)/sr.isavalue::numeric(10,2)),2)
             ELSE 0
        END as mos
        ,sr.isavalue as monthly_stock
        ,sr.maximumstock as maximum_stock
        ,sr.reorderlevel as reorder_level
        ,sr.bufferstock as buffer_stock
       ,sc.totalquantityonhand as soh
       ,du.code as unity_of_measure
       ,(select fn_get_vaccine_stock_color(COALESCE(sr.maximumstock::integer,0),COALESCE(sr.reorderlevel::integer,0),COALESCE(sr.bufferstock::integer,0),COALESCE(sc.totalquantityonhand,0))) as color
       ,pc.name as product_category
FROM stock_cards sc
JOIN facilities f on f.id=sc.facilityid
LEFT JOIN products p on p.id=sc.productid
LEFT JOIN stock_requirements sr ON sr.productid=sc.productid and sr.facilityid=f.id
LEFT JOIN dosage_units du on du.id=p.dosageunitid
LEFT JOIN program_products pp on pp.programid=(select fn_get_vaccine_program_id()) and pp.productid=sc.productid
LEFT JOIN product_categories pc on pc.id=pp.productcategoryid
JOIN geographic_zones gz ON f.geographiczoneid = gz.id
order by pc.displayorder, p.id
)