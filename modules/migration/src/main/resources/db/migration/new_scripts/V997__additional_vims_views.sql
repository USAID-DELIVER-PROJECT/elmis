
CREATE MATERIALIZED VIEW vw_vaccine_monthly_vaccinations
  AS
    SELECT
      vr.facilityid,
      vrcli.productid,
      extract(YEAR FROM pp.startdate)              AS year,
      extract(MONTH FROM pp.startdate)             AS month,
      sum(vrcli.regularmale + vrcli.regularfemale) AS monthlyRegular,
      pp.id                                        AS periodid
    FROM vaccine_report_coverage_line_items vrcli
      JOIN vaccine_reports vr ON vr.id = vrcli.reportid
      JOIN processing_periods pp ON pp.id = vr.periodid
    GROUP BY vr.facilityid, extract(MONTH FROM pp.startdate), extract(YEAR FROM pp.startdate), vrcli.productid,
      pp.startDate, pp.id
    ORDER BY facilityid, extract(MONTH FROM pp.startdate);


CREATE INDEX i_vw_vaccine_monthly_vaccination
  ON vw_vaccine_monthly_vaccinations (facilityid, year, month);

----------------   End  --------------------

DROP INDEX IF EXISTS i_vw_vaccine_monthly_vaccination_by_dosage;
DROP MATERIALIZED VIEW IF EXISTS vw_vaccine_monthly_vaccinations_by_dosage CASCADE;

CREATE MATERIALIZED VIEW vw_vaccine_monthly_vaccinations_by_dosage
  AS
    SELECT
      vr.facilityid,
      vrcli.productid,
      vrcli.doseid,
      extract(YEAR FROM pp.startdate)              AS year,
      extract(MONTH FROM pp.startdate)             AS month,
      sum(vrcli.regularmale + vrcli.regularfemale) AS monthlyRegular,
      pp.id                                        AS periodid
    FROM vaccine_report_coverage_line_items vrcli
      JOIN vaccine_reports vr ON vr.id = vrcli.reportid
      JOIN processing_periods pp ON pp.id = vr.periodid
    GROUP BY vr.facilityid, extract(MONTH FROM pp.startdate), extract(YEAR FROM pp.startdate), vrcli.productid,
      vrcli.doseid, pp.startDate, pp.id
    ORDER BY facilityid, extract(MONTH FROM pp.startdate);


CREATE INDEX i_vw_vaccine_monthly_vaccination_by_dosage
  ON vw_vaccine_monthly_vaccinations_by_dosage (facilityid, productid, doseid, year, month);

----------------   End  --------------------

CREATE MATERIALIZED VIEW vw_vaccine_monthly_consumption
  AS
    SELECT
      vr.facilityid,
      vrcli.productid,
      extract(YEAR FROM pp.startdate)                   AS year,
      extract(MONTH FROM pp.startdate)                  AS month,
      sum(coalesce(vrcli.quantityissued, 0))            AS monthlyConsumption,
      sum(coalesce(vrcli.quantitydiscardedunopened, 0)) AS monthlyDiscardedUnopnened,
      pp.id                                             AS periodid
    FROM vaccine_report_logistics_line_items vrcli
      JOIN vaccine_reports vr ON vr.id = vrcli.reportid
      JOIN processing_periods pp ON pp.id = vr.periodid
    GROUP BY vr.facilityid, extract(MONTH FROM pp.startdate), extract(YEAR FROM pp.startdate), vrcli.productid,
      pp.startDate, pp.id
    ORDER BY facilityid, extract(MONTH FROM pp.startdate);


CREATE INDEX i_vw_monthly_vaccine_consumption
  ON vw_vaccine_monthly_consumption (facilityid, productid, year, month);


