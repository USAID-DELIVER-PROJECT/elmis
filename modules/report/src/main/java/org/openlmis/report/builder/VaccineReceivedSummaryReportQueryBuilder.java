package org.openlmis.report.builder;

import org.openlmis.report.model.params.CompletenessAndTimelinessReportParam;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.params.VaccineReceivedSummaryReportParam;

import java.util.Date;
import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
public class VaccineReceivedSummaryReportQueryBuilder {


    private static String sql;

    public static String getReceivedConsignmentSummaryData(Map params) {

        VaccineReceivedSummaryReportParam param = getParamsValues(params);

        sql = "                SELECT vw.region_name region, vw.district_name district,\n" +
                "                VW.DISTRICT_ID districtId,f.id facilityId,p.id productId,f.name facilityName, p.primaryname product,\n" +
                "                d.modifiedDate receivedDate,SUM(dl.quantity) quantityReceived \n" +
                "                FROM vaccine_distribution_line_items dl\n" +
                "                JOIN vaccine_distributions d on d.id=dl.distributionid \n" +
                "                JOIN facilities f on f.id=d.fromFacilityId \n" +
                "                JOIN facility_types ft on f.typeid = ft.id\n" +
                "                JOIN products p on p.id=dl.productId\n" +
                "                join processing_periods pp on pp.id = d.periodid \n" +
                "                JOIN vw_districts vw ON f.geographicZoneId = vw.district_id  \n" +
           /*     "                WHERE toFacilityId= (SELECT f.id FROM users U, facilities F \n" +
                "                WHERE U.facilityId = F.id AND U.id = " + param.getUserId() + " AND f.active = TRUE AND f.virtualFacility = FALSE)" +
                "                AND   pp.startdate::date >='" + param.getPeriodStart() + "'  and pp.enddate::date <=   '" + param.getPeriodEnd() + "' " +
                writeDistrictPredicate(param.getDistrict()) +*/
                "                group by 1,2,3,4 ,5,6,7,8\n" +
                "                order by vw.region_name, productId \n";

        return sql;

    }

    private static String writeDistrictPredicate(Long zone) {

        String predicate = " ";
        if (zone != 0 && zone != null) {
            predicate = " AND (district_id = " + zone + " or zone_id = " + zone + " or region_id = " + zone + " or parent = " + zone + ")";
        }
        return predicate;
    }

    public static VaccineReceivedSummaryReportParam getParamsValues(Map param) {
        return (VaccineReceivedSummaryReportParam) param.get("filterCriteria");
    }
}
