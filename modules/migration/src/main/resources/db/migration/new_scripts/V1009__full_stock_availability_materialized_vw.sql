

DROP MATERIALIZED VIEW IF EXISTS vw_full_stock_availability_vw CASCADE;
CREATE MATERIALIZED VIEW vw_full_stock_availability_vw AS

SELECT pr.name periodName,

Max(total) total,sum(fullstock) fullstock,
ROUND(sum(fullstock::numeric) /(Max(total)) * 100,2) percentageOfFullStock
 from(

 SELECT
(
select count(*) total from facilities f
INNER JOIN programs_supported ps ON f.id = ps.facilityId
INNER JOIN facility_types ft ON f.typeid = ft.id
INNER JOIN vw_districts d ON f.geographiczoneid = d.district_id
join requisition_group_members m on m.facilityId = f.id
join requisition_group_program_schedules rps on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId
WHERE f.active= true and ps.active = true and ps.programID = (select fn_get_vaccine_program_id())
and ft.code not in ('dvs','rvs','cvs')
and ps.startdate <= (SELECT startDate from processing_periods where id =r.periodID)

) total,

 FACILITYid,periodID, MAX(case when l.closingbalance > 0 then 1 else 0 end ) available,
 MAX(case when l.closingbalance = 0 then 1 else 0 end ) notAvailable,
CASE WHEN( MAX(case when l.closingbalance = 0 then 1 else 0 end ) ) = 1 then 0 ELSE 1 end as fullStock,
  MAX(l.closingbalance) soh
from vaccine_reports r
join vaccine_report_logistics_line_items l on r.id = l.reportid
join processing_periods pr on pr.id = r.periodid and pr.numberofmonths = 1
where
 --productid = 2413::INT
 status <> 'DRAFT'   AND extract(year from pr.startdate)= extract(year from now())
 GROUP BY FACILITYID,periodID
 ORDER BY FACILITYID

 )X
 JOIN processing_periods pr ON periodid = pr.id
GROUP BY pr.name,pr.startdate
order by pr.startdate

WITH DATA;









