CREATE VIEW "public"."vw_district_bcg_mr_dropout" AS
  SELECT
    f.geographiczoneid AS districtid,
    d.productid,
    d.doseid,
    d.year,
    d.month,
    d.periodid,
    CASE
    WHEN ((sum(d.monthlyregular) IS NOT NULL) AND (sum(d.monthlyregular) <> (0) :: NUMERIC))
      THEN ((sum(d.monthlyregular) - (max(dd.monthlyregular) * (100) :: NUMERIC)) / sum(d.monthlyregular))
    ELSE NULL :: NUMERIC
    END                AS dropout
  FROM ((vw_vaccine_monthly_vaccinations_by_dosage d
    JOIN facilities f ON ((f.id = d.facilityid)))
    LEFT JOIN (SELECT
                 ff.geographiczoneid,
                 sum(v.monthlyregular) AS monthlyregular,
                 v.month,
                 v.year,
                 v.productid,
                 v.doseid
               FROM (vw_vaccine_monthly_vaccinations_by_dosage v
                 JOIN facilities ff ON ((ff.id = v.facilityid)))
               WHERE ((v.productid = 2420) AND (v.doseid = 1))
               GROUP BY v.month, v.year, v.productid, v.doseid, ff.geographiczoneid) dd
      ON (((dd.month = d.month) AND (dd.year = d.year) AND (f.geographiczoneid = dd.geographiczoneid))))
  WHERE ((d.productid = 2412) AND (d.doseid = 1))
  GROUP BY f.geographiczoneid, d.productid, d.doseid, d.year, d.month, d.periodid;

CREATE VIEW "public"."vw_district_bcg_mr_dropout_summary" AS
  SELECT
    a.districtid,
    a.productid,
    a.doseid,
    a.year,
    a.month,
    a.periodid,
    a.dropout,
    d.district_id,
    d.district_name,
    d.region_id,
    d.region_name,
    d.zone_id,
    d.zone_name,
    d.parent,
    pp.name AS period_name,
    CASE
    WHEN (a.dropout <= (pt.targetdropoutgood) :: NUMERIC)
      THEN 'good' :: TEXT
    WHEN ((a.dropout > (pt.targetdropoutgood) :: NUMERIC) AND (a.dropout <= (pt.targetdropoutwarn) :: NUMERIC))
      THEN 'normal' :: TEXT
    WHEN ((a.dropout > (pt.targetdropoutwarn) :: NUMERIC) AND (a.dropout <= (pt.targetdropoutbad) :: NUMERIC))
      THEN 'warn' :: TEXT
    WHEN (a.dropout IS NOT NULL)
      THEN 'bad' :: TEXT
    ELSE NULL :: TEXT
    END     AS classification
  FROM (((vw_district_bcg_mr_dropout a
    JOIN processing_periods pp ON ((pp.id = a.periodid)))
    JOIN vw_districts d ON ((d.district_id = a.districtid)))
    JOIN vaccine_product_targets pt ON ((pt.productid = a.productid)));

CREATE VIEW "public"."vw_facility_bcg_mr_dropout" AS
  SELECT
    d.facilityid,
    d.productid,
    d.doseid,
    d.year,
    d.month,
    d.periodid,
    CASE
    WHEN ((d.monthlyregular IS NOT NULL) AND (d.monthlyregular <> 0))
      THEN (((d.monthlyregular - dd.monthlyregular) * 100) / d.monthlyregular)
    ELSE NULL :: BIGINT
    END AS dropout
  FROM (vw_vaccine_monthly_vaccinations_by_dosage d
    JOIN (SELECT
            v.facilityid,
            v.productid,
            v.doseid,
            v.year,
            v.month,
            v.monthlyregular,
            v.periodid
          FROM vw_vaccine_monthly_vaccinations_by_dosage v
          WHERE ((v.productid = 2420) AND (v.doseid = 1))) dd
      ON (((dd.month = d.month) AND (dd.year = d.year) AND (d.facilityid = dd.facilityid))))
  WHERE ((d.productid = 2412) AND (d.doseid = 1))
  GROUP BY d.facilityid, d.productid, d.doseid, d.monthlyregular, dd.monthlyregular, d.year, d.month, d.periodid;

CREATE OR REPLACE VIEW "public"."vw_monthly_district_estimate" AS
  SELECT
    fde.districtid,
    fde.year,
    fde.demographicestimateid,
    (COALESCE(fde.value, 0) / 12) AS monthlyestimate,
    COALESCE(fde.value, 0)        AS annualestimate
  FROM district_demographic_estimates fde
  WHERE (fde.programid = 82);

CREATE OR REPLACE VIEW "public"."vw_monthly_estimate" AS
  SELECT
    f.id                          AS facilityid,
    f.name,
    fde.year,
    fde.demographicestimateid,
    (COALESCE(fde.value, 0) / 12) AS estimate
  FROM (facilities f
    JOIN facility_demographic_estimates fde ON (((fde.facilityid = f.id) AND (fde.programid = 82))));

