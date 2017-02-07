package org.openlmis.report.builder;

import org.openlmis.report.model.params.DistributionSummaryReportParam;

import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
public class DistributionSummaryQueryBuilder {

    public  String getQuery(Map params) {

        DistributionSummaryReportParam filter = (DistributionSummaryReportParam) params.get("filterCriteria");

        return " \n" +
                "                  SELECT * FROM crosstab( \n" +
                "                 ' SELECT region, storeName,product,sum(quantity) quantityIssued FROM (  \n" +
                "                  SELECT m.region_name region, f.name storeName,toFacilityId facilityId,p.primaryName product,li.quantity  \n" +
                "                  FROM  vaccine_distributions vd     \n" +
                "                  JOIN vaccine_distribution_line_items li ON vd.id=li.distributionId  \n" +
                "                  JOIN products p on p.id = li.productId       \n" +
                "                  JOIN program_products  pp ON pp.productId = p.id       \n" +
                "                  JOIN product_categories pc ON pp.productCategoryId = PC.ID      \n" +
                "                  JOIN facilities f ON vd.tofacilityId = f.id         \n" +
                "                  JOIN vw_districts M ON f.geographiczoneId = M.district_id    \n" +
                "                   order by pc.displayOrder      \n" +
                "                   ) x    \n" +
                "                   group by 1,2,3   \n" +
                "                   order by region',\n" +
                "                    'VALUES (''BCG''), (''OPV''), (''DTP-HepB-Hib''), (''MR''),(''PCV-13''), (''Rota'')'\n" +
                "                   )ct(region text,storeName text, \"BCG\" text,\"OPV\" text, \"DTP\" text, \"MR\" text,\"PCV_13\" text,\"ROTA\" text) ";




        /*
        return
               "                  SELECT facilityId,region, storeName,product, sum(quantity) OVER (PARTITION BY facilityId, productId) as quantityIssued\n" +
               "                  FROM (  \n" +
               "                  SELECT m.region_name region, f.name storeName,toFacilityId facilityId,p.primaryName product,li.quantity ,p.id productId \n" +
               "                  FROM  vaccine_distributions vd     \n" +
               "                  JOIN vaccine_distribution_line_items li ON vd.id=li.distributionId  \n" +
               "                  JOIN products p on p.id = li.productId       \n" +
               "                  JOIN program_products  pp ON pp.productId = p.id       \n" +
               "                  JOIN product_categories pc ON pp.productCategoryId = PC.ID      \n" +
               "                  JOIN facilities f ON vd.tofacilityId = f.id         \n" +
               "                  JOIN vw_districts M ON f.geographiczoneId = M.district_id    \n" +
               "                   WHERE   pc.code = 'Vaccine'\n" +
               "                   ORDER BY p.id,pc.displayOrder      \n" +
               "                   ) x \n" +
               "                   order by region  ";*/




 /*
        return " WITH Q AS(  SELECT storeName,region,product,sum(quantity) quantityIssued FROM (  \n" +
                "  SELECT m.region_name region, f.name storeName,toFacilityId facilityId,p.primaryName product,li.quantity   \n" +
                "  FROM  vaccine_distributions vd     \n" +
                "  JOIN vaccine_distribution_line_items li ON vd.id=li.distributionId   \n" +
                "  JOIN products p on p.id = li.productId        \n" +
                "  JOIN program_products  pp ON pp.productId = p.id       \n" +
                "  JOIN product_categories pc ON pp.productCategoryId = PC.ID      \n" +
                "  JOIN facilities f ON vd.tofacilityId = f.id         \n" +
                "  JOIN vw_districts M ON f.geographiczoneId = M.district_id    \n" +
                    "order by pc.displayOrder "+
                "   ) x    \n" +
                "   GROUP BY storeName,region,product    \n" +
                "   ORDER BY storeName )" +
                "   SELECT * FROM Q";*/
    }


    private static String writePredicates(DistributionSummaryReportParam param) {

        String predicate = "";

        predicate = " WHERE fromFacilityId = " + param.getFacility();
        predicate += "  and vd.modifiedDate >=#{filterCriteria.startDate}::date";
        predicate += " and vd.modifiedDate <=#{filterCriteria.endDate}::date";

        return predicate;

    }
}
