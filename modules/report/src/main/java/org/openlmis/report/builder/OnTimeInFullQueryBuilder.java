package org.openlmis.report.builder;

import org.openlmis.report.model.params.OnTimeInFullReportParam;

import java.util.Map;
import java.util.Objects;

/**
 * Created by hassan on 1/29/17.
 */

public class OnTimeInFullQueryBuilder {

    public String getQuery(Map params) {

        OnTimeInFullReportParam filter = (OnTimeInFullReportParam) params.get("filterCriteria");
        System.out.println(filter.getStartDate());
        Long userId = (Long) params.get("userId");

        return "               SELECT  X.region, x.district, x.storeName, y.quantityRequested,x.quantityReceived,requestedDate, distributiondate receivedDate ,\n" +
                "              x.orderId ,y.orderId,x.product \n" +
                "              FROM   \n" +
                "               (    \n" +
                "               SELECT p.id productId,p.primaryName product,M.region_name region, M.district_name district,f.name storeName, dl.quantity QuantityReceived,d.modifiedDate distributiondate,d.orderId \n" +
                "               FROM vaccine_distributions d   \n" +
                "               JOIN vaccine_distribution_line_items dl on d.id = dl.distributionId   \n" +
                "               JOIN products p on p.id = dl.productId    \n" +
                "               JOIN program_products  pp ON pp.productId = p.id    \n" +
                "               JOIN product_categories pc ON pp.productCategoryId = PC.ID   \n" +
                "               JOIN facilities f ON toFacilityId = f.id  \n" +
                "               JOIN vw_districts M ON f.geographiczoneId = M.district_id  \n" +
                "               JOIN facility_types  ON f.typeId = facility_types.Id   \n" +
                "               "+writePredicates(filter)+"   AND  d.status = 'RECEIVED'   " +
                "               AND d.modifiedDate::DATE <= #{filterCriteria.endDate}::Date and  d.modifiedDate::DATE >= #{filterCriteria.startDate}::Date " +
                /*"               AND extract('year' from d.modifiedDate)::int = #{filterCriteria.year}::int and extract('month' from d.modifiedDate)::int= #{filterCriteria.periods}::int  "*/
                 "              AND M.district_id in (select district_id from vw_user_facilities where user_id = '" + userId + "'::INT and program_id = fn_get_vaccine_program_id())  "+
                "               order by M.region_name   \n" +
                "               ) x INNER JOIN (     \n" +
                "               SELECT LI.PRODUCTID,p.primaryName product, M.region_name region, M.district_name district,f.name storeName, quantityRequested,o.createddate requestedDate,o.id orderId\n" +
                "               FROM vaccine_order_requisitions o\n" +
                "               JOIN vaccine_order_requisition_line_items li on li.orderid = o.id   \n" +
                "               JOIN products p on p.id = li.productId     \n" +
                "               JOIN program_products  pp ON pp.productId = p.id   \n" +
                "               JOIN product_categories pc ON pp.productCategoryId = PC.ID    \n" +
                "               JOIN facilities f ON facilityId = f.id  \n" +
                "               JOIN vw_districts M ON f.geographiczoneId = M.district_id  \n" +
                "               JOIN facility_types  ON f.typeId = facility_types.Id     \n" +
                "               "+writePredicates(filter)+"  and o.status = 'ISSUED'  " +
                "                AND M.district_id in (select district_id from vw_user_facilities where user_id = '" + userId + "'::INT and program_id = fn_get_vaccine_program_id())  "+
                "               order by M.region_name ASC  \n" +
                "               ) y ON (x.orderId = y.orderId and X.productId = y.productId)" +
                "                 ORDER BY region ASC  ";


    }

        private static String writePredicates(OnTimeInFullReportParam params) {

                String predicate = " ";

                predicate += " where pp.productCategoryId = " + params.getProductCategory();
            /* if(Objects.equals("0", params.getProducts())){
                 System.out.println("reached here");
                 predicate +="and p.id in (select productId from program_products where productCategoryID = "+params.getProductCategory()+") and active = true";
             }else
                 predicate += " and p.id IN( " + params.getProducts()+")";*/
                 String facilityLevel = params.getFacilityLevel();
                 predicate += " and facility_types.code = #{filterCriteria.facilityLevel}::text ";

                return predicate;

        }


}
