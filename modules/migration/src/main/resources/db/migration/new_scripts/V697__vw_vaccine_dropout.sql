-- View: vw_vaccine_dropout

DROP VIEW IF EXISTS vw_vaccine_dropout;

CREATE OR REPLACE VIEW vw_vaccine_dropout AS 
 SELECT d.program_id,
     gz.id AS geographic_zone_id,
     gz.name AS geographic_zone_name,
     gz.levelid AS level_id,
     gz.parentid AS parent_id,
     pp.id AS period_id,
     pp.name AS period_name,
     date_part('year'::text, pp.startdate) AS period_year,
     (pp.startdate)::date AS period_start_date,
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
    FROM ((((( SELECT a.program_id,
             a.period_id,
             a.facility_id,
             a.report_id,
             a.dropout_code,
             max(a.denominator) AS denominator,
             sum(a.product_id) AS product_id,
             sum(a.bcg_1) AS bcg_1,
             sum(a.mr_1) AS mr_1,
             sum(a.dtp_1) AS dtp_1,
             sum(a.dtp_3) AS dtp_3,
             sum(within_outside_total) within_outside_total
            FROM ( WITH temp AS (
                          SELECT vaccine_reports.programid AS program_id,
                             processing_periods.id AS period_id,
                             facilities.id AS facility_id,
                             vaccine_reports.id report_id,
                             fn_get_vaccine_coverage_denominator(vaccine_reports.programid, facilities.id, (date_part('year'::text, processing_periods.startdate))::integer, products.id, vaccine_report_coverage_line_items.doseid) AS denominator,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_BCG'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN products.id
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_MR'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN 0
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN products.id
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 3)) THEN 0
                                     ELSE 0
                                 END AS product_id,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_BCG'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN 'BCGMR'::character varying
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_MR'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN 'BCGMR'::character varying
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN 'DTP1DTP3'::character varying
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 3)) THEN 'DTP1DTP3'::character varying
                                     ELSE products.code
                                 END AS dropout_code,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_BCG'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                                     ELSE 0
                                 END AS bcg_1,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_MR'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                                     ELSE 0
                                 END AS mr_1,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                                     ELSE 0
                                 END AS dtp_1,
                                 CASE
                                     WHEN (((products.code)::text = (( SELECT configuration_settings.value
                                        FROM configuration_settings
                                       WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 3)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                                     ELSE 0
                                 END AS dtp_3,
                                 CASE
                                     WHEN vaccine_report_coverage_line_items.doseid = 1 THEN 
                                        COALESCE(regularmale, 0) + COALESCE(regularfemale, 0) + COALESCE(outreachmale, 0) + COALESCE(outreachfemale, 0) 
                                     ELSE 0
                                 END within_outside_total
                            FROM ((((((vaccine_report_coverage_line_items
                              JOIN vaccine_reports ON ((vaccine_report_coverage_line_items.reportid = vaccine_reports.id)))
                              JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
                              JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
                              JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
                              JOIN products ON ((vaccine_report_coverage_line_items.productid = products.id)))
                              JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = vaccine_reports.programid))))
                           WHERE ((program_products.active = true) AND ((((((products.code)::text = (( SELECT configuration_settings.value
                                    FROM configuration_settings
                                   WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_BCG'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) OR (((products.code)::text = (( SELECT configuration_settings.value
                                    FROM configuration_settings
                                   WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_MR'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1))) OR (((products.code)::text = (( SELECT configuration_settings.value
                                    FROM configuration_settings
                                   WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1))) OR (((products.code)::text = (( SELECT configuration_settings.value
                                    FROM configuration_settings
                                   WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 3))))
                         )
                  SELECT b.program_id,
                     b.period_id,
                     b.facility_id,
                     b.report_id,
                     b.denominator,
                     b.product_id,
                     b.dropout_code,
                     b.bcg_1,
                     b.mr_1,
                     b.dtp_1,
                     b.dtp_3,
                     b.within_outside_total
                    FROM temp b) a
           GROUP BY a.program_id, a.period_id, a.facility_id, a.report_id, a.dropout_code) d
      JOIN facilities f ON ((d.facility_id = f.id)))
      JOIN geographic_zones gz ON ((f.geographiczoneid = gz.id)))
      JOIN processing_periods pp ON ((d.period_id = pp.id)))
     JOIN products p ON ((d.product_id = p.id)));

ALTER TABLE vw_vaccine_dropout
  OWNER TO postgres;
