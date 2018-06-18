package org.openlmis.report.builder;

public class DashboardReportingRateQueryBuilder {

    public static String getQuery(){
        String query="with reportingRate as(\n" +
                "select gzz.id, gzz.name, \n" +
                "COALESCE(prevexpected.count,0) prevexpected, \n" +
                "COALESCE(expected.count,0) expected, \n" +
                "COALESCE(total.count,0) total, \n" +
                "COALESCE(ever.count,0) as ever, \n" +
                "COALESCE(prevperiod.count,0) as prevperiod ,\n" +
                "COALESCE(period.count,0) as period   \n" +
                "     from   \n" +
                "     geographic_zones gzz \n" +
                "left join  \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join programs_supported ps on ps.facilityId = facilities.id  \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     join requisition_group_members rgm on rgm.facilityId = facilities.id  \n" +
                "     join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId   \n" +
                "     join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = '112'  \n" +
                "     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = 3  \n" +
                "     group by geographicZoneId \n" +
                "     ) prevexpected \n" +
                "\t  on gzz.id = prevexpected.geographicZoneId \n" +
                "     left join  \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join programs_supported ps on ps.facilityId = facilities.id  \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     join requisition_group_members rgm on rgm.facilityId = facilities.id  \n" +
                "     join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId   \n" +
                "     join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = '114'  \n" +
                "     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = 3  \n" +
                "     group by geographicZoneId \n" +
                "     ) expected  \n" +
                "     on gzz.id = expected.geographicZoneId  \n" +
                "     left join  \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     where gz.levelId = (select max(id) from geographic_levels)   \n" +
                "     group by geographicZoneId \n" +
                "     ) total  \n" +
                "     on gzz.id = total.geographicZoneId  \n" +
                "     left join   \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join programs_supported ps on ps.facilityId = facilities.id  \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     where ps.programId = 3 and facilities.id in   \n" +
                "    (select facilityId from requisitions where programId = 3 )  \n" +
                "    group by geographicZoneId \n" +
                "     ) ever  \n" +
                "     on gzz.id = ever.geographicZoneId  \n" +
                "\t left join  \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join programs_supported ps on ps.facilityId = facilities.id  \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     where  ps.programId = 3 and facilities.id in   \n" +
                "     (select facilityId from requisitions where periodId = '112' and programId = 3 and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false )  \n" +
                "     group by geographicZoneId \n" +
                "     ) prevperiod \n" +
                "     on gzz.id = prevperiod.geographicZoneId\n" +
                "     left join  \n" +
                "     (select geographicZoneId, count(*) from facilities   \n" +
                "     join programs_supported ps on ps.facilityId = facilities.id  \n" +
                "     join geographic_zones gz on gz.id = facilities.geographicZoneId  \n" +
                "     where  ps.programId = 3 and facilities.id in   \n" +
                "     (select facilityId from requisitions where periodId = '113' and programId = 3 and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false )  \n" +
                "     group by geographicZoneId \n" +
                "     ) period \n" +
                "     on gzz.id = period.geographicZoneId order by gzz.name\n" +
                "\t)\n" +
                "\tselect r.name, \n" +
                "\tCASE WHEN COALESCE(r.prevexpected, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(r.prevperiod, 0)::numeric / COALESCE(r.prevexpected, 0)) * 100, 2)  END prev,\n" +
                "   CASE WHEN COALESCE(r.expected, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(r.period, 0)::numeric / COALESCE(r.expected, 0)) * 100, 2)  END \"current\",\n" +
                "case \n" +
                "when (case WHEN COALESCE(r.expected, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(r.period, 0)::numeric / COALESCE(r.expected, 0)) * 100, 2) end)>=80 then 'good' \n" +
                "when (case WHEN COALESCE(r.expected, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(r.period, 0)::numeric / COALESCE(r.expected, 0)) * 100, 2) end)>=60 then 'normal' \n" +
                "when (case WHEN COALESCE(r.expected, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(r.period, 0)::numeric / COALESCE(r.expected, 0)) * 100, 2) end)<60 then 'bad' \n" +
                "\t\t\t   end status\n" +
                "\tfrom reportingRate r";
        return query;
    }
}
