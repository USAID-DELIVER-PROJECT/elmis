/*TODO: Fix flyway issue to run this query*/
/*
-- Function: fn_get_dashboard_summary_data(integer, integer, integer, character varying, integer, integer)

 DROP FUNCTION IF EXISTS fn_get_dashboard_summary_data(integer, integer, integer, character varying, integer, integer);

CREATE OR REPLACE FUNCTION fn_get_dashboard_summary_data(IN in_program_id integer, IN in_period_id integer, IN in_userid integer DEFAULT 0, IN in_product_code character varying DEFAULT 'ALL'::character varying, IN in_geographiczone_id integer DEFAULT 0, IN in_n integer DEFAULT 4)
  RETURNS TABLE(r bigint, program_id integer, product_code character varying, geographiczone_id integer, startdate date, beginning_balance integer, quantity_received integer, quantity_dispensed integer, total_losses_and_adjustments integer, stock_in_hand_facility integer, stock_in_hand_upper integer, amc integer, quantity_requested integer, calculated_order_quantity integer, quantity_approved integer, quantity_expired_facility integer, quantity_expired_upper integer, number_of_facilities_stocked_out_facility integer, number_of_facilities_stocked_out_upper integer, price numeric) AS
$BODY$
This function function build anlystical table of key logistics indicators of product
for past n periods of single reporting facility
indicators include:
opening balanace
quantity received,
quantity dispensed
adjusted consumption
adjustment
stockinhand
quantity requested
quantity approved
quanity expired
price


DECLARE
-- return values
finalQuery VARCHAR;
-- temp
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;
t_program_id integer;
t_geographiczone_id integer;
t_parent_id integer;
t_level_id integer;
t_where_1 varchar;
t_order_by_1 varchar;
t_group_by_1 varchar;
t_join_1 varchar;
t_user_products varchar;
t_periods varchar;
t_tracer varchar;
t_default varchar;
t_sdp_level_id integer;
BEGIN
t_where_1 = '';
t_order_by_1 = '';
t_group_by_1 = '';
t_join_1 = '';
t_user_products = '';
t_periods = '';
t_tracer = 't';
t_default = 'DEFAULT_PRODUCTS';
-- check if ids are valid
select id into t_product_id from products where lower(code) = lower(in_product_code); 
select id into t_program_id from programs where id = in_program_id;
select id, processing_periods.startdate::date into t_period_id, t_start_date from processing_periods where id = in_period_id;
select max(levelid) into t_sdp_level_id from facility_types;
t_sdp_level_id = COALESCE(t_sdp_level_id,0);
if in_geographiczone_id <> 0 then
select id, levelid, parentid into t_geographiczone_id, t_level_id, t_parent_id from geographic_zones where id = in_geographiczone_id;
t_geographiczone_id = COALESCE(t_geographiczone_id,-1);
t_parent_id = COALESCE(t_parent_id,-1);
end if;
t_geographiczone_id = COALESCE(t_geographiczone_id,0);
t_parent_id = COALESCE(t_parent_id,0);
-- check null values
t_period_id = COALESCE(t_period_id,0);
t_program_id = COALESCE(t_program_id,0);
t_product_id = COALESCE(t_product_id,0);
if t_geographiczone_id = 0 THEN
t_geographiczone_id = (select id from geographic_zones where COALESCE(parentid,0) = 0);
t_geographiczone_id = COALESCE(t_geographiczone_id,0);
end if;
-- proceed if ids arae valid
if t_period_id > 0 and t_program_id > 0 and t_geographiczone_id >= 0 then
-- TODO: implement district, region and zone level aggregation
--t_where_1 = ' and district_id = '||t_geographiczone_id||' or region_id = '||t_geographiczone_id||' or zone_id = '||t_geographiczone_id || ' or parent = '||t_geographiczone_id;
t_order_by_1 = ' order by startdate::date desc ';
-- determine single of all products
if in_product_code = 'ALL' then
t_where_1 = 'where requisitions.programid = '||t_program_id ||' and products.tracer = '||quote_literal(t_tracer);
else
t_where_1 = 'where requisitions.programid = '||t_program_id ||' and products.code = '|| quote_literal(in_product_code) ||' and products.tracer = '||quote_literal(t_tracer);
end if;
-- add user product constraint
if in_userid > 0 THEN
t_user_products = (select value from user_preferences where userid = in_userid and userpreferencekey = 'DEFAULT_PRODUCTS');
t_user_products = COALESCE(t_user_products,'0'); 
t_where_1 = t_where_1 || ' and products.id in ('|| t_user_products ||') ';
end if;
-- add period constraint
--t_periods = array_to_string( array( select id from processing_periods p where p.startdate::date <= t_start_date order by p.startdate desc limit 4 ), ',' );
t_periods = array_to_string( array( 
select pp.id from processing_periods pp where scheduleid in (
select ps.scheduleid from requisition_group_program_schedules ps
where ps.programid = t_program_id) and pp.startdate::date <= t_start_date
order by pp.startdate desc limit in_n), ',' );
t_periods = COALESCE(t_periods,'0'); 
t_where_1 = t_where_1 || ' and processing_periods.id in ('|| t_periods ||') ';
-- add geoggraphic zone id constraint 
if t_geographiczone_id > 0 and t_parent_id > 0 THEN
t_where_1 = t_where_1 || ' and facilities.geographiczoneid = ' || t_geographiczone_id;
end if;
-- set ggroup by
t_group_by_1 = ' group by productcode, processing_periods.startdate::date '; 
--TODO- implement expired quantity
t_quantity_expired = 0;
i := 0;
select currentprice into t_price from program_products where productid = t_product_id and programid = t_program_id;
t_price = COALESCE(t_price,0);
-- not executed if in_nth is 0(current period)
finalQuery :=
'
select
rs2.r, 
rs2.program_id, 
rs2.product_code, 
rs2.geographiczone_id, 
max(rs2.startdate) startdate, 
sum(rs2.beginning_balance)::int beginning_balance,
sum(rs2.quantity_received)::int quantity_received,
sum(rs2.quantity_dispensed)::int quantity_dispensed,
sum(rs2.total_losses_and_adjustments)::int totallosses_and_adjustments,
sum (rs2.stock_in_hand_facility)::int stock_in_hand_facility,
sum (rs2.stock_in_hand_upper)::int stock_in_hand_upper,
sum(rs2.quantity_requested)::int quantity_requested,
sum(rs2.calculated_order_quantity)::int calculated_order_quantity,
sum(rs2.quantity_approved)::int quantity_approved,
avg(rs2.amc)::int amc, 
sum(rs2.quantity_expired_facility)::int quantity_expired_facility,
sum(rs2.quantity_expired_upper)::int quantity_expired_upper,
sum(rs2.number_of_facilities_stocked_out_facility)::int number_of_facilities_stocked_out_facility, 
sum(rs2.number_of_facilities_stocked_out_upper)::int number_of_facilities_stocked_out_upper, 
avg(rs2.price)::numeric price
from (
SELECT
rs.* 
FROM (
SELECT
ROW_NUMBER() OVER (PARTITION BY t.product_code '|| t_order_by_1 || ') AS r,
t.*
FROM
(
SELECT '||
in_program_id|| '::integer program_id, '||
'productcode product_code, 
processing_periods.startdate::date startdate, '||
t_geographiczone_id|| '::integer geographiczone_id,
sum(COALESCE(beginningbalance,0))::int beginning_balance,
sum(COALESCE(quantityreceived,0))::int quantity_received,
sum(COALESCE(case when facility_types.levelid = '|| t_sdp_level_id ||' then quantitydispensed else 0 end,0))::int quantity_dispensed,
sum(COALESCE(totallossesandadjustments,0))::int total_losses_and_adjustments,
sum(COALESCE(case when facility_types.levelid = '|| t_sdp_level_id ||' then stockinhand else 0 end,0))::int stock_in_hand_facility,
sum(COALESCE(case when facility_types.levelid <> '|| t_sdp_level_id ||' then stockinhand else 0 end,0))::int stock_in_hand_upper,
sum(COALESCE(quantityrequested,0))::int quantity_requested,
sum(COALESCE(calculatedorderquantity,0))::int calculated_order_quantity,
sum(COALESCE(quantityapproved,0))::int quantity_approved,
avg(COALESCE(amc,0))::integer amc, 
sum(COALESCE((select case when facility_types.levelid = '|| t_sdp_level_id ||' then quantity else 0 end from requisition_line_item_losses_adjustments where requisitionlineitemid = requisition_line_items.id and type = ''EXPIRED''),0))::int quantity_expired_facility,
sum(COALESCE((select case when facility_types.levelid <> '|| t_sdp_level_id ||' then quantity else 0 end from requisition_line_item_losses_adjustments where requisitionlineitemid = requisition_line_items.id and type = ''EXPIRED''),0))::int quantity_expired_upper,
sum( case when facility_types.levelid = '|| t_sdp_level_id ||' and stockinhand = 0 then 1 else 0 end)::int number_of_facilities_stocked_out_facility, 
sum( case when facility_types.levelid <> '|| t_sdp_level_id ||' and stockinhand = 0 then 1 else 0 end)::int number_of_facilities_stocked_out_upper, 
avg(COALESCE((select currentprice from program_products where programid = requisitions.programid and productid = products.id limit 1),0)) price
from requisition_line_items
INNER JOIN requisitions ON requisition_line_items.rnrid = requisitions.id
INNER JOIN processing_periods ON processing_periods.id = requisitions.periodid
INNER JOIN facilities ON facilities.id = requisitions.facilityid
INNER JOIN facility_types ON facilities.typeid = facility_types.id
INNER JOIN products ON requisition_line_items.productcode= products.code '||
t_join_1 || t_where_1 || t_group_by_1 ||' ) t) rs where rs.r <= '||in_n ||
' union
SELECT
rs.* 
FROM (
SELECT
ROW_NUMBER() OVER (PARTITION BY t.product_code order by startdate::date desc ) AS r,
t.*
FROM
(
SELECT '||
in_program_id|| '::integer program_id, '||
'products.code product_code,
startdate::date startdate, '||
t_geographiczone_id|| '::integer geographiczone_id,
0::integer beginning_balance,
0::integer quantity_received, 
0::integer quantity_dispensed, 
0::integer total_losses_and_adjustments,
0::integer stock_in_hand_facility,
0::integer stock_in_hand_upper,
0::integer amc, 
0::integer quantity_requested,
0::integer calculated_order_quantity,
0::integer quantity_approved, 
0::integer quantity_expired_facility,
0::integer quantity_expired_upper,
0::integer number_of_facilities_stocked_out_facility,
0::integer number_of_facilities_stocked_out_upper,
0::numeric price
from processing_periods 
CROSS JOIN products 
where processing_periods.id in ('|| t_periods ||') 
and products.id in ('|| t_user_products ||') 
) t) rs where rs.r <= '||in_n ||
') rs2
group by 1,2,3,4
order by 2,3,4,1';
ELSE
finalQuery :=
'select 0::bigint, '||
t_program_id|| '::integer program_id, '''||
in_product_code|| '''::varchar product_code, '''||
t_start_date|| '''::date startdate, '||
in_geographiczone_id|| '::integer geographiczone_id, '||
'null::integer beginning_balance,
null::integer quantity_received, 
null::integer quantity_dispensed, 
null::integer total_losses_and_adjustments,
null::integer stock_in_hand_facility,
null::integer stock_in_hand_upper,
null::integer amc, 
null::integer quantity_requested,
null::integer calculated_order_quantity,
null::integer quantity_approved, 
null::integer quantity_expired_facility,
null::integer quantity_expired_upper,
null::integer number_of_facilities_stocked_out_facility,
null::integer number_of_facilities_stocked_out_upper,
null::numeric price';
end if;
RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_dashboard_summary_data(integer, integer, integer, character varying, integer, integer)
  OWNER TO postgres;
*/
