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

import org.openlmis.report.model.params.AdjustmentSummaryReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;



public class AdjustmentSummaryQueryBuilder {

  public static String getQuery(Map params) {

    AdjustmentSummaryReportParam filter = (AdjustmentSummaryReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT("facility_code as facilityCode, facility_name facilityName, facility_type_name facilityType, d.zone_name as province");
    SELECT("d.district_name as district, program_name as program, productcode, product productDescription, supplying_facility_name supplyingFacility");
    SELECT("t.description  AS  adjustmentType, extract(year from processing_periods_start_date) as year, initcap(to_char(processing_periods_start_date, 'mon')) as month");
    SELECT("adjutment_qty adjustment, processing_periods_name as period, product_category_name category, adjustment_type");
    SELECT("adjutment_qty * case when adjustment_additive  = 't' then 1 else -1 end AS signedadjustment");
    FROM("vw_requisition_adjustment ");
      JOIN(" facilities f on f.id = vw_requisition_adjustment.facility_id ");
      JOIN(" vw_districts d on f.geographicZoneId = d.district_id ");
      JOIN(" losses_adjustments_types t on t.name = vw_requisition_adjustment.adjustment_type AND t.isdefault = TRUE ");
      JOIN(" products p on p.id = vw_requisition_adjustment.product_id");
    WHERE(rnrStatusFilteredBy("req_status", filter.getAcceptedRnrStatuses()));
    WHERE(programIsFilteredBy("program_id"));
    WHERE(userHasPermissionOnFacilityBy("f.id"));
    WHERE(periodIsFilteredBy("processing_periods_id"));

    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facility_type_id"));
    }

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("vw_requisition_adjustment.facility_id"));
    }

    if (filter.getProductCategory() != 0L ) {
      WHERE(productCategoryIsFilteredBy("product_category_id"));
    }

    if (multiProductFilterBy(filter.getProducts(), "p.id", "p.tracer") != null) {
      WHERE(multiProductFilterBy(filter.getProducts(), "p.id", "p.tracer"));
    }

    if (filter.getAdjustmentType() != null && !"0".equals(filter.getAdjustmentType()) && !filter.getAdjustmentType().isEmpty()) {
      WHERE("adjustment_type = #{filterCriteria.adjustmentType}");
    }
    if(filter.getFacilityOperator() != 0L){
      WHERE("f.operatedById = " + filter.getFacilityOperator().toString());
    }
    ORDER_BY(QueryHelpers.getSortOrder(params, " product, adjustment_type, facility_type_name,facility_name, supplying_facility_name, product_category_name "));
    return SQL();
  }

}
