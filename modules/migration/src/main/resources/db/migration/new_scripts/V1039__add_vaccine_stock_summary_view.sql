DROP MATERIALIZED VIEW IF EXISTS public.vw_vaccine_stock_summary_view;

CREATE MATERIALIZED VIEW public.vw_vaccine_stock_summary_view AS
 SELECT s.facilityid,
    s.productid,
    f.geographiczoneid,
    pp.code AS productcode,
    pp.primaryname AS product,
    f.name AS facilityname,
    ft.name AS facilitytype,
    ft.code AS facilitytypecode,
    s.totalquantityonhand,
    r.annualneed,
    r.supplyperiodneed,
    r.isavalue,
    r.reorderlevel,
    r.bufferstock,
    r.maximumstock
   FROM stock_cards s
     JOIN facilities f ON s.facilityid = f.id
     JOIN products pp ON s.productid = pp.id
     JOIN program_products pr ON pp.id = pr.productid AND pr.productcategoryid = 100
     JOIN facility_types ft ON f.typeid = ft.id
     JOIN stock_requirements r ON r.facilityid = s.facilityid AND r.productid = pr.productid AND pr.programid = fn_get_vaccine_program_id() AND r.year::double precision = date_part('year'::text, s.modifieddate::date)
  WHERE pp.active IS TRUE AND f.active IS TRUE
WITH DATA;

ALTER TABLE public.vw_vaccine_stock_summary_view
  OWNER TO postgres;
