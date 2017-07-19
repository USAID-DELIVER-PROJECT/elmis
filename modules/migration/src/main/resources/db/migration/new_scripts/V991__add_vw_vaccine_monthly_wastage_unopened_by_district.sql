DROP VIEW IF EXISTS public.vw_vaccine_monthly_wastage_unopened_by_district;

CREATE OR REPLACE VIEW public.vw_vaccine_monthly_wastage_unopened_by_district AS
 SELECT av.year,
    av.month,
    ff.geographiczoneid,
    sum(av.monthlydispensable) AS monthlydispensable,
    sum(av.monthlydiscardedunopened) AS monthlydiscardedunopened,
    av.productid,
    av.periodid
   FROM vw_vaccine_monthly_wastage_unopened av
     JOIN facilities ff ON ff.id = av.facilityid
  GROUP BY av.productid, av.periodid, av.year, av.month, ff.geographiczoneid;

ALTER TABLE public.vw_vaccine_monthly_wastage_unopened_by_district
    OWNER TO postgres;
