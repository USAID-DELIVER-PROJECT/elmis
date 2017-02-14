package org.openlmis.report.builder;

import org.openlmis.report.model.params.DistributionSummaryReportParam;

import java.util.Date;
import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
public class DistributionSummaryQueryBuilder {

    public String getQuery(Map params) {

        DistributionSummaryReportParam filter = (DistributionSummaryReportParam) params.get("filterCriteria");

        return "              SELECT vw.region_name region, vw.district_name district,f.id facilityId,p.id productId,max(d.periodid) period, f.name facilityName, p.primaryname product,\n" +
                "                 SUM(dl.quantity) quantityIssued \n" +
                "                 from vaccine_distribution_line_items dl\n" +
                "                 join vaccine_distributions d on d.id=dl.distributionid\n" +
                "                 join facilities f on f.id=d.tofacilityid\n" +
                "                 join products p on p.id=dl.productid\n" +
                "                 join vw_districts vw ON f.geographicZoneId = vw.district_id  \n" +
                               //   writePredicates(filter)+
                "                 group by 1,2,3,4\n" +
                "                 order by vw.region_name, productid ";

    }


    private static String writePredicates(DistributionSummaryReportParam param) {

        String predicate = "";

        predicate = " WHERE fromFacilityId = " + param.getFacility();
        predicate += "  and vd.modifiedDate >=#{filterCriteria.startDate}::date";
        predicate += " and vd.modifiedDate <=#{filterCriteria.endDate}::date";

        return predicate;

    }


    private static String sql;

    public static String getDistributionQueryData(Map params){

       sql = "   SELECT vw.region_name region, vw.district_name district,f.id facilityId,p.id productId,max(d.periodid) period, f.name facilityName, p.primaryname product,\n" +
               " SUM(dl.quantity) quantityIssued " +
               " FROM vaccine_distribution_line_items dl " +
               " JOIN vaccine_distributions d on d.id=dl.distributionid " +
               " JOIN facilities f on f.id=d.tofacilityId  " +
               " JOIN products p on p.id=dl.productId" +
               " JOIN vw_districts vw ON f.geographicZoneId = vw.district_id  " +
                 writePredicate(params) +
               "  group by 1,2,3,4  " +
               " order by vw.region_name, productid  ";

       return sql;


    }


    private static String writePredicate(Map params) {

        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");
        Long doseId = (Long) params.get("doseId");
        Long userId = (Long) params.get("userId");

        String predicate = "";
        predicate = " geographic_zone_id in (select district_id from vw_user_facilities where user_id ="+userId+" and program_id = fn_get_vaccine_program_id())  ";
        predicate += " AND   vd.modifiedDate::date >= '"+startDate+"' and  vd.modifiedDate::date <= '"+endDate+"'\n";
        //predicate +=" AND  i.product_id = " +productId +" ";

        if (zone != 0 && zone != null) {
            predicate += " AND (district_id = "+zone+" or zone_id = "+zone+" or region_id = "+zone+" or parent = "+zone+")";
        }


     //   predicate += " AND dose_id = "+ doseId;

        return predicate;
    }
}