CREATE OR REPLACE VIEW "public"."vw_vaccine_district_penta_dropouts" AS
  SELECT
    f.geographiczoneid AS districtid,
    d.productid,
    d.doseid,
    d.year,
    d.month,
    d.periodid,
    CASE
    WHEN ((sum(d.monthlyregular) IS NOT NULL) AND (sum(d.monthlyregular) <> (0) :: NUMERIC))
      THEN (((sum(d.monthlyregular) - sum(dd.monthlyregular)) * (100) :: NUMERIC) / sum(d.monthlyregular))
    ELSE NULL :: NUMERIC
    END                AS dropout
  FROM ((vw_vaccine_monthly_vaccinations_by_dosage d
    JOIN (SELECT
            v.facilityid,
            v.productid,
            v.doseid,
            v.year,
            v.month,
            v.monthlyregular,
            v.periodid
          FROM vw_vaccine_monthly_vaccinations_by_dosage v
          WHERE (v.doseid = 3)) dd
      ON (((dd.month = d.month) AND (dd.year = d.year) AND (d.facilityid = dd.facilityid) AND
           (d.productid = dd.productid))))
    JOIN facilities f ON ((f.id = d.facilityid)))
  WHERE ((d.productid = 2421) AND (d.doseid = 1))
  GROUP BY f.geographiczoneid, d.productid, d.doseid, d.year, d.month, d.periodid;

CREATE OR REPLACE VIEW "public"."vw_penta_dropout_district_summary" AS
  SELECT
    a.districtid,
    a.productid,
    a.doseid,
    a.year,
    a.month,
    a.periodid,
    a.dropout,
    pp.name AS period_name,
    d.district_id,
    d.district_name,
    d.region_id,
    d.region_name,
    d.zone_id,
    d.zone_name,
    d.parent,
    CASE
    WHEN (a.dropout <= (pt.targetdropoutgood) :: NUMERIC)
      THEN 'good' :: TEXT
    WHEN ((a.dropout > (pt.targetdropoutgood) :: NUMERIC) AND (a.dropout <= (pt.targetdropoutwarn) :: NUMERIC))
      THEN 'normal' :: TEXT
    WHEN ((a.dropout > (pt.targetdropoutwarn) :: NUMERIC) AND (a.dropout <= (pt.targetdropoutbad) :: NUMERIC))
      THEN 'warn' :: TEXT
    WHEN (a.dropout IS NOT NULL)
      THEN 'bad' :: TEXT
    ELSE NULL :: TEXT
    END     AS classification
  FROM (((vw_vaccine_district_penta_dropouts a
    JOIN processing_periods pp ON ((pp.id = a.periodid)))
    JOIN vw_districts d ON ((d.district_id = a.districtid)))
    JOIN vaccine_product_targets pt ON ((pt.productid = a.productid)));

CREATE OR REPLACE VIEW "public"."vw_vaccine_facility_penta_dropouts" AS
  SELECT
    d.facilityid,
    d.productid,
    d.doseid,
    d.year,
    d.month,
    d.periodid,
    CASE
    WHEN ((d.monthlyregular IS NOT NULL) AND (d.monthlyregular <> 0))
      THEN (((d.monthlyregular - dd.monthlyregular) * 100) / d.monthlyregular)
    ELSE NULL :: BIGINT
    END AS dropout
  FROM (vw_vaccine_monthly_vaccinations_by_dosage d
    JOIN (SELECT
            v.facilityid,
            v.productid,
            v.doseid,
            v.year,
            v.month,
            v.monthlyregular,
            v.periodid
          FROM vw_vaccine_monthly_vaccinations_by_dosage v
          WHERE (v.doseid = 3)) dd
      ON (((dd.month = d.month) AND (dd.year = d.year) AND (d.facilityid = dd.facilityid) AND
           (d.productid = dd.productid))))
  WHERE ((d.productid = 2421) AND (d.doseid = 1))
  GROUP BY d.facilityid, d.productid, d.doseid, d.monthlyregular, dd.monthlyregular, d.year, d.month, d.periodid;

