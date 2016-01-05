package org.openlmis.vaccine.repository.mapper.reports.builder;


import java.util.Date;
import java.util.Map;

public class AdequacyLevelReportQueryBuilder {

    public static String selectAdequacyLevelOfSupplyReportDataByDistrict(Map params) {
        Long zone = (Long) params.get("districtId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        Long productId = (Long) params.get("productId");

        String sql = "WITH temp as (SELECT\n" +
                "  d.zone_name,\n" +
                "  d.region_name,\n" +
                "  d.district_name,\n" +
                "  period_start_date,\n" +
                "  product_id, \n"+
                "  EXTRACT(year from period_start_date) report_year,\n" +
                "  EXTRACT(month from period_start_date) report_month,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_received / s.quantity_issued * 100 else 0 end supplied_over_needs,\n" +
                "  case when s.quantity_issued > 0 then s.closing_balance / s.quantity_issued * 100 else 0 end mos,\n" +
                "  case when (COALESCE(s.quantity_issued,0) +  COALESCE(s.opening_balanace,0)) > 0 \n" +
                "  then COALESCE(s.quantity_issued,0) / (COALESCE(s.quantity_issued,0) +  COALESCE(s.opening_balanace,0)) * 100 else 0 end \n" +
                "  consumption_rate,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_discarded_opened / s.quantity_issued * 100 else 0 end wasted_opened,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_discarded_unopened / s.quantity_issued * 100 else 0 end wasted_unopened,\n" +
                "  case when s.quantity_issued > 0 then (COALESCE(s.quantity_discarded_unopened,0) + \n" +
                "    COALESCE(s.quantity_discarded_opened,0)) / s.quantity_issued * 100 else 0 end wasted_global\n" +
                " FROM   vw_vaccine_stock_status s\n" +
                " JOIN   vw_districts d on d.district_id = s.geographic_zone_id\n" +
                " WHERE  product_id = " +productId+
                "        AND period_start_date::date >= '" +startDate+"' AND period_start_date::date <= '"+endDate+"' "+
                writeDistrictPredicate(zone) +
                ")" +
                " SELECT zone_name,  region_name, district_name, period_start_date,report_year, report_month, " +
                "        SUM(supplied_over_needs) supplied_over_needs, SUM(mos) mos, SUM(consumption_rate) consumption_rate,\n" +
                "        SUM(wasted_opened) wasted_opened, SUM(wasted_unopened) wasted_unopened, SUM(wasted_global) wasted_global\n" +
                "   FROM temp\n" +
                "   group by zone_name,region_name,district_name,period_start_date,report_year,report_month\n" +
                "   order by 1,2,3,4";
        
        return sql;
    }

    public static String selectAdequacyLevelOfSupplyReportDataByRegion(Map params) {
        Long zone = (Long) params.get("districtId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        Long productId = (Long) params.get("productId");

        String sql = "WITH temp as (SELECT\n" +
                "  d.zone_name,\n" +
                "  d.region_name,\n" +
                "  d.district_name,\n" +
                "  period_start_date,\n" +
                "  product_id, \n"+
                "  EXTRACT(year from period_start_date) report_year,\n" +
                "  EXTRACT(month from period_start_date) report_month,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_received / s.quantity_issued * 100 else 0 end supplied_over_needs,\n" +
                "  case when s.quantity_issued > 0 then s.closing_balance / s.quantity_issued * 100 else 0 end mos,\n" +
                "  case when (COALESCE(s.quantity_issued,0) +  COALESCE(s.opening_balanace,0)) > 0 \n" +
                "  then COALESCE(s.quantity_issued,0) / (COALESCE(s.quantity_issued,0) +  COALESCE(s.opening_balanace,0)) * 100 else 0 end \n" +
                "  consumption_rate,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_discarded_opened / s.quantity_issued * 100 else 0 end wasted_opened,\n" +
                "  case when s.quantity_issued > 0 then s.quantity_discarded_unopened / s.quantity_issued * 100 else 0 end wasted_unopened,\n" +
                "  case when s.quantity_issued > 0 then (COALESCE(s.quantity_discarded_unopened,0) + \n" +
                "    COALESCE(s.quantity_discarded_opened,0)) / s.quantity_issued * 100 else 0 end wasted_global\n" +
                " FROM   vw_vaccine_stock_status s\n" +
                " JOIN   vw_districts d on d.district_id = s.geographic_zone_id\n" +
                " WHERE  product_id = " +productId+
                "        AND period_start_date::date >= '" +startDate+"' AND period_start_date::date <= '"+endDate+"' "+
                writeDistrictPredicate(zone) +
                ")" +
                "select \n" +
                "  region_name,\n" +
                "  report_year, report_month, SUM(supplied_over_needs) supplied_over_needs, SUM(mos) mos, SUM(consumption_rate) consumption_rate,\n" +
                "  SUM(wasted_opened) wasted_opened, SUM(wasted_unopened) wasted_unopened, SUM(wasted_global) wasted_global\n" +
                "    from temp\n" +
                "   group by region_name,report_year,report_month\n" +
                "   order by 1,2,3";

        return sql;
    }

    private static String writeDistrictPredicate(Long zone) {

        String predicate = " ";
        if (zone != 0 && zone != null) {
            predicate = " AND (district_id = "+zone+" or zone_id = "+zone+" or region_id = "+zone+" or parent = "+zone+")";
        }
        return predicate;
    }
}
