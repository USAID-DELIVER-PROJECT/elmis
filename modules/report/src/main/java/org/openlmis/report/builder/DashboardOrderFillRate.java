package org.openlmis.report.builder;

public class DashboardOrderFillRate {
    public static String getQuery(){
        String query="with period as (\n" +
                "select p.id pid,name periodname from processing_periods p\n" +
                "\twhere id =ANY('{114,113}'::int[])\n" +
                "),\n" +
                "request as (\n" +
                "\tSELECT    r.id rnrId,\n" +
                "\t r.periodid,\n" +
                "\t          f.name as facility, \n" +
                "\td.district_id as districtid,\n" +
                "\td.district_name district,\t\n" +
                "              li.productCode, \n" +
                "              li.product, \n" +
                "              li.quantityrequested  AS order, \n" +
                "              li.quantityapproved  AS approved\n" +
                "    FROM requisitions r \n" +
                "\tJOIN requisition_line_items li ON r.id = li.rnrid               \n" +
                "\tJOIN products p on li.productcode = p.code               \n" +
                "\tJOIN facilities f on f.id = r.facilityid \n" +
                "\tjoin vw_districts d on d.district_id=f.geographiczoneid\n" +
                "\tWHERE r.periodId =ANY('{114,113}'::int[]) and programid=3 \n" +
                "),\n" +
                "fullfilment as \n" +
                "( SELECT DISTINCT  orderid,productcode, quantityshipped, substitutedproductcode,  substitutedproductname, substitutedproductquantityshipped \n" +
                "         FROM \n" +
                "               ( \n" +
                "                      SELECT NULL as orderid, NULL AS quantityshipped, productcode, substitutedproductcode, substitutedproductname,  \n" +
                "                                substitutedproductquantityshipped \n" +
                "                       FROM shipment_line_items li \n" +
                "                       WHERE li.substitutedproductcode IS NOT NULL \n" +
                "-- \t\t\t\t\t   AND orderid = #{filterCriteria.rnrId} \n" +
                "                       UNION \n" +
                "                        SELECT orderid, sum(quantityshipped) quantityshipped, productcode, NULL substitutedproductcode, NULL AS substitutedproductname,  \n" +
                "                                NULL AS substitutedproductquantityshipped \n" +
                "                              FROM shipment_line_items \n" +
                "--                               WHERE orderid = #{filterCriteria.rnrId} \n" +
                "                              GROUP BY orderid, productcode \n" +
                "                            ) AS substitutes \n" +
                "                          ORDER BY productcode, substitutedproductcode DESC \n" +
                "                        ) \n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\tselect r.*,p.*,f.*,\n" +
                "\t\t\tCASE WHEN COALESCE(r.approved, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC \n" +
                "               ELSE round((COALESCE(f.quantityshipped, 0)::numeric / COALESCE(r.approved, 0)) * 100, 2)  END  AS item_fill_rate \n" +
                "\t\t\t\n" +
                "\t\t\tfrom request r\n" +
                "\t\t\tinner join period p on r.periodid=p.pid\n" +
                "\t\t\tleft outer join fullfilment f on\n" +
                "\t\t\tf.productcode = r.productcode and f.orderid=r.rnrId";
        return query;
    }
}