CREATE OR REPLACE VIEW "public"."vw_penta_dropout_facility_summary" AS
  SELECT
    a.facilityid,
    a.productid,
    a.doseid,
    a.year,
    a.month,
    a.periodid,
    a.dropout,
    pp.name AS period_name,
    f.id    AS facility_id,
    f.name  AS facility_name,
    d.district_id,
    d.district_name,
    d.region_id,
    d.region_name,
    d.zone_id,
    d.zone_name,
    d.parent,
    CASE
    WHEN ((a.dropout) :: NUMERIC <= (pt.targetdropoutgood) :: NUMERIC)
      THEN 'good' :: TEXT
    WHEN (((a.dropout) :: NUMERIC > (pt.targetdropoutgood) :: NUMERIC) AND
          ((a.dropout) :: NUMERIC <= (pt.targetdropoutwarn) :: NUMERIC))
      THEN 'normal' :: TEXT
    WHEN (((a.dropout) :: NUMERIC > (pt.targetdropoutwarn) :: NUMERIC) AND
          ((a.dropout) :: NUMERIC <= (pt.targetdropoutbad) :: NUMERIC))
      THEN 'warn' :: TEXT
    WHEN (a.dropout IS NOT NULL)
      THEN 'bad' :: TEXT
    ELSE NULL :: TEXT
    END     AS classification
  FROM ((((vw_vaccine_facility_penta_dropouts a
    JOIN facilities f ON ((f.id = a.facilityid)))
    JOIN processing_periods pp ON ((pp.id = a.periodid)))
    JOIN vw_districts d ON ((d.district_id = f.geographiczoneid)))
    JOIN vaccine_product_targets pt ON ((pt.productid = a.productid)));

CREATE OR REPLACE VIEW "public"."vw_vaccine_cumulative_coverage_by_dose" AS
  SELECT
    a.facilityid,
    a.facilityname,
    a.year,
    a.month,
    a.monthlyregular,
    a.productid,
    a.estimate,
    a.doseid,
    a.periodid,
    a.denominatorestimatecategoryid,
    a.coveragepercentage,
    a.cumulative,
    CASE
    WHEN ((a.estimate IS NOT NULL) AND (a.estimate <> 0))
      THEN (((a.cumulative * (100) :: NUMERIC)) :: DOUBLE PRECISION / (a.month * (a.estimate) :: DOUBLE PRECISION))
    ELSE NULL :: DOUBLE PRECISION
    END AS cumulativepercentage
  FROM (SELECT
          av.facilityid,
          si.name                                                               AS facilityname,
          av.year,
          av.month,
          av.monthlyregular,
          av.productid,
          si.estimate,
          av.doseid,
          av.periodid,
          vd.denominatorestimatecategoryid,
          CASE
          WHEN ((si.estimate IS NOT NULL) AND (si.estimate <> 0))
            THEN ((av.monthlyregular * 100) / si.estimate)
          ELSE NULL :: BIGINT
          END                                                                   AS coveragepercentage,
          (SELECT sum(a_1.monthlyregular) AS sum
           FROM vw_vaccine_monthly_vaccinations_by_dosage a_1
           WHERE ((a_1.facilityid = av.facilityid) AND (a_1.year = av.year) AND (a_1.month <= av.month) AND
                  (a_1.productid = av.productid) AND (a_1.doseid = av.doseid))) AS cumulative
        FROM ((vw_vaccine_monthly_vaccinations_by_dosage av
          JOIN vaccine_product_doses vd ON (((vd.productid = av.productid) AND (vd.doseid = av.doseid))))
          JOIN vw_monthly_estimate si
            ON (((si.facilityid = av.facilityid) AND (si.demographicestimateid = vd.denominatorestimatecategoryid))))
        WHERE (((si.year) :: DOUBLE PRECISION = av.year) AND
               (si.demographicestimateid = vd.denominatorestimatecategoryid))) a;

