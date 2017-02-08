package org.openlmis.report.builder;

import org.openlmis.report.model.params.DistributionSummaryReportParam;

import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
public class DistributionSummaryQueryBuilder {

    public String getQuery(Map params) {

        DistributionSummaryReportParam filter = (DistributionSummaryReportParam) params.get("filterCriteria");

        return "  select max(vw.region_name) region, MAX(vw.district_name) district,max(f.id) facilityId,max(p.id) productId,max(d.periodid) period, f.name facilityName, p.primaryname product, sum(dl.quantity) quantityIssued \n" +
                " from vaccine_distribution_line_items dl\n" +
                " join vaccine_distributions d on d.id=dl.distributionid\n" +
                " join facilities f on f.id=d.tofacilityid\n" +
                " join products p on p.id=dl.productid\n" +
                " left join vw_districts vw ON f.geographicZoneId = vw.district_id  " +
                " group by f.name, p.primaryname\n" +
                " order by f.name, productid ";

         //TODO: Add filter
    }


    private static String writePredicates(DistributionSummaryReportParam param) {

        String predicate = "";

        predicate = " WHERE fromFacilityId = " + param.getFacility();
        predicate += "  and vd.modifiedDate >=#{filterCriteria.startDate}::date";
        predicate += " and vd.modifiedDate <=#{filterCriteria.endDate}::date";

        return predicate;

    }
}
