package org.openlmis.report.builder;


import org.openlmis.report.model.params.VaccineStockStatusParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.multiProductFilterBy;


public class VaccineStockStatusQueryBuilder {

    public String getQuery(Map params) {

        VaccineStockStatusParam filter = (VaccineStockStatusParam) params.get("filterCriteria");

        BEGIN();

        SELECT(" MAX(f.id) facilityId,MAX(f.name) facilityName,MAX(facility_types.name) facilityType,geographic_zones.name district,p.primaryName product,  " +
                "  COALESCE(SUM(e.quantity),0) as soh,  " +
                "e.createdDate::date lastUpdate, " +
                "ROUND((MAX(f.catchmentpopulation) * (COALESCE(sum(whoRatio),0) / 100) * COALESCE(sum(dosesPerYear),0) * sum(wastageFactor) / 12   " +
                "* (1 + sum(bufferPercentage) / 100) + sum(adjustmentValue) ),0) monthlyStock  ");
        FROM("   facilities f  " +
                "inner join facility_types ON f.typeid = facility_types.id  " +
                "inner join geographic_zones on geographic_zones.id = f.geographiczoneid  " +
                "inner join stock_cards s on s.facilityId = f.id  " +
                "inner join stock_card_entries e on e.stockcardid=s.id  " +
                "inner join products p on s.productId = p.id  " +
                "inner join program_products pp on pp.productId = P.ID  " +
                "inner join programs ON  pp.programid = programs.id   " +
                "inner join programs_supported ON  programs.id = programs_supported.programid   AND   f.id = programs_supported.facilityid  " +
                "Inner join isa_coefficients isa on isa.id = pp.isacoefficientsId  ");
        writePredicates(filter);
        GROUP_BY(" e.createdDate,p.primaryName,adjustmentvalue,f.name,geographic_zones.name,programs.name,f.catchmentpopulation  ");
        ORDER_BY("f.name asc,e.createdDate ");

        String sql = SQL();

        return sql;
    }


    private static void writePredicates(VaccineStockStatusParam filter){

        WHERE(programIsFilteredBy("programs.id"));

        if(filter.getStatusDate() != null) {
            WHERE(statusDateFilteredBy("e.createddate::DATE"));
        }

        String facilityLevel = filter.getFacilityLevel();
        if (facilityLevel.isEmpty()
                || facilityLevel.equalsIgnoreCase("cvs")
                || facilityLevel.equalsIgnoreCase("rvs")
                || facilityLevel.equalsIgnoreCase("dvs")) {
            WHERE("facility_types.code = #{filterCriteria.facilityLevel}");
        } else {
            WHERE("facility_types.code NOT IN ('cvs','rvs','dvs')");
        }

        // WHERE(" e.createddate::DATE <=  #{filterCriteria.statusDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");

        if (multiProductFilterBy(filter.getProducts(), "s.productId", "p.tracer") != null) {
            WHERE(multiProductFilterBy(filter.getProducts(), "s.productId", "p.tracer"));
        }



    }


}
