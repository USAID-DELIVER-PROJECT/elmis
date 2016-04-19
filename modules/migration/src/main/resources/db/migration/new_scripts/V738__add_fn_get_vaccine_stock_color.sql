CREATE OR REPLACE FUNCTION fn_get_vaccine_stock_color(maximum_stock integer,reorder_level integer, buffer_stock integer, stock_on_hand integer)
  RETURNS varchar(7) AS
$BODY$
DECLARE
color varchar(7);
t_color varchar(7);
BEGIN
  color='#E3E3E3';
  IF stock_on_hand > maximum_stock THEN

          t_color=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('STOCK_GREATER_THAN_MAXIMUM_COLOR') LIMIT 1);
          IF t_color IS NOT NULL AND t_color <> '' THEN
             color=t_color;
          END IF;
    ELSIF stock_on_hand <= maximum_stock AND stock_on_hand >= reorder_level THEN
          t_color=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('STOCK_GREATER_THAN_REORDER_LEVEL_COLOR') LIMIT 1);
          IF t_color IS NOT NULL AND t_color <> '' THEN
             color=t_color;
          END IF;
    ELSIF stock_on_hand < reorder_level AND stock_on_hand >= buffer_stock THEN
           t_color=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('STOCK_GREATER_THAN_BUFFER_COLOR') LIMIT 1);
           IF t_color IS NOT NULL AND t_color <> '' THEN
             color=t_color;
           END IF;
    ELSE
          t_color=(SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('STOCK_LESS_THAN_BUFFER_COLOR') LIMIT 1);
          IF t_color IS NOT NULL AND t_color <> '' THEN
             color=t_color;
          END IF;
 END IF;

return color;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_vaccine_stock_color(integer,integer,integer,integer)
  OWNER TO postgres;