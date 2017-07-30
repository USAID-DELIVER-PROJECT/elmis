 CREATE OR REPLACE VIEW vw_stock_status_2 AS
  SELECT
    facilities.code                                         AS facilitycode,
    products.code                                           AS productcode,
    facilities.name                                         AS facility,
    requisitions.status                                     AS req_status,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    ((((requisition_line_items.stockinhand + requisition_line_items.beginningbalance) +
       requisition_line_items.quantitydispensed) + requisition_line_items.quantityreceived) +
     abs(requisition_line_items.totallossesandadjustments)) AS reported_figures,
    requisitions.id                                         AS rnrid,
    requisition_line_items.amc,
    CASE
    WHEN (COALESCE(requisition_line_items.amc, 0) = 0)
      THEN (0) :: NUMERIC
    ELSE round((((requisition_line_items.stockinhand) :: DOUBLE PRECISION /
                 (requisition_line_items.amc) :: DOUBLE PRECISION)) :: NUMERIC, 1)
    END                                                     AS mos,
    COALESCE(
        CASE
        WHEN (((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) -
               requisition_line_items.stockinhand) < 0)
          THEN 0
        ELSE ((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) -
              requisition_line_items.stockinhand)
        END, 0)                                             AS required,

    CASE
    WHEN requisition_line_items.stockinhand = 0 THEN 'SO'::text
    ELSE
      CASE WHEN requisition_line_items.amc > 0 AND requisition_line_items.stockinhand > 0 THEN
        CASE
        WHEN round((requisition_line_items.stockinhand::decimal / requisition_line_items.amc)::numeric, 2) <= fap.minmonthsofstock THEN 'US'::text
        WHEN round((requisition_line_items.stockinhand::decimal / requisition_line_items.amc)::numeric, 2) >= fap.minmonthsofstock::numeric
             AND round((requisition_line_items.stockinhand::decimal / requisition_line_items.amc)::numeric, 2) <= fap.maxmonthsofstock::numeric THEN 'SP'::text
        WHEN round((requisition_line_items.stockinhand::decimal / requisition_line_items.amc)::numeric, 2) > fap.maxmonthsofstock THEN 'OS'::text
        END
      ELSE 'UK'::text END
                                                            END AS status,
    facility_types.name                                     AS facilitytypename,
    geographic_zones.id                                     AS gz_id,
    geographic_zones.name                                   AS location,
    products.id                                             AS productid,
    processing_periods.startdate,
    programs.id                                             AS programid,
    processing_schedules.id                                 AS psid,
    processing_periods.enddate,
    processing_periods.id                                   AS periodid,
    facility_types.id                                       AS facilitytypeid,
    program_products.productcategoryid                      AS categoryid,
    products.tracer                                         AS indicator_product,
    facilities.id                                           AS facility_id,
    processing_periods.name                                 AS processing_period_name,
    requisition_line_items.stockoutdays,
    0                                                       AS supervisorynodeid
  FROM (((((((((((requisition_line_items
                   JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
                 JOIN facilities ON ((facilities.id = requisitions.facilityid)))
                JOIN facility_types ON ((facility_types.id = facilities.typeid)))
               JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
              JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
             JOIN products ON (((products.code):: TEXT = (requisition_line_items.productcode):: TEXT)))
            JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
           JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
          JOIN facility_approved_products fap on facility_types.id = fap.facilitytypeid and fap.programproductid=program_products.id)
          JOIN programs ON ((programs.id = requisitions.programid)))
    JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
  WHERE ((requisition_line_items.stockinhand IS NOT NULL) AND (requisition_line_items.skipped = FALSE))