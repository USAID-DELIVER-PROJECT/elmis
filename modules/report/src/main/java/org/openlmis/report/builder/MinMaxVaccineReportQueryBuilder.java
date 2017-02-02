package org.openlmis.report.builder;

import org.openlmis.report.model.params.MinMaxVaccineReportParam;

import java.util.Map;

/**
 * Created by hassan on 2/2/17.
 */
public class MinMaxVaccineReportQueryBuilder {

    public String getQuery(Map params) {

        MinMaxVaccineReportParam filter = (MinMaxVaccineReportParam) params.get("filterCriteria");

        return
                "                               select m.region_name region, f.name storeName, sr.isaValue MinimumStock, sr.MaximumStock,p.primaryName product from  stock_requirements sr\n" +
                        "                               JOIN products p on p.id = sr.productId     \n" +
                        "                               JOIN program_products  pp ON pp.productId = p.id   \n" +
                        "                               JOIN product_categories pc ON pp.productCategoryId = PC.ID    \n" +
                        "                               JOIN facilities f ON facilityId = f.id  \n" +
                        "                               JOIN vw_districts M ON f.geographiczoneId = M.district_id \n" +
                        "                               JOIN facility_types  ON f.typeId = facility_types.Id \n" +
                        writePredicates(filter) +
                        "                               order by m.region_name asc  ";


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
