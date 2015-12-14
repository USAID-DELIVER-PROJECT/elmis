/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.builder;


import org.openlmis.report.model.params.StockLedgerReportParam;

import java.util.Map;


public class StockLedgerReportQueryBuilder {


    public String getQuery(Map params) {

        StockLedgerReportParam filter = (StockLedgerReportParam) params.get("filterCriteria");

        return ("Select primaryname product,id,date , facility storeName, received, issued, adjustment,total,lotnumber,expirationdate,vvm vvmStatus, (SUM(total) over(partition by lotnumber order by id))  as loh,(SUM(total) over(order by id))  as soh\n" +
                "FROM  " +
                "(WITH Q AS (  " +
                "select MAX(p.primaryname) primaryname  , 0 as id, MAX(#{filterCriteria.startDate})::timestamp with time zone as date,  " +
                "null::TEXT as facility, " +
                "0::INTEGER as received,  " +
                "0::INTEGER as issued,   " +
                "0::INTEGER as adjustment, " +
                "l.lotnumber,  " +
                "MAX(l.expirationdate::DATE) as expirationdate, " +
                "MAX(skvvvm.valuecolumn) as vvm,  " +
                "SUM(se.quantity)::integer as total  " +
                "from stock_card_entries se  " +
                "join stock_cards s ON s.id=se.stockcardid  " +
                "join lots_on_hand lo ON lo.id=se.lotonhandid  " +
                "join lots l on l.id=lo.lotid  " +
                "join products p on p.id=s.productid  " +
                "join facilities f on f.id=s.facilityid  " +
                "left join stock_card_entry_key_values skvvvm on skvvvm.stockcardentryid=se.id and skvvvm.keycolumn='vvmstatus'  " +
                "where  " +
                " p.id = #{filterCriteria.product}::Integer and f.id = " + filter.getFacility() + " and  se.createddate::DATE < #{filterCriteria.startDate}::date    group by l.lotnumber)  " +
                "SELECT * FROM Q  " +
                "UNION ALL  " +
                "(select p.primaryname , se.id, se.createddate as date,  " +
                "case when se.type='CREDIT' then skvr.valuecolumn when se.type='ADJUSTMENT' then (select name from facilities where id =  " + filter.getFacility() + ") when se.type='DEBIT' then skvi.valuecolumn end as facility,  " +
                "case when se.type ='CREDIT' then se.quantity else 0 end as received,  " +
                "case when se.type ='DEBIT' then se.quantity else 0 end as issued,  " +
                "case when se.type ='ADJUSTMENT' then quantity else 0 end as adjustment,  " +
                "l.lotnumber,  " +
                "l.expirationdate::DATE,  " +
                "skvvvm.valuecolumn as vvm,  " +
                "se.quantity::integer as total  " +
                "from stock_card_entries se  " +
                "join stock_cards s ON s.id=se.stockcardid  " +
                "join lots_on_hand lo ON lo.id=se.lotonhandid  " +
                "join lots l on l.id=lo.lotid  " +
                "join products p on p.id=s.productid  " +
                "join facilities f on f.id=s.facilityid  " +
                "left join stock_card_entry_key_values skvr on skvr.stockcardentryid=se.id and skvr.keycolumn='receivedfrom'  " +
                "left join stock_card_entry_key_values skvi on skvi.stockcardentryid=se.id and skvi.keycolumn='issuedto'   " +
                "left join stock_card_entry_key_values skvvvm on skvvvm.stockcardentryid=se.id and skvvvm.keycolumn='vvmstatus'  " +
                 getPredicate(filter) +
                " order by se.createddate)) AS ledger order by id  ");
    }

    public static String getPredicate(StockLedgerReportParam params) {

        String predicate = " ";
        predicate += " where p.Id = " + params.getProduct();
        predicate += " and f.id = " + params.getFacility();
        predicate += " and se.createddate::DATE <= #{filterCriteria.endDate}::date";
        predicate += " and se.createddate::DATE >= #{filterCriteria.startDate}::date";

        return predicate;
    }

}
