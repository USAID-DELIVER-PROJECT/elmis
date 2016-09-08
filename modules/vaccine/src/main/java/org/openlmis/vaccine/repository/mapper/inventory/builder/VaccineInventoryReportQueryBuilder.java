package org.openlmis.vaccine.repository.mapper.inventory.builder;

import java.util.Date;
import java.util.Map;

/**
 * Created by chrispinus on 10/29/15.
 */
public class VaccineInventoryReportQueryBuilder {

    public static final String getDistributionCompleteness(Map params) {
        Long zone = (Long) params.get("districtId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");


        String sql = "select vd.region_name, vd.district_name,f.id facilityId, f.name facilityname,pp.id periodId,pp.name period,\n" +
                "(Select count(facilityid) from requisition_group_members where requisitiongroupid=rg.id) as expected,\n" +
                "(SELECT COUNT(*) FROM (SELECT DISTINCT tofacilityid FROM vaccine_distributions where fromfacilityid=f.id and periodid=pp.id) AS temp) as issued,\n" +
                "f.id facilityid,\n" +
                "pp.id periodid\n" +
                "from processing_periods pp\n" +
                "join processing_schedules ps on ps.id=pp.scheduleid\n" +
                "join requisition_group_program_schedules rgps on rgps.scheduleid=ps.id\n" +
                "join requisition_groups rg on rg.id=rgps.requisitiongroupid\n" +
                "join supervisory_nodes sn on sn.id=rg.supervisorynodeid\n" +
                "join facilities f on f.id=sn.facilityid\n" +
                "join facility_types ft on ft.id=f.typeid\n" +
                "left join vw_districts vd on vd.district_id=f.geographiczoneid\n" +
                "where ft.code='dvs' and pp.startdate::DATE >='" + startDate + "' and pp.enddate::DATE <='" + endDate + "' " + writeDistrictPredicate(zone) + " \n" +
                " order by vd.region_name,vd.district_name, pp.id";

        return sql;

    }

    public static final String getTotalDistributionCompleteness(Map params) {
        Long zone = (Long) params.get("districtId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");


        String sql = "select count(f.id) " +
                "from processing_periods pp\n" +
                "join processing_schedules ps on ps.id=pp.scheduleid\n" +
                "join requisition_group_program_schedules rgps on rgps.scheduleid=ps.id\n" +
                "join requisition_groups rg on rg.id=rgps.requisitiongroupid\n" +
                "join supervisory_nodes sn on sn.id=rg.supervisorynodeid\n" +
                "join facilities f on f.id=sn.facilityid\n" +
                "join facility_types ft on ft.id=f.typeid\n" +
                "left join vw_districts vd on vd.district_id=f.geographiczoneid\n" +
                "where ft.code='dvs' and pp.startdate::DATE >='" + startDate + "' and pp.enddate::DATE <='" + endDate + "' " + writeDistrictPredicate(zone) + " \n";

        return sql;

    }

    private static String writeDistrictPredicate(Long zone) {

        String predicate = " ";
        if (zone != 0 && zone != null) {
            predicate = " AND (vd.district_id = " + zone + " or vd.zone_id = " + zone + " or vd.region_id = " + zone + " or vd.parent = " + zone + ")";
        }
        return predicate;
    }
}
