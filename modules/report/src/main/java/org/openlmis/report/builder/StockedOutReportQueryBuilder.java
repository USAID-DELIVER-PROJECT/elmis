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

import org.openlmis.report.model.params.StockedOutReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.geoZoneIsFilteredBy;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.multiProductFilterBy;


public class StockedOutReportQueryBuilder {

  public static String getQuery(Map params) {
    StockedOutReportParam filter = (StockedOutReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT_DISTINCT( " gz.region_name as supplyingfacility,  " +
        " f.code as facilitycode,  " +
        " li.productcode,  " +
        " f.name as facility,  " +
        " li.product as product,  " +
        " ft.name facilitytypename,  " +
        " gz.district_name as location,  " +
        " pp.name as processing_period_name,  " +
        " li.stockoutdays stockoutdays,   " +
        " to_char(pp.startdate, 'Mon') as month,  " +
        " extract(year from pp.startdate) as year,   " +
        " pg.code as program  " +
        "   ");

      FROM(
         " processing_periods pp  " +
        "JOIN requisitions r ON pp. ID = r.periodid  " +
        "JOIN requisition_line_items li ON li.rnrid = r. ID  " +
        "JOIN facilities f on f.id = r.facilityId  " +
        "JOIN facility_types ft on ft.id = f.typeid  " +
        "JOIN products p on p.code = li.productcode  " +
        "JOIN vw_districts gz on gz.district_id = f.geographiczoneid  " +
        "JOIN programs pg on pg.id = r.programid  " );


    WHERE("li.stockinhand = 0 AND li.skipped = false ");
    WHERE("(li.beginningbalance > 0 or li.quantityreceived > 0 or li.quantitydispensed > 0 or abs(li.totallossesandadjustments) > 0 or li.amc > 0)");
    WHERE(programIsFilteredBy("r.programId"));
    WHERE(periodIsFilteredBy("r.periodId"));
    WHERE(userHasPermissionOnFacilityBy("r.facilityId"));
    WHERE(rnrStatusFilteredBy("r.status", filter.getAcceptedRnrStatuses()));

    if (filter.getProductCategory() != 0) {
      WHERE(productCategoryIsFilteredBy("p.categoryId"));
    }

    if(filter.getFacility() != 0){
      WHERE(facilityIsFilteredBy("r.facilityId"));
    }

    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("f.typeid"));
    }

    if (multiProductFilterBy(filter.getProducts(), "p.id", "p.tracer") != null) {
      WHERE(multiProductFilterBy(filter.getProducts(), "p.id", "p.tracer"));
    }

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }
    ORDER_BY("supplyingFacility asc, facility asc, product asc");
    // copy the sql over to a variable, this makes the debugging much more possible.
    return SQL();
  }


}
