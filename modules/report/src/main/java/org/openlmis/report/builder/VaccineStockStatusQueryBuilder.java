package org.openlmis.report.builder;


import org.openlmis.report.model.params.VaccineStockStatusParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.multiProductFilterBy;


public class VaccineStockStatusQueryBuilder {

    public String getQuery(Map params) {

        VaccineStockStatusParam filter = (VaccineStockStatusParam) params.get("filterCriteria");

        return (
                "  WITH Q as (  SELECT  x.* ,   " +
                "  (select isaValue from stock_requirements where facilityId = x.facilityId and productId = X.productId)    " +
                "  FROM (    " +
                "  SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*    " +
                "  FROM  (    " +
                "  SELECT  facilityId, s.productId, f.name facilityName,    " +
                "  p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,    " +
                "  e.modifiedDate::timestamp lastUpdate    " +
                "  FROM stock_cards s    " +
                "  JOIN stock_card_entries e ON e.stockCardId = s.id     " +
                "  JOIN program_products pp ON s.productId = pp.productId    " +
                "  JOIN programs ON pp.programId = programs.id    " +
                "  JOIN products p ON pp.productId = p.id      " +
                "  JOIN facilities f ON s.facilityId = f.id    " +
                "  JOIN facility_types  ON f.typeId = facility_types.Id   " +
                "  " + writePredicates(filter) +
                "   ORDER BY e.modifiedDate ) t) x    " +
                "   WHERE  x.r <= 1    " +
                "   ORDER BY facilityId,productId )  " +
                "   SELECT facilityId, productId, facilityName,product,lastUpdate,soh,isaValue,   " +
                "   CASE WHEN isaValue > 0 THEN  ROUND((coalesce(soh,0) / coalesce(isaValue,10)),0) else 0 end as mos  " +
                "   FROM Q  "
        );

    }

    private static String writePredicates(VaccineStockStatusParam params) {

                String predicate = " ";

                predicate += " where programs.id = " + params.getProgram();
                predicate += " and e.modifiedDate::DATE <= #{filterCriteria.statusDate}::date";
                predicate += " and pp.productCategoryId = " + params.getProductCategory();

                String facilityLevel = params.getFacilityLevel();
                if (facilityLevel.isEmpty()
                        || facilityLevel.equalsIgnoreCase("cvs")
                        || facilityLevel.equalsIgnoreCase("rvs")
                        || facilityLevel.equalsIgnoreCase("dvs")) {
                    predicate += " and facility_types.code = #{filterCriteria.facilityLevel} ";
                } else {
                    predicate += " and facility_types.code NOT IN ('cvs','rvs','dvs') ";

                }

                return predicate;

    }


}
