package org.openlmis.report.builder;

public class DashboardOrderFillRateBuilder {
    public static String getQuery(){
        String query="WITH \n" +
                "fullfilment AS\n" +
                "  (SELECT DISTINCT orderid,\n" +
                "                   productcode,\n" +
                "                   quantityshipped,\n" +
                "                   substitutedproductcode,\n" +
                "                   substitutedproductname,\n" +
                "                   substitutedproductquantityshipped\n" +
                "   FROM\n" +
                "     (SELECT NULL AS orderid,\n" +
                "             NULL AS quantityshipped,\n" +
                "             productcode,\n" +
                "             substitutedproductcode,\n" +
                "             substitutedproductname,\n" +
                "             substitutedproductquantityshipped\n" +
                "      FROM shipment_line_items li\n" +
                "      WHERE li.substitutedproductcode IS NOT NULL \n" +
                "\n" +
                "      UNION SELECT orderid,\n" +
                "                   sum(quantityshipped) quantityshipped,\n" +
                "                   productcode,\n" +
                "                   NULL substitutedproductcode,\n" +
                "                        NULL AS substitutedproductname,\n" +
                "                        NULL AS substitutedproductquantityshipped\n" +
                "      FROM shipment_line_items \n" +
                "\n" +
                "      GROUP BY orderid,\n" +
                "               productcode) AS substitutes\n" +
                "   ORDER BY productcode,\n" +
                "            substitutedproductcode DESC),\n" +
                "    request AS\n" +
                "  (SELECT r.id rnrId,    \n" +
                "        li.productcode,\n" +
                "\t\t  fa.id as facilityid,  \n" +
                "   fa.geographicZoneid as location,\n" +
                "          li.quantityrequested AS\n" +
                "   ORDER,\n" +
                "          li.quantityapproved AS approved,\n" +
                "\t\t  CASE\n" +
                "           WHEN COALESCE(li.quantityapproved, 0 :: NUMERIC) = 0 :: NUMERIC THEN 0 :: NUMERIC\n" +
                "           ELSE round((COALESCE(f.quantityshipped, 0)::numeric / COALESCE(li.quantityapproved, 0)) * 100, 2)\n" +
                "       END AS item_fill_rate\n" +
                "   FROM requisitions r\n" +
                "   JOIN requisition_line_items li ON r.id = li.rnrid   \n" +
                "   JOIN facilities fa ON fa.id = r.facilityid   \n" +
                "   LEFT OUTER JOIN fullfilment f ON f.productcode = li.productcode\n" +
                "AND f.orderid=li.rnrId\n" +
                "   WHERE r.periodId =110\n" +
                "     AND programid=3 ),\n" +
                "\t prev AS\n" +
                "  (SELECT r.id rnrId,\n" +
                "          r.periodid,     \n" +
                "   li.productcode,\n" +
                "\t\t  fa.id as facilityid,  \n" +
                "   fa.geographicZoneid as location,\n" +
                "          li.quantityrequested AS\n" +
                "   ORDER,\n" +
                "          li.quantityapproved AS approved,\n" +
                "\t\t  CASE\n" +
                "           WHEN COALESCE(li.quantityapproved, 0 :: NUMERIC) = 0 :: NUMERIC THEN 0 :: NUMERIC\n" +
                "           ELSE round((COALESCE(f.quantityshipped, 0)::numeric / COALESCE(li.quantityapproved, 0)) * 100, 2)\n" +
                "       END AS item_fill_rate\n" +
                "   FROM requisitions r\n" +
                "   JOIN requisition_line_items li ON r.id = li.rnrid  \n" +
                "   JOIN facilities fa ON fa.id = r.facilityid   \n" +
                "   LEFT OUTER JOIN fullfilment f ON f.productcode = li.productcode\n" +
                "AND f.orderid=li.rnrId\n" +
                "   WHERE r.periodId =113\n" +
                "     AND programid=3 ),\n" +
                "\t aggregate as(\n" +
                "     \n" +
                "SELECT d.region_name as name,\n" +
                "      sum(p.item_fill_rate) as prev,\n" +
                "\t  sum(c.item_fill_rate) as current,\n" +
                "\t\t count(*) as count\n" +
                "From request c \n" +
                "inner join prev p on c.facilityid=p.facilityid and p.productcode=c.productcode\n" +
                "\t\t join vw_districts d on d.district_id= p.location\n" +
                "\t\t group by d.region_name\n" +
                "\t\t \n" +
                "\t\t )\n" +
                "\t\n" +
                "\t\t select a.name as name,\n" +
                "\t\t round(a.prev/count,2) as prev,\n" +
                "\t\t round(a.current/count,2) as current ,\n" +
                "\t\t case \n" +
                "\t\t when round(a.current/count,2)>=80 then 'good'\n" +
                "\t\t when round(a.current/count,2)>=60 then 'normal'\n" +
                "\t\t when round(a.current/count,2)<60 then 'bad'\n" +
                "\t\t end status\t \n" +
                "\t\t \n" +
                "\t\t from aggregate a " +
                "order by a.name";
        return query;
    }
}
