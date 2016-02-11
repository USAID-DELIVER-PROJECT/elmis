-- Function: fn_get_vaccine_district_reporting_performance(integer, varchar)

DROP FUNCTION if exists fn_get_vaccine_district_reporting_performance(integer, varchar);

CREATE OR REPLACE FUNCTION fn_get_vaccine_district_reporting_performance(in_periodid integer, in_type VARCHAR DEFAULT 'ALL')
  RETURNS TABLE(geographiczoneid integer, total integer, expected integer, ever integer, period integer, late integer) AS
$BODY$
/*
 This function returns reporting performance by district. 

Extensions
 - join with vw_districts view to 
 aggegrate data at upper LEVEL. 

 - pass the code of district vaacine store type (facility_types) can be passed to get performance of 
 single store.

 -- join with geographic_zone_geojson to get geometery information

 in_periodid - selected period
 in_type - code for district vaccine store type, default is ALL

Examples

-- get national reporting performance for single period
select  sum(total) total, sum(expected) expected, sum(period) period, sum(late) late
from fn_get_vaccine_district_reporting_performance(51);

-- Get reporty performance by district for single period
select district_id, district_name,  sum(total) total, sum(expected) expected, sum(period) period, sum(late) late
from fn_get_vaccine_district_reporting_performance(51)
join vw_districts vd on vd.district_id = geographiczoneid
group by 1, 2;

-- Get district store reporty performance by district for single period
select district_id, district_name,  sum(total) total, sum(expected) expected, case when sum(expected) > 0 then case when sum(late) > 0 then 'L' else 'T' end end timeliness
from fn_get_vaccine_district_reporting_performance(51,'dvs')
join vw_districts vd on vd.district_id = geographiczoneid
group by 1, 2;

*/

DECLARE
t_programid integer;
t_type VARCHAR(8);
qry text;
BEGIN
--TODO - get programid from configuration table
t_programid = (select id from programs where enableivdform = 't' limit 1);
t_type = in_type;

qry = '';

qry = '
select b.id geographiczoneid,
sum(b.total)::int total,
sum(b.expected)::int expected,
sum(b.ever)::int ever,
sum(b.period)::int period,
sum(b.late)::int late

from (
select id, a.total, a.expected, a.ever, a.period, a.late from (
select
gzz.id,
gzz.name,
COALESCE(expected.count,0) expected,
COALESCE(total.count,0) total,
COALESCE(ever.count,0) as ever,
COALESCE(period.count,0) as period,
COALESCE(late.count,0) late
from
geographic_zones gzz
left join
geographic_zone_geojson gjson on
gzz.id = gjson.zoneId
left join
(select geographicZoneId, count(*) from facilities
join facility_types ft on ft.id = facilities.typeid
join programs_supported ps on ps.facilityId = facilities.id
join geographic_zones gz on gz.id = facilities.geographicZoneId
join requisition_group_members rgm on rgm.facilityId = facilities.id
join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId
and rgps.programId = ps.programId
join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = '||in_periodid ||'
where gz.levelId = (select max(id) from geographic_levels) and ps.programId = '||t_programid ||'
and (ft.code = '''||t_type||''' or '''||t_type||''' = ''ALL'') 
group by geographicZoneId
) expected
on gzz.id = expected.geographicZoneId
left join
(
select geographicZoneId, count(*) from facilities
join facility_types ft on ft.id = facilities.typeid
join programs_supported ps on ps.facilityId = facilities.id
join geographic_zones gz on gz.id = facilities.geographicZoneId
where  ps.programId = '||t_programid ||' and facilities.id in
(select facilityId from vaccine_reports r
join processing_periods pp on pp.id = r.periodid
where periodId = '||in_periodid ||'
and programId = '||t_programid ||'
and status not in (''DRAFT'', ''SKIPPED'') 
and (ft.code = '''||t_type||''' or '''||t_type||''' = ''ALL'')
and COALESCE(date_part(''day''::text, r.createddate - pp.enddate::date::timestamp), 0::double precision)
> COALESCE((( SELECT configuration_settings.value
FROM configuration_settings
WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::double precision
)  group by geographicZoneId
) late on gzz.id = late.geographicZoneId
left join
(select geographicZoneId, count(*) from facilities
join facility_types ft on ft.id = facilities.typeid
join geographic_zones gz on gz.id = facilities.geographicZoneId
where gz.levelId = (select max(id) from geographic_levels)
and (ft.code = '''||t_type||''' or '''||t_type||''' = ''ALL'')
group by geographicZoneId
) total

on gzz.id = total.geographicZoneId
left join
(select geographicZoneId, count(*) from facilities
join facility_types ft on ft.id = facilities.typeid
join programs_supported ps on ps.facilityId = facilities.id
join geographic_zones gz on gz.id = facilities.geographicZoneId
where ps.programId = '||t_programid ||'  and facilities.id in
(select facilityId from vaccine_reports where programId = '||t_programid ||'  )
and (ft.code = '''||t_type||''' or '''||t_type||''' = ''ALL'') 
group by geographicZoneId
) ever
on gzz.id = ever.geographicZoneId
left join
(select geographicZoneId, count(*) from facilities
join facility_types ft on ft.id = facilities.typeid
join programs_supported ps on ps.facilityId = facilities.id
join geographic_zones gz on gz.id = facilities.geographicZoneId
where  ps.programId = '||t_programid ||'  and facilities.id in
(select facilityId from vaccine_reports where periodId = '||in_periodid ||'  and programId = '||t_programid ||'
and status not in (''DRAFT'', ''SKIPPED'')  )
and (ft.code = '''||t_type||''' or '''||t_type||''' = ''ALL'') 
group by geographicZoneId
) period
on gzz.id = period.geographicZoneId order by gzz.name ) a ) b group by b.id';

RETURN QUERY EXECUTE qry;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
