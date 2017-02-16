with completeness_with_reporting_periods as
(
   select
      a.region_name,
      a.district_name,
      a.priod_id,
      a.period_name,
      a.period_start_date,
      a.geographiczoneid,
      a.fixed,
      a.outreach,
      a.session_total,
      a.target,
      a.expected,
      a.reported,
      a.ontime,
      a.late,
      trunc((a.reported::numeric / a.expected::numeric)*100, 2) percent_reported,
      trunc((a.late::numeric / a.reported::numeric)*100, 2) percent_late
   from
      (
         with temp as
         (
            select
               pp.id priod_id,
               pp.name period_name,
               pp.startdate::date period_start_date,
               z.id geographiczoneid,
               z.name district,
               f.name facility_name,
               f.code facility_code,
               to_char(COALESCE(vr.submissiondate::date, vr.createddate::date) , 'DD Mon YYYY') reported_date,
               COALESCE(vr.fixedimmunizationsessions, 0) fixed,
               COALESCE(vr.outreachimmunizationsessions, 0) outreach,
               COALESCE(z.catchmentpopulation, 0) target,
               CASE
                  WHEN
                     date_part('day'::text, COALESCE(vr.submissiondate::date, vr.createddate::date) - pp.enddate::date::timestamp without time zone) <= COALESCE(((
                     SELECT
                        configuration_settings.value
                     FROM
                        configuration_settings
                     WHERE
                        configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision
                     THEN
                        'T'::text
                     WHEN
                        COALESCE(date_part('day'::text, COALESCE(vr.submissiondate::date, vr.createddate::date) - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE(((
                        SELECT
                           configuration_settings.value
                        FROM
                           configuration_settings
                        WHERE
                           configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision
                        THEN
                           'L'::text
                        ELSE
                           'N'::text
               END
               AS reporting_status
                        from
                           programs_supported ps
                           left join
                              vaccine_reports vr
                              on vr.programid = ps.programid
                              and vr.facilityid = ps.facilityid
                           left outer join
                              processing_periods pp
                              on pp.id = vr.periodid
                           join
                              facilities f
                              on f.id = ps.facilityId
                           join
                              geographic_zones z
                              on z.id = f.geographicZoneId
                        where
                           ps.programId =
                           (
                              select
                                 id
                              from
                                 programs
                              where
                                 enableivdform = 't' limit 1
                           )
                           and pp.startdate::date >= '2016-01-01'::date
                           and pp.enddate::date <= '2016-12-31'::date
         )
         select
            vd.region_name,
            vd.district_name,
            priod_id,
            t.period_name,
            t.period_start_date,
            t.geographiczoneid,
            sum(fixed) fixed,
            sum(outreach) outreach,
            sum(fixed) + sum(outreach) session_total,
            sum(target) target,
            (
               select
                  count(*)
               from
                  requisition_group_members rgm
                  join
                     facilities f
                     on f.id = rgm.facilityid
                  join
                     programs_supported ps
                     on ps.facilityid = f.id
                  join
                     requisition_group_program_schedules rgs
                     on rgs.programid =
                     (
                        select
                           id
                        from
                           programs
                        where
                           enableivdform = 't' limit 1
                     )
                     and rgs.requisitiongroupid = rgm.requisitiongroupid
                     and rgs.scheduleid = 45
               where
                  f.geographiczoneid = t.geographiczoneid
                  and f.active = true
                  and f.sdp = true
            )
            expected,
            sum(
            case
               when
                  reporting_status IN
                  (
                     'T',
                     'L'
                  )
               then
                  1
               else
                  0
            end
) reported, sum(
            case
               when
                  reporting_status = 'T'
               then
                  1
               else
                  0
            end
) ontime, sum(
            case
               when
                  reporting_status = 'L'
               then
                  1
               else
                  0
            end
) late
         from
            temp t
            join
               vw_districts vd
               on vd.district_id = t.geographiczoneid 					--writeDistrictPredicate(params.getDistrict())
         where
            vd.district_id in
            (
               select
                  district_id
               from
                  vw_user_facilities
               where
                  user_id = 2
                  and program_id = fn_get_vaccine_program_id()
            )
         group by
            1,
            2,
            3,
            4,
            5,
            6
      )
      a
)
,
completness_with_nonreporting_periods as
(
   select
      c.geographiczoneid,
      periods.*,
      (
         select
            count(*)
         from
            requisition_group_members rgm
            join
               facilities f
               on f.id = rgm.facilityid
            join
               programs_supported ps
               on ps.facilityid = f.id
            join
               requisition_group_program_schedules rgs
               on rgs.programid =
               (
                  select
                     id
                  from
                     programs
                  where
                     enableivdform = 't' limit 1
               )
               and rgs.requisitiongroupid = rgm.requisitiongroupid
               and rgs.scheduleid = periods.scheduleid
         where
            f.geographiczoneid = c.geographiczoneid
            and f.active = true
            and f.sdp = true
      )
      expected
   from
      (
         select
            id,
            scheduleid,
            name period_name,
            startdate period_start_date
         from
            processing_periods pp
         where
            pp.startdate::date >= '2016-01-01'::date
            and pp.enddate::date <= '2016-12-31'::date
            AND pp.numberofmonths = 1
      )
      periods,
      (
         select distinct
            geographiczoneid
         from
            completeness_with_reporting_periods c
      )
      c
)
SELECT
   vd.region_name as regionName,
   vd.district_name as districtName,
   nonreporting.period_name as periodName,
   nonreporting.period_start_date as periodStartDate,
   nonreporting.geographiczoneid as geographicZoneId,
   COALESCE(c.fixed, 0) as fixed,
   COALESCE(c.outreach, 0) as outreach,
   COALESCE(c.session_total, 0) as sessionTotal,
   CASE
      WHEN
         c.target is null
      then
         z.catchmentpopulation
      ELSE
         c.target
   end
   as target, nonreporting.expected as expected, COALESCE(c.reported, 0) as reported, COALESCE(c.ontime, 0) as ontime, COALESCE(c.late, 0) late, COALESCE(c.percent_reported, 0) percentReported, COALESCE(c.percent_late, 0) percentLate,
   CASE
      WHEN
         c.geographiczoneid is null
      then
         'NONREPORTING'
      else
         'REPORTING'
   end
   as reportingStatus
FROM
   completness_with_nonreporting_periods nonreporting
   join
      geographic_zones z
      on z.id = nonreporting.geographiczoneid
   join
      vw_districts vd
      on vd.district_id = nonreporting.geographiczoneid
   left outer join
      completeness_with_reporting_periods c
      On c.geographiczoneid = nonreporting.geographiczoneid
      AND nonreporting.id = c.priod_id
order by
   1, 2, 4;