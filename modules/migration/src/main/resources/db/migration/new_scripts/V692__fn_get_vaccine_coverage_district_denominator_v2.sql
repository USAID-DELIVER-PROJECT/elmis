-- Function: fn_get_vaccine_coverage_district_denominator(integer, integer, integer, integer)

DROP FUNCTION if exists fn_get_vaccine_coverage_district_denominator(integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_get_vaccine_coverage_district_denominator(in_district integer, in_year integer, in_product integer, in_program integer DEFAULT 0)
  RETURNS integer AS
$BODY$
DECLARE
v_denominator integer;
v_year integer;
v_program integer;
v_target_value integer;
BEGIN

v_program = in_program;
if in_program = 0 THEN
 v_program = (select id from programs where enableivdform = true limit 1);
end if;
select d.denominatorestimatecategoryid into v_denominator from vaccine_product_doses d
where programid = v_program
and productid = in_product
limit 1;
v_denominator = COALESCE(v_denominator,0);
select round(value/12) into v_target_value from district_demographic_estimates
where year = in_year
and districtid = in_district
and demographicestimateid = v_denominator;
v_target_value = COALESCE(v_target_value,0);
return v_target_value;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;