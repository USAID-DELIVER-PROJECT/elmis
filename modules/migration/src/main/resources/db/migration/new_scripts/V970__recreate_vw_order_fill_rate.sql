DROP VIEW IF EXISTS vw_order_fill_rate ;

CREATE OR REPLACE VIEW vw_order_fill_rate
AS
  SELECT r.status
    , r.facilityid
    , r.periodid
    , li.product
    , li.productcode
    , f.name as facilityName
    , pr.scheduleid
    , f.typeid as facilitytypeid
    , p.id as productid
    , pp.productcategoryid
    , r.programid
    , f.geographiczoneid as zoneid
    , gz.name as zoneName
    , li.quantityapproved::numeric
    , sli.quantityshipped::numeric as quantityReceived
    , li.quantityapproved::numeric totalProductsApproved
    , sli.quantityshipped::numeric as totalProductsReceived
    , sli.quantityshipped::numeric as totalProductsPushed
  FROM
    requisitions r
    join processing_periods pr on pr.id = r.periodid
    join facilities f on f.id = r.facilityid
    join geographic_zones gz on gz.id = f.geographiczoneid
    join requisition_line_items li on li.rnrid = r.id
    join products p on p.code = li.productcode
    join program_products pp on p.id = pp.productid and pp.programid = r.programid
    join orders o on o.id = r.id
    left join shipment_line_items sli on o.id = sli.orderid and li.productcode = sli.productcode
