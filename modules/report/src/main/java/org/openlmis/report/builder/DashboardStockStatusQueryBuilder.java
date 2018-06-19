package org.openlmis.report.builder;

public class DashboardStockStatusQueryBuilder {

    public static String getQuery(){
        String query="with stockout as ( \n" +
                "                select  \n" +
                "                  facility_id facilityid, \n" +
                "                  count(*) from vw_stock_status_2  \n" +
                "                where periodId = 113 and programId = 1 \n" +
                "                AND (gz_id = 0 OR 0 = 0)  \n" +
                "                and  req_status <> 'INITIATED' and reported_figures > 0  \n" +
                "                and status = 'SO' group by facility_id  )  , \n" +
                "                prevstockout as ( \n" +
                "                select  \n" +
                "                  facility_id facilityid, \n" +
                "                  count(*) from vw_stock_status_2  \n" +
                "                where periodId = 114 and programId = 1 \n" +
                "                AND (gz_id = 0 OR 0 = 0)  \n" +
                "                and  req_status <> 'INITIATED' and reported_figures > 0  \n" +
                "                and status = 'SO' group by facility_id  ) ,\n" +
                "\t\t\t\t\n" +
                "\t\t\t\tcompiled as(\n" +
                "                  SELECT \n" +
                "                  d.region_id as region, \n" +
                "                  d.region_name as name, \n" +
                "                   sum(ps.count) AS prev , \n" +
                "                  sum(s.count) AS current , \n" +
                "\t\t\t\t  count(d.region_id) as total\n" +
                "                               FROM  \n" +
                "                   public.facilities f \n" +
                "                   inner join public.vw_districts AS d  on f.geographiczoneid =d. district_id                 \n" +
                "                               LEFT JOIN    stockout s ON f.id = s.facilityid                   \n" +
                "                                 LEFT JOIN    prevstockout ps ON f.id = ps.facilityid   \n" +
                "\t\t\t\t\t\t\t\t group by d.region_id, d.region_name\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t\tselect\n" +
                "\t\t\t\t\tc.name, \n" +
                "\t\t\t\t\tround((100-c.prev/c.total),2) as prev,\n" +
                "\t\t\t\t\tround((100-c.current/c.total),2) as current, \n" +
                "\t\t\t\t\t\n" +
                "                case  \n" +
                "                when \n" +
                "                sum(100-c.current/total)>=80 then 'good' \n" +
                "               when sum(100-c.current/total)>=60 then 'normal'  \n" +
                "               when sum(100-c.current/total)<60 then 'bad'   \n" +
                "                end status  \n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\tfrom compiled as c \n" +
                "\t\t\t\t\tgroup by c.name, c.prev, c.current, c.total\n" +
                "\t\t\t\t\torder by c.name\n" +
                "\t\t\t\t\t ";
        return query;
    }
}
