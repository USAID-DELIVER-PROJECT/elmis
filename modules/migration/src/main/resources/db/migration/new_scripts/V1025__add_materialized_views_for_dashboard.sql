DROP MATERIALIZED VIEW IF EXISTS public.categorization_view;

CREATE MATERIALIZED VIEW public.categorization_view AS
 SELECT
        CASE
            WHEN pd.dropout::double precision <= pt.targetdropoutgood AND cc.coveragepercentage::double precision >= pt.targetcoveragegood THEN 'good'::text
            WHEN pd.dropout::double precision > pt.targetdropoutgood AND cc.coveragepercentage::double precision >= pt.targetcoveragegood THEN 'normal'::text
            WHEN pd.dropout::double precision <= pt.targetdropoutgood AND cc.coveragepercentage::double precision <= pt.targetcoveragegood THEN 'warn'::text
            ELSE 'bad'::text
        END AS classificationclass,
        CASE
            WHEN pd.dropout::double precision <= pt.targetdropoutgood AND cc.coveragepercentage::double precision >= pt.targetcoveragegood THEN 'Cat_1'::text
            WHEN pd.dropout::double precision > pt.targetdropoutgood AND cc.coveragepercentage::double precision >= pt.targetcoveragegood THEN 'Cat_2'::text
            WHEN pd.dropout::double precision <= pt.targetdropoutgood AND cc.coveragepercentage::double precision <= pt.targetcoveragegood THEN 'Cat_3'::text
            ELSE 'Cat_4'::text
        END AS catagorization,
    cc.month,
    cc.year,
    pd.district_id,
    d.region_id,
    cc.region_name,
    cc.periodid,
    pd.period_name,
    pd.district_name,
    pd.doseid
   FROM vw_vaccine_coverage_by_dose_and_district cc
     JOIN vw_penta_dropout_district_summary pd ON cc.doseid = pd.doseid AND pd.productid = cc.productid AND pd.district_id = cc.district_id AND pd.year = cc.year AND pd.month = cc.month
     JOIN vw_districts d ON d.district_id = cc.geographiczoneid
     JOIN vaccine_product_targets pt ON pt.productid = cc.productid
WITH DATA;

ALTER TABLE public.categorization_view
  OWNER TO postgres;


-- Materialized View: public.dashboard_reporting_view

 DROP MATERIALIZED VIEW IF EXISTS public.dashboard_reporting_view;

CREATE MATERIALIZED VIEW public.dashboard_reporting_view AS
 SELECT pp.id AS priod_id,
    pp.name AS period_name,
    pp.startdate::date AS period_start_date,
    z.id AS geographiczoneid,
    z.name AS district,
    f.name AS facility_name,
    f.code AS facility_code,
    to_char(vr.createddate, 'DD Mon YYYY'::text) AS reported_date,
    COALESCE(vr.fixedimmunizationsessions, 0) AS fixed,
    COALESCE(vr.outreachimmunizationsessions, 0) AS outreach,
    COALESCE(z.catchmentpopulation, 0) AS target,
        CASE
            WHEN date_part('day'::text, COALESCE(vr.submissiondate, vr.createddate::date)::timestamp without time zone - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'T'::text
            WHEN COALESCE(date_part('day'::text, COALESCE(vr.submissiondate, vr.createddate::date)::timestamp without time zone - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'L'::text
            ELSE 'N'::text
        END AS reporting_status,
    ( SELECT count(*) AS approved
           FROM vaccine_reports rd
          WHERE rd.status::text = 'APPROVED'::text AND ps.facilityid = rd.facilityid AND pp.id = rd.periodid) AS approved,
    ( SELECT count(*) AS count
           FROM vaccine_distributions
          WHERE vaccine_distributions.tofacilityid = f.id AND vaccine_distributions.periodid = pp.id) AS distributed,
    pp.startdate,
    pp.enddate
   FROM programs_supported ps
     LEFT JOIN vaccine_reports vr ON vr.programid = ps.programid AND vr.facilityid = ps.facilityid AND (vr.status::text = ANY (ARRAY['SUBMITTED'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying]::text[]))
     LEFT JOIN processing_periods pp ON pp.id = vr.periodid
     JOIN facilities f ON f.id = ps.facilityid
     JOIN geographic_zones z ON z.id = f.geographiczoneid
  WHERE ps.programid = (( SELECT programs.id
           FROM programs
          WHERE programs.enableivdform = true
         LIMIT 1))
WITH DATA;

ALTER TABLE public.dashboard_reporting_view
  OWNER TO postgres;



-- Materialized View: public.vaccine_facility_report_view

 DROP MATERIALIZED VIEW IF EXISTS public.vaccine_facility_report_view;

CREATE MATERIALIZED VIEW public.vaccine_facility_report_view AS
 SELECT r.status,
    l.productid,
    pr.enddate::date AS enddate,
    pr.startdate::date AS startdate,
    pr.name AS period_name,
    r.periodid,
    r.programid,
    r.facilityid,
    l.closingbalance AS soh,
    l.quantityissued AS consumption
   FROM vaccine_reports r
     JOIN vaccine_report_logistics_line_items l ON r.id = l.reportid
     JOIN processing_periods pr ON pr.id = r.periodid AND pr.numberofmonths = 1
WITH DATA;

ALTER TABLE public.vaccine_facility_report_view
  OWNER TO postgres;


