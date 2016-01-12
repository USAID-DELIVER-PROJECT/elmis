-- Function: fn_get_vaccine_program_id()
DROP FUNCTION IF EXISTS fn_get_vaccine_program_id();

CREATE OR REPLACE FUNCTION fn_get_vaccine_program_id()
  RETURNS integer AS
$BODY$
DECLARE
v_ret integer;
BEGIN
select id into v_ret from programs p where p.enableivdform = true;
v_ret =  COALESCE(v_ret,0);
return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;