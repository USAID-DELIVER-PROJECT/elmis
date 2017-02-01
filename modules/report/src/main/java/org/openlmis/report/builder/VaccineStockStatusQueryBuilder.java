package org.openlmis.report.builder;


import org.openlmis.report.model.params.VaccineStockStatusParam;

import java.util.Map;

public class VaccineStockStatusQueryBuilder {

    public String getQuery(Map params) {

        VaccineStockStatusParam filter = (VaccineStockStatusParam) params.get("filterCriteria");

        return (
                        "   WITH Q AS (  SELECT  x.* , r.isaValue, \n" +
                        "   case when x.soh > r.maximumstock then 1 else 0 end as blue,\n" +
                        "   case when x.soh <= maximumstock AND x.soh  >= reorderlevel then 1 else 0 end as green,\n" +
                        "   case when x.soh < reorderlevel AND x.soh >= r.bufferstock then 1 else 0 end as yellow,\n" +
                        "   case when  x.soh >= r.bufferstock then 1 else 0 end as adequacy,\n" +
                        "   (          " +
                        "   select fn_get_vaccine_stock_color(r.maximumstock::int, reorderlevel::int, bufferstock::int, x.soh::int)\n" +
                        "    )  color           " +
                        "    FROM (               " +
                        "    SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*    \n" +
                        "    FROM  (                             " +
                        "    SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
                        "    p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,    \n" +
                        "    e.modifiedDate::timestamp lastUpdate   \n" +
                        "    FROM stock_cards s   \n" +
                        "    JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
                        "    JOIN program_products pp ON s.productId = pp.productId    \n" +
                        "    JOIN programs ON pp.programId = programs.id    \n" +
                        "    JOIN products p ON pp.productId = p.id      \n" +
                        "    JOIN facilities f ON s.facilityId = f.id  \n" +
                        "    JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
                        "    JOIN facility_types  ON f.typeId = facility_types.Id   \n" +
                        "  " + writePredicates(filter) +
                        "    ORDER BY e.modifiedDate ) t) x \n" +
                        "    JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid\n" +
                        "    WHERE  x.r <= 1 and r.year = (SELECT date_part('YEAR', #{filterCriteria.statusDate}::date ))         " +
                        "    ORDER BY facilityId,productId )  \n" +
                        "    SELECT facilityId,districtId,regionId, productId, facilityName,district,region,product,lastUpdate,soh,isaValue,   \n" +
                        "    CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as mos,color, adequacy," +
                        "    sum(adequacy) OVER (PARTITION BY facilityId) adequacy2, count(productId) OVER (PARTITION BY facilityId) total\n   " +
                        "    FROM Q  " +
                             " ORDER BY region ASC "
                );

    }

    private static String writePredicates(VaccineStockStatusParam params) {

        String predicate = " ";

        predicate += " where programs.id = " + params.getProgram();
        predicate += " and e.modifiedDate::DATE <= #{filterCriteria.statusDate}::date";
        predicate += " and pp.productCategoryId = " + params.getProductCategory();
        predicate += " and facilityId = ANY (#{filterCriteria.facilityIds}::INT[])";

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
