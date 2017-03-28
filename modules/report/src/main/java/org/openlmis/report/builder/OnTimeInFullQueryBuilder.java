package org.openlmis.report.builder;

import org.openlmis.report.model.params.OnTimeInFullReportParam;

import java.util.Map;

/**
 * Created by hassan on 1/29/17.
 */

public class OnTimeInFullQueryBuilder {

    public String getQuery(Map params) {

        OnTimeInFullReportParam filter = (OnTimeInFullReportParam) params.get("filterCriteria");
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
                "               AND d.modifiedDate::date >= #{filterCriteria.startDate}::date and d.modifiedDate::date <= #{filterCriteria.endDate}::date " +
                 "              AND M.district_id in (select district_id from vw_user_facilities where user_id = '" + userId + "'::INT and program_id = fn_get_vaccine_program_id())  "+
                "               order by M.region_name   \n" +
                "               ) x LEFT JOIN (     \n" +
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
                    predicate += " and p.id = " + params.getProduct();
                 String facilityLevel = params.getFacilityLevel();
                 predicate += " and facility_types.code = #{filterCriteria.facilityLevel}::text ";

                return predicate;

        }


}
