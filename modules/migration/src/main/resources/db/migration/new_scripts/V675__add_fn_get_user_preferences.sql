-- Function: fn_get_user_preferences(integer)

DROP FUNCTION IF EXISTS fn_get_user_preferences(integer);

CREATE OR REPLACE FUNCTION fn_get_user_preferences(IN in_userid integer)
  RETURNS TABLE(programid integer, facilityid integer, geographiczoneid integer, productid integer) AS
$BODY$
DECLARE
_query VARCHAR;
finalQuery            VARCHAR;
r   RECORD;

v_programid integer;
v_facilityid integer;
v_geographiczoneid integer;
v_productid integer;

BEGIN
_query := 'select * from user_preferences where userid = '||in_userid;

FOR r IN EXECUTE _query
LOOP

IF  r.userpreferencekey = 'DEFAULT_PROGRAM' THEN v_programid = r.value; END IF;
IF  r.userpreferencekey = 'DEFAULT_PRODUCT' THEN v_productid = r.value; END IF;
IF  r.userpreferencekey = 'DEFAULT_FACILITY' THEN v_facilityid = r.value; END IF;
IF  r.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' THEN v_geographiczoneid = r.value; END IF;

END LOOP;

v_programid = COALESCE(v_programid,0);
v_facilityid = COALESCE(v_facilityid,0);
v_geographiczoneid = COALESCE(v_geographiczoneid,0);
v_productid = COALESCE(v_productid,0);

v_geographiczoneid = 450;

finalQuery := 'select '||v_programid ||','|| v_facilityid || ','|| v_geographiczoneid || ','|| v_productid;

RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;