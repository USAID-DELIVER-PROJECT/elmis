-- Function: fn_get_vaccine_current_reporting_period()

DROP FUNCTION if exists fn_get_vaccine_current_reporting_period();

CREATE OR REPLACE FUNCTION fn_get_vaccine_current_reporting_period()
  RETURNS integer AS
$BODY$
DECLARE

v_cutoff_days integer;
v_start_date date;
v_end_date date;
v_days_in_month integer;
v_period_id integer;

BEGIN
-- initialize
v_period_id = 0;
select value into v_cutoff_days from configuration_settings where key = 'VACCINE_LATE_REPORTING_DAYS';

-- check for valid cutoff days
if v_cutoff_days::int > 0 then 

-- get number of days in the current month
SELECT  
    DATE_PART('days', 
        DATE_TRUNC('month', NOW()) 
        + '1 MONTH'::INTERVAL 
        - DATE_TRUNC('month', NOW())
    ) into v_days_in_month;

-- reset cut off day to number of days in the current month if cut off day value is greater
if v_cutoff_days > v_days_in_month then 
 v_cutoff_days = v_days_in_month;
end if;

-- calculate start date and end date based on cut off days
select 
case when (now()::date  - date_trunc('month', current_date)::date)  >= v_cutoff_days 
  then (date_trunc('month', current_date) - interval '1 month')::date  
   else (date_trunc('month', current_date) - interval '2 month')::date end into v_start_date;


v_end_date = ( v_start_date + interval '1 month' - interval '1 day')::date;

-- get period id
select id into v_period_id from processing_periods where startdate::date = v_start_date and enddate::date = v_end_date; 
v_period_id = COALESCE(v_period_id,0);
end if;

return v_period_id;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;