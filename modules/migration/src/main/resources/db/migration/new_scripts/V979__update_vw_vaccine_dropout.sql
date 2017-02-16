
-- View: vw_vaccine_dropout

DROP VIEW vw_vaccine_dropout;

CREATE OR REPLACE VIEW vw_vaccine_dropout AS
SELECT d.program_id,
    gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    pp.id AS period_id,
    pp.name AS period_name,
    date_part('year'::text, pp.startdate) AS period_year,
    pp.startdate::date AS period_start_date,
    pp.enddate AS period_end_date,
    f.id AS facility_id,
    f.code AS facility_code,
    f.name AS facility_name,
    d.report_id,
    d.denominator,
    p.id AS product_id,
    p.code AS product_code,
    p.primaryname AS product_name,
    d.dropout_code,
    d.bcg_1,
    d.mr_1,
    d.dtp_1,
    d.dtp_3,
    d.within_outside_total
   FROM  (
SELECT      a.program_id, a.period_id, a.facility_id, a.report_id,
            a.dropout_code, max(a.denominator) AS denominator,
            sum(a.product_id) AS product_id, sum(a.bcg_1) AS bcg_1,
            sum(a.mr_1) AS mr_1, sum(a.dtp_1) AS dtp_1, sum(a.dtp_3) AS dtp_3,
            sum(a.within_outside_total) AS within_outside_total
from (
SELECT
       vr.programid AS program_id,
                            vr.periodid AS period_id,
                            vr.facilityid AS facility_id,
                            vr.id AS report_id,
                            1 AS denominator,
                                CASE
                                    WHEN p.code::text = 'V001'::text AND cli.doseid = 1 THEN p.id
                                    WHEN p.code::text = 'V009'::text AND cli.doseid = 1 THEN 0
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 1 THEN p.id
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 3 THEN 0
                                    ELSE 0
                                END AS product_id,
                                CASE
                                    WHEN p.code::text = 'V001'::text AND cli.doseid = 1 THEN 'BCGMR'::character varying
                                    WHEN p.code::text = 'V009'::text AND cli.doseid = 1 THEN 'BCGMR'::character varying
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 1 THEN 'DTP1DTP3'::character varying
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 3 THEN 'DTP1DTP3'::character varying
                                    ELSE p.code
                                END AS dropout_code,
                                CASE
                                    WHEN p.code::text = 'V001'::text AND cli.doseid = 1 THEN COALESCE(cli.regularmale, 0) + COALESCE(cli.regularfemale, 0)
                                    ELSE 0
                                END AS bcg_1,
                                CASE
                                    WHEN p.code::text = 'V009'::text AND cli.doseid = 1 THEN COALESCE(cli.regularmale, 0) + COALESCE(cli.regularfemale, 0)
                                    ELSE 0
                                END AS mr_1,
                                CASE
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 1 THEN COALESCE(cli.regularmale, 0) + COALESCE(cli.regularfemale, 0)
                                    ELSE 0
                                END AS dtp_1,
                                CASE
                                    WHEN p.code::text = 'V010'::text AND cli.doseid = 3 THEN COALESCE(cli.regularmale, 0) + COALESCE(cli.regularfemale, 0)
                                    ELSE 0
                                END AS dtp_3,
                                CASE
                                    WHEN cli.doseid = 1 THEN COALESCE(cli.regularmale, 0) + COALESCE(cli.regularfemale, 0) + COALESCE(cli.outreachmale, 0) + COALESCE(cli.outreachfemale, 0)
                                    ELSE 0
                                END AS within_outside_total
                           FROM vaccine_report_coverage_line_items cli
                      JOIN vaccine_reports vr ON cli.reportid = vr.id
                      JOIN products p ON cli.productid = p.id


   WHERE
  (p.code::text   = 'V001'::text AND cli.doseid = 1
  OR p.code::text = 'V009'::text AND cli.doseid = 1
  OR p.code::text = 'V010'::text AND cli.doseid = 1
  OR p.code::text = 'V010'::text AND cli.doseid = 3)) a
  GROUP BY a.program_id, a.period_id, a.facility_id, a.report_id, a.dropout_code) d
  JOIN facilities f ON d.facility_id = f.id
  JOIN geographic_zones gz ON f.geographiczoneid = gz.id
  JOIN processing_periods pp ON d.period_id = pp.id and numberofmonths=1
     JOIN products p ON d.product_id = p.id     ;

     ALTER TABLE vw_vaccine_dropout
  OWNER TO postgres;
