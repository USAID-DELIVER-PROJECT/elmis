package org.openlmis.report.builder;

import org.openlmis.report.model.params.MinMaxVaccineReportParam;

import java.util.Map;

/**
 * Created by hassan on 2/2/17.
 */
public class MinMaxVaccineReportQueryBuilder {

    public String getQuery(Map params) {

        MinMaxVaccineReportParam filter = (MinMaxVaccineReportParam) params.get("filterCriteria");

        return  "    SELECT m.region_name region, f.name storeName, sr.isaValue MinimumStock, sr.MaximumStock,p.primaryName product,    " +
                "    sc.totalquantityonhand AS soh   " +
                "    FROM  stock_requirements sr\n" +
                "    JOIN products p on p.id = sr.productId        " +
                "    JOIN program_products  pp ON pp.productId = p.id      " +
                "    JOIN product_categories pc ON pp.productCategoryId = PC.ID      " +
                "    JOIN facilities f ON facilityId = f.id     " +
                "    JOIN vw_districts M ON f.geographiczoneId = M.district_id   " +
                "    JOIN facility_types  ON f.typeId = facility_types.Id   " +
                "    JOIN stock_cards sc ON sr.facilityId = SC.facilityId and sc.productId = SR.productId  " +
                           writePredicates(filter)+
                "     ORDER BY m.region_name asc  ";

    }


    private static String writePredicates(MinMaxVaccineReportParam params) {

        String predicate = " ";
        predicate += " where sr.programId = " + params.getProgram();
        predicate += "  and pp.productCategoryId = " + params.getProductCategory();
        predicate += " and pp.productId = " + params.getProduct();
        String facilityLevel = params.getFacilityLevel();
        predicate += " and facility_types.code = #{filterCriteria.facilityLevel}::text ";
        predicate += " and year =" + params.getYear();

        return predicate;

    }


}
