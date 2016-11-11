-- Function: fn_get_vaccine_coverage_denominator_name(integer, integer, integer, integer, integer)
DROP FUNCTION if EXISTS get_vaccine_coverage_denominator_name(integer, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION get_vaccine_coverage_denominator_name(in_program integer, in_facility integer, in_year integer, in_product integer, in_dose integer)
  RETURNS VARCHAR AS
$BODY$
DECLARE
v_denominator VARCHAR (20);
BEGIN
select dc.name into v_denominator
from vaccine_product_doses d
inner join demographic_estimate_categories dc on d.denominatorestimatecategoryid=dc.id
where programid = in_program
and productid = in_product
and (doseid = in_dose or 0=in_dose);

return v_denominator;
END;
$BODY$
  LANGUAGE plpgsql
  COST 100;
ALTER FUNCTION get_vaccine_coverage_denominator_name(integer, integer, integer, integer, integer)
  OWNER TO postgres;