CREATE OR REPLACE VIEW "public"."vw_vaccine_coverage_by_dose_and_district" AS
  SELECT
    a.geographiczoneid,
    a.doseid,
    a.productid,
    a.year,
    a.month,
    a.periodid,
    a.monthlyestimate,
    a.cumulativemonthlyregular,
    a.coveragepercentage,
    (((p.primaryname) :: TEXT || ' - ' :: TEXT) || a.doseid) AS product_dose,
    d.district_name,
    d.district_id,
    d.region_name,
    CASE
    WHEN (a.coveragepercentage IS NULL)
      THEN NULL :: TEXT
    WHEN ((a.coveragepercentage) :: DOUBLE PRECISION > pt.targetcoveragegood)
      THEN 'good' :: TEXT
    WHEN ((a.coveragepercentage) :: DOUBLE PRECISION > pt.targetcoveragewarn)
      THEN 'normal' :: TEXT
    WHEN ((a.coveragepercentage) :: DOUBLE PRECISION > pt.targetcoveragebad)
      THEN 'warn' :: TEXT
    ELSE 'bad' :: TEXT
    END                                                      AS coverageclassification
  FROM ((((SELECT
             f.geographiczoneid,
             d_1.doseid,
             d_1.productid,
             d_1.year,
             d_1.month,
             d_1.periodid,
             e.monthlyestimate,
             sum(d_1.monthlyregular) AS cumulativemonthlyregular,
             CASE
             WHEN ((e.monthlyestimate IS NOT NULL) AND (e.monthlyestimate <> 0))
               THEN ((sum(d_1.monthlyregular) * (100) :: NUMERIC) / (e.monthlyestimate) :: NUMERIC)
             ELSE NULL :: NUMERIC
             END                     AS coveragepercentage
           FROM ((vw_vaccine_cumulative_coverage_by_dose d_1
             JOIN facilities f ON ((f.id = d_1.facilityid)))
             JOIN vw_monthly_district_estimate e
               ON (((e.districtid = f.geographiczoneid) AND (d_1.year = (e.year) :: DOUBLE PRECISION) AND
                    (d_1.denominatorestimatecategoryid = e.demographicestimateid))))
           GROUP BY d_1.year, d_1.month, d_1.periodid, f.geographiczoneid, d_1.productid, d_1.doseid,
             e.monthlyestimate) a
    JOIN vw_districts d ON ((d.district_id = a.geographiczoneid)))
    JOIN products p ON ((p.id = a.productid)))
    JOIN vaccine_product_targets pt ON ((pt.productid = a.productid)));

CREATE OR REPLACE VIEW "public"."vw_vaccine_cumulative_consumption" AS
  SELECT
    av.facilityid,
    av.year,
    av.month,
    av.monthlyconsumption,
    av.productid,
    av.periodid,
    (SELECT sum(a.monthlyconsumption) AS sum
     FROM vw_vaccine_monthly_consumption a
     WHERE ((a.facilityid = av.facilityid) AND (a.year = av.year) AND (a.month <= av.month) AND
            (a.productid = av.productid))) AS cumulative
  FROM vw_vaccine_monthly_consumption av;

CREATE OR REPLACE VIEW "public"."vw_vaccine_cumulative_consumption_by_district" AS
  SELECT
    av.year,
    av.month,
    ff.geographiczoneid,
    sum(av.monthlyconsumption)             AS monthlyconsumption,
    av.productid,
    av.periodid,
    (SELECT sum(a.monthlyconsumption) AS sum
     FROM (vw_vaccine_monthly_consumption a
       JOIN facilities f ON ((f.id = a.facilityid)))
     WHERE ((f.geographiczoneid = ff.geographiczoneid) AND (a.year = av.year) AND (a.month <= av.month) AND
            (a.productid = av.productid))) AS cumulative
  FROM (vw_vaccine_monthly_consumption av
    JOIN facilities ff ON ((ff.id = av.facilityid)))
  GROUP BY av.productid, av.periodid, av.year, av.month, ff.geographiczoneid;



CREATE OR REPLACE VIEW "public"."vw_vaccine_cumulative_coverage" AS
  SELECT
    a.facilityid,
    a.facilityname,
    a.year,
    a.month,
    a.monthlyregular,
    a.productid,
    a.estimate,
    a.periodid,
    a.coveragepercentage,
    a.cumulative,
    CASE
    WHEN ((a.estimate IS NOT NULL) AND (a.estimate <> 0))
      THEN (((a.cumulative * (100) :: NUMERIC)) :: DOUBLE PRECISION / (a.month * (a.estimate) :: DOUBLE PRECISION))
    ELSE NULL :: DOUBLE PRECISION
    END AS cumulativepercentage
  FROM (SELECT
          av.facilityid,
          si.name                                  AS facilityname,
          av.year,
          av.month,
          av.monthlyregular,
          av.productid,
          si.estimate,
          av.periodid,
          CASE
          WHEN ((si.estimate IS NOT NULL) AND (si.estimate <> 0))
            THEN ((av.monthlyregular * 100) / si.estimate)
          ELSE NULL :: BIGINT
          END                                      AS coveragepercentage,
          (SELECT sum(a_1.monthlyregular) AS sum
           FROM vw_vaccine_monthly_vaccinations a_1
           WHERE ((a_1.facilityid = av.facilityid) AND (a_1.year = av.year) AND (a_1.month <= av.month) AND
                  (a_1.productid = av.productid))) AS cumulative
        FROM ((vw_vaccine_monthly_vaccinations av
          JOIN vw_monthly_estimate si ON ((si.facilityid = av.facilityid)))
          JOIN (SELECT DISTINCT
                  vaccine_product_doses.productid,
                  max(vaccine_product_doses.denominatorestimatecategoryid) AS demographicestimateid
                FROM vaccine_product_doses
                GROUP BY vaccine_product_doses.productid) pp ON ((pp.productid = av.productid)))
        WHERE (((si.year) :: DOUBLE PRECISION = av.year) AND (si.demographicestimateid = pp.demographicestimateid))) a;

