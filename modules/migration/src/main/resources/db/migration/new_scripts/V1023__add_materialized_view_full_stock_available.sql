DROP MATERIALIZED VIEW IF EXISTS public.vw_full_stock_availability_vw;

CREATE MATERIALIZED VIEW public.vw_full_stock_availability_vw AS
 SELECT  this_year,pr.name AS periodname,
    max(x.total) AS total,
    sum(x.fullstock) AS fullstock,
    round(sum(x.fullstock::numeric) / max(x.total)::numeric * 100::numeric, 2) AS percentageoffullstock
   FROM ( SELECT ( SELECT count(*) AS total
                   FROM facilities f
                     JOIN programs_supported ps ON f.id = ps.facilityid
                     JOIN facility_types ft ON f.typeid = ft.id
                     JOIN vw_districts d ON f.geographiczoneid = d.district_id
                     JOIN requisition_group_members m ON m.facilityid = f.id
                     JOIN requisition_group_program_schedules rps ON m.requisitiongroupid = rps.requisitiongroupid AND ps.programid = rps.programid
                  WHERE f.active = true AND ps.active = true AND ps.programid = (( SELECT fn_get_vaccine_program_id() AS fn_get_vaccine_program_id)) AND (ft.code::text <> ALL (ARRAY['dvs'::character varying::text, 'rvs'::character varying::text, 'cvs'::character varying::text])) AND ps.startdate <= (( SELECT processing_periods.startdate
                           FROM processing_periods
                          WHERE processing_periods.id = r.periodid))) AS total,
            r.facilityid,
            r.periodid,
            max(
                CASE
                    WHEN l.closingbalance > 0 THEN 1
                    ELSE 0
                END) AS available,
            max(
                CASE
                    WHEN l.closingbalance = 0 THEN 1
                    ELSE 0
                END) AS notavailable,
                CASE
                    WHEN max(
                    CASE
                        WHEN l.closingbalance = 0 THEN 1
                        ELSE 0
                    END) = 1 THEN 0
                    ELSE 1
                END AS fullstock,
            max(l.closingbalance) AS soh,date_part('year'::text, pr_1.startdate) this_year
           FROM vaccine_reports r
             JOIN vaccine_report_logistics_line_items l ON r.id = l.reportid
             JOIN processing_periods pr_1 ON pr_1.id = r.periodid AND pr_1.numberofmonths = 1
          WHERE r.status::text <> 'DRAFT'::text
          GROUP BY r.facilityid, r.periodid,pr_1.startdate
          ORDER BY r.facilityid,pr_1.startdate) x
     JOIN processing_periods pr ON x.periodid = pr.id
  GROUP BY pr.name, pr.startdate,x.this_year
  ORDER BY pr.startdate
WITH DATA;



