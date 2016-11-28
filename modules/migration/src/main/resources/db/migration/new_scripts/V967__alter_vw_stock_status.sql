DROP VIEW vw_stock_status;

CREATE OR REPLACE VIEW vw_stock_status AS
  SELECT
    0                                                                                      AS supplyingfacility,
    facilities.code                                                                        AS facilitycode,
    products.code                                                                          AS productCode,
    facilities.name                                                                        AS facility,
    requisitions.status                                                                    AS req_status,
    TRIM(TRIM(TRIM(products.primaryname || ' ' || COALESCE(product_forms.code, '')) || ' ' ||
              COALESCE(products.strength,'')) || ' ' || CASE WHEN COALESCE(dosage_units.code) = '-' THEN '' ELSE COALESCE(dosage_units.code) END) AS product,
    requisition_line_items.stockinhand,
    requisition_line_items.stockinhand + requisition_line_items.beginningbalance +
    requisition_line_items.quantitydispensed + requisition_line_items.quantityreceived +
    abs(requisition_line_items.totallossesandadjustments)                                  AS reported_figures,
    requisitions.id                                                                        AS rnrid,
    requisition_line_items.amc,
    CASE
    WHEN COALESCE(requisition_line_items.amc, 0) = 0
      THEN 0 :: NUMERIC
    ELSE round((requisition_line_items.stockinhand / requisition_line_items.amc) :: NUMERIC, 1)
    END                                                                                    AS mos,
    COALESCE(
        CASE
        WHEN (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth -
              requisition_line_items.stockinhand) < 0
          THEN 0
        ELSE COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth -
             requisition_line_items.stockinhand
        END, 0)                                                                            AS required,
    requisition_line_items.quantityapproved                                                AS ordered,
    CASE
    WHEN COALESCE(requisition_line_items.amc,0) = 0 THEN 'UK'
    WHEN COALESCE(requisition_line_items.stockinhand, 0) = 0 THEN 'SO'
    WHEN round(requisition_line_items.stockinhand / requisition_line_items.amc, 1) <= facility_approved_products.minmonthsofstock THEN 'US'
    WHEN round(requisition_line_items.stockinhand / requisition_line_items.amc, 1) <= facility_approved_products.maxmonthsofstock THEN 'SP'
    WHEN round(requisition_line_items.stockinhand / requisition_line_items.amc, 1) > facility_approved_products.minmonthsofstock THEN 'OS'
    ELSE 'UK' END                                                                        AS status,
    facility_types.name                                                                    AS facilitytypename,
    geographic_zones.id                                                                    AS gz_id,
    geographic_zones.name                                                                  AS location,
    products.id                                                                            AS productid,
    processing_periods.startdate,
    programs.id                                                                            AS programid,
    processing_schedules.id                                                                AS psid,
    processing_periods.enddate,
    processing_periods.id                                                                  AS periodid,
    facility_types.id                                                                      AS facilitytypeid,
    requisition_group_members.requisitiongroupid                                           AS rgid,
    program_products.productCategoryId                                                     AS categoryid,
    products.tracer                                                                        AS indicator_product,
    facilities.id                                                                          AS facility_id,
    processing_periods.name                                                                AS processing_period_name,
    requisition_line_items.stockoutdays,
    requisitions.supervisorynodeid
  FROM requisition_line_items
    JOIN requisitions ON requisitions.id = requisition_line_items.rnrid
    JOIN facilities ON facilities.id = requisitions.facilityid
    JOIN facility_types ON facility_types.id = facilities.typeid
    JOIN processing_periods ON processing_periods.id = requisitions.periodid
    JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid
    JOIN products ON products.code :: TEXT = requisition_line_items.productcode :: TEXT
    LEFT JOIN product_forms ON products.formid = product_forms.id
    LEFT JOIN dosage_units ON products.dosageunitid = dosage_units.id
    JOIN program_products
      ON requisitions.programId = program_products.programId AND products.id = program_products.productId
    LEFT JOIN facility_approved_products
      ON facility_types.id = facility_approved_products.facilitytypeid AND program_products.id = facility_approved_products.programproductid
    JOIN product_categories ON product_categories.id = program_products.productCategoryId
    JOIN programs ON programs.id = requisitions.programid
    JOIN requisition_group_members ON requisition_group_members.facilityid = facilities.id
    JOIN requisition_group_program_schedules
      ON requisition_group_members.requisitiongroupid = requisition_group_program_schedules.requisitiongroupid
         AND requisition_group_program_schedules.programid = requisitions.programid
         AND requisition_group_program_schedules.scheduleid = processing_schedules.id
    JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
  WHERE requisition_line_items.stockinhand IS NOT NULL AND requisition_line_items.skipped = FALSE;