CREATE OR REPLACE VIEW "public"."vw_vaccine_cumulative_coverage_by_district" AS
  SELECT
    f.geographiczoneid,
    coverage.periodid,
    coverage.productid,
    coverage.year,
    coverage.month,
    pp.name                      AS period_name,
    sum(coverage.monthlyregular) AS monthlyregular,
    sum(coverage.cumulative)     AS cumulativecoverage
  FROM ((vw_vaccine_cumulative_coverage coverage
    JOIN processing_periods pp ON ((pp.id = coverage.periodid)))
    JOIN facilities f ON ((f.id = coverage.facilityid)))
  GROUP BY f.geographiczoneid, coverage.periodid, coverage.productid, coverage.year, pp.name, coverage.month;


CREATE OR REPLACE VIEW "public"."vw_vaccine_wastage_rate_by_district" AS
  SELECT
    consumption.periodid,
    consumption.month,
    coverage.period_name,
    consumption.year,
    consumption.geographiczoneid,
    consumption.productid,
    CASE
    WHEN ((consumption.monthlyconsumption IS NOT NULL) AND (consumption.monthlyconsumption <> (0) :: NUMERIC))
      THEN (((consumption.monthlyconsumption - coverage.monthlyregular) * (100) :: NUMERIC) /
            consumption.monthlyconsumption)
    ELSE NULL :: NUMERIC
    END AS wastagerate
  FROM (vw_vaccine_cumulative_consumption_by_district consumption
    JOIN vw_vaccine_cumulative_coverage_by_district coverage
      ON (((consumption.productid = coverage.productid) AND (consumption.geographiczoneid = coverage.geographiczoneid)
           AND (consumption.periodid = coverage.periodid))));

CREATE OR REPLACE VIEW "public"."x_vw_vaccine_cumulative_coverage_by_dose" AS
  SELECT
    a.facilityid,
    a.facilityname,
    a.year,
    a.month,
    a.monthlyregular,
    a.productid,
    a.estimate,
    a.doseid,
    a.periodid,
    a.denominatorestimatecategoryid,
    a.coveragepercentage,
    a.cumulative,
    CASE
    WHEN ((a.estimate IS NOT NULL) AND (a.estimate <> 0))
      THEN (a.month * (a.estimate) :: DOUBLE PRECISION)
    ELSE NULL :: DOUBLE PRECISION
    END AS cumulativeestimate,
    CASE
    WHEN ((a.estimate IS NOT NULL) AND (a.estimate <> 0))
      THEN (((a.cumulative * (100) :: NUMERIC)) :: DOUBLE PRECISION / (a.month * (a.estimate) :: DOUBLE PRECISION))
    ELSE NULL :: DOUBLE PRECISION
    END AS cumulativepercentage
  FROM (SELECT
          av.facilityid,
          si.name                                                               AS facilityname,
          av.year,
          av.month,
          av.monthlyregular,
          av.productid,
          si.estimate,
          av.doseid,
          av.periodid,
          vd.denominatorestimatecategoryid,
          CASE
          WHEN ((si.estimate IS NOT NULL) AND (si.estimate <> 0))
            THEN ((av.monthlyregular * 100) / si.estimate)
          ELSE NULL :: BIGINT
          END                                                                   AS coveragepercentage,
          (SELECT sum(a_1.monthlyregular) AS sum
           FROM vw_vaccine_monthly_vaccinations_by_dosage a_1
           WHERE ((a_1.facilityid = av.facilityid) AND (a_1.year = av.year) AND (a_1.month <= av.month) AND
                  (a_1.productid = av.productid) AND (a_1.doseid = av.doseid))) AS cumulative
        FROM ((vw_vaccine_monthly_vaccinations_by_dosage av
          JOIN vaccine_product_doses vd ON (((vd.productid = av.productid) AND (vd.doseid = av.doseid))))
          JOIN vw_monthly_estimate si
            ON (((si.facilityid = av.facilityid) AND (si.demographicestimateid = vd.denominatorestimatecategoryid))))
        WHERE (((si.year) :: DOUBLE PRECISION = av.year) AND
               (si.demographicestimateid = vd.denominatorestimatecategoryid))) a;


