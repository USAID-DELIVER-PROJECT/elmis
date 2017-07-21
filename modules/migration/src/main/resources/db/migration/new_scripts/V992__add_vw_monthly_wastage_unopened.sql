DROP MATERIALIZED VIEW IF EXISTS public.vw_vaccine_monthly_wastage_unopened;

CREATE MATERIALIZED VIEW public.vw_vaccine_monthly_wastage_unopened
TABLESPACE pg_default
AS
 SELECT vr.facilityid,
    vrcli.productid,
    date_part('year'::text, pp.startdate) AS year,
    date_part('month'::text, pp.startdate) AS month,
    COALESCE(vrcli.openingbalance, 0) + COALESCE(vrcli.quantityreceived, 0) AS monthlydispensable,
    COALESCE(vrcli.quantitydiscardedunopened, 0) AS monthlydiscardedunopened,
    pp.id AS periodid
   FROM vaccine_report_logistics_line_items vrcli
     JOIN vaccine_reports vr ON vr.id = vrcli.reportid
     JOIN processing_periods pp ON pp.id = vr.periodid
  ORDER BY vr.facilityid, (date_part('month'::text, pp.startdate))
WITH DATA;

ALTER TABLE public.vw_vaccine_monthly_wastage_unopened
    OWNER TO postgres;


CREATE INDEX i_vw_vaccine_monthly_wastage_unopened
    ON public.vw_vaccine_monthly_wastage_unopened USING btree
    (facilityid, productid, year, month)
    TABLESPACE pg_default;