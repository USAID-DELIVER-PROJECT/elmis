 DROP MATERIALIZED VIEW IF EXISTS categorization_view ;
           create MATERIALIZED VIEW categorization_view AS
            SELECT
              CASE WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood
               THEN 'good'
              WHEN pd.dropout > pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood
                THEN 'normal'
              WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage <= pt.targetcoveragegood
                THEN 'warn'
              ELSE 'bad'
              END AS classificationClass,
              CASE WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood
                THEN 'Cat_1'
              WHEN pd.dropout > pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood
                THEN 'Cat_2'
              WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage <= pt.targetcoveragegood
                THEN 'Cat_3'
              ELSE 'Cat_4'
              END AS catagorization,
              cc.month,
              cc.year,
              pd.district_id,
              d.region_id,
              cc.region_name,
              cc.periodid,
              pd.period_name,
              pd.district_name,
              PD.DOSEID
            FROM vw_vaccine_coverage_by_dose_and_district cc
              JOIN vw_penta_dropout_district_summary pd
              ON cc.doseid =pd.doseId
                 AND pd.productid = cc.productid
                   AND pd.district_id = cc.district_id
                   AND pd.year = cc.year
                   AND pd.month = cc.month
              JOIN vw_districts d on d.district_id = cc.geographiczoneid
              JOIN vaccine_product_targets pt ON pt.productid = cc.productid
           WITH DATA;