package org.openlmis.report.builder;

import org.openlmis.report.model.params.StockEventParam;

import java.util.Map;

/**
 * Created by hassan on 6/22/17.
 */
public class StockEventQueryBuilder {

    public static String getQuery(Map params){

        StockEventParam filter = (StockEventParam)params.get("filterCriteria");
        System.out.println(filter.getFacilityId());
        return
                "                select Max(MaximumStock) maximumStock,MAX(isaValue) minimumStock,\n" +
                "                to_char(e.createddate, 'yyyy-mm-dd')::date days,to_char(e.createddate, 'dd') dayOccured,\n" +
                "                 to_char(e.createddate, 'mm') monthOccured,\n" +
                "                 count(e.*) Events ,Case WHEN sum(e.quantity) >0 THEN sum(e.quantity) else sum(e.quantity) * -1 end as soh , (select name from facilities where id = '"+filter.getFacilityId()+"' ) storeName\n" +
                "                from stock_card_entries e\n" +
                "                join stock_cards sc on e.stockcardid = sc.id\n" +
                "                LEFT JOIN stock_requirements sr on sc.facilityId =SR.FACILITYID AND SC.PRODUCTiD = sr.productId AND sr.year ='"+filter.getYear()+"'\n" +
                "                where  extract('year' from e.createddate) = '"+filter.getYear()+"' and extract('month' from e.createddate) ='"+filter.getMonthInNumber()+"'\n" +
                "                and sc.productId = '"+filter.getProduct()+"' and sc.facilityId ="+filter.getFacilityId()+"\n" +
                "                group by to_char(e.createddate, 'yyyy-mm-dd'),to_char(e.createddate, 'dd'),to_char(e.createddate, 'mm'),sc.FACILITYiD\n" +
                "                order by to_char(e.createddate, 'yyyy-mm-dd') asc ";


    }


}
