
/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.params.AggregateConsumptionReportParam;
import org.openlmis.report.model.params.FacilityConsumptionReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.reportTypeFilteredBy;

public class DailyConsumptionQueryBuilder {

    public static String getQuery(Map params){

        FacilityConsumptionReportParam filter = (FacilityConsumptionReportParam) params.get("filterCriteria");

        BEGIN();
        SELECT("ds.id");
        SELECT("ds.programid programId");
        SELECT("dsl.stockonhand stockOnHand");
        SELECT("f.name facilityName");
        SELECT("f.id facilityId");
        SELECT("f.code facilityCode");
        SELECT("date");
        SELECT("source");
        SELECT("p.code productCode");
        SELECT("p.primaryName productName");
        SELECT("current_date -(select min(date)from daily_stock_status where facilityid=ds.facilityid) as daysAfterFirstSubmission");
        SELECT("current_date -(select max(date)from daily_stock_status where facilityid=ds.facilityid) as daysAfterLastSubmission");
        FROM("daily_stock_status ds");
        INNER_JOIN("facilities f on f.id=ds.facilityid");
        INNER_JOIN("daily_stock_status_line_items dsl on ds.id=dsl.stockstatussubmissionid ");
        INNER_JOIN("products p on p.id=dsl.productid ");

        writePredicates(filter);
        ORDER_BY("f.name, p.primaryName");
        return SQL();

    }

    private static void writePredicates(FacilityConsumptionReportParam filter) {

        WHERE(programIsFilteredBy("ds.programid"));
        WHERE(userHasPermissionOnFacilityBy("ds.facilityId"));
        WHERE(dateFilteredBy("ds.date", filter.getDate().trim()));

//        if (filter.getZone() != 0) {
//            WHERE(geoZoneIsFilteredBy("d"));
//        }


    }

}
