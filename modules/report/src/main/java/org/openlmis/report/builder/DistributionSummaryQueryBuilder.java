package org.openlmis.report.builder;

import org.openlmis.report.model.params.DistributionSummaryReportParam;

import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
public class DistributionSummaryQueryBuilder {

    public  String getQuery(Map params) {

        DistributionSummaryReportParam filter = (DistributionSummaryReportParam) params.get("filterCriteria");

        return "  SELECT region, storeName,product,sum(quantity) quantityIssued FROM (   " +
                "  SELECT m.region_name region, f.name storeName,toFacilityId facilityId,p.primaryName product,li.quantity   " +
                "  FROM  vaccine_distributions vd     " +
                "  JOIN vaccine_distribution_line_items li ON vd.id=li.distributionId   " +
                "  JOIN products p on p.id = li.productId        " +
                "  JOIN program_products  pp ON pp.productId = p.id       " +
                "  JOIN product_categories pc ON pp.productCategoryId = PC.ID      " +
                "  JOIN facilities f ON vd.tofacilityId = f.id         " +
                "  JOIN vw_districts M ON f.geographiczoneId = M.district_id    " +
                "  where fromFacilityId = 19077 "+
                 //   writePredicates(filter) +
                "   order by pc.displayOrder      " +
                "   ) x    " +
                "   group by region,storeName,product    " +
                "   order by storeName";

    }


    private static String writePredicates(DistributionSummaryReportParam param) {

        String predicate = "";

        predicate = " WHERE fromFacilityId = " + param.getFacility();
        predicate += "  and vd.modifiedDate >=#{filterCriteria.startDate}::date";
        predicate += " and vd.modifiedDate <=#{filterCriteria.endDate}::date";

        return predicate;

    }
}
