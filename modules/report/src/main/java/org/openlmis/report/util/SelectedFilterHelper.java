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

package org.openlmis.report.util;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.report.model.dto.AdjustmentType;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@NoArgsConstructor
public class SelectedFilterHelper {

    public static final String PROGRAM = "program";
    public static final String PERIOD = "period";
    public static final String ZONE = "zone";
    public static final String USER_ID = "userId";
    public static final String FACILITY = "facility";
    public static final String PRODUCT = "product";
    public static final String ADJUSTMENET_TYPE = "adjustmentType";
    public static final String PROGRAM_AREA = "";
    public static final String PERIODSTART = "periodStart";
    public static final String PERIODEND = "periodEnd";
    public static final String DISTRICT = "district";

    public static final String STARTDATE = "startDate";
    public static final String ENDDATE = "periodEnd";


    @Autowired
    private ProcessingPeriodRepository periodService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProductRepository productService;

    @Autowired
    private GeographicZoneRepository geoZoneRepsotory;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private SupervisoryNodeService supervisoryNodeService;

    @Autowired
    private ReportLookupService reportLookupService;

    public String getProgramPeriodGeoZone(Map<String, String[]> params) {
        String filterSummary = "";

        String program = StringHelper.getValue(params, PROGRAM);
        String period = StringHelper.getValue(params, PERIOD);
        String zone = StringHelper.getValue(params, ZONE);
        String userId = StringHelper.getValue(params, USER_ID);
        String adjustmentType = StringHelper.getValue(params, ADJUSTMENET_TYPE);

        ProcessingPeriod periodObject = (period != null) ? periodService.getById(Long.parseLong(period)) : null;
        GeographicZone zoneObject = (zone != null) ? geoZoneRepsotory.getById(Long.parseLong(zone)) : null;
        if (program != null) {
            if ("0".equals(program)) {
                filterSummary = "Program: All Programs";
            } else {
                filterSummary = String.format("Program: %s", programService.getById(Long.parseLong(program)).getName());
            }
        }
        if (periodObject != null) {
            filterSummary += String.format("%nPeriod: %s, %s", periodObject.getName(), periodObject.getStringYear());
        }
        if (zoneObject == null && userId != null && program != null) {
            // Lets determine the user's supervisory node is either National or not
            Long totalSNods = supervisoryNodeService.getTotalUnassignedSupervisoryNodeOfUserBy(Long.parseLong(userId), Long.parseLong(program));

            if (totalSNods == 0)
                filterSummary += "\nGeographic Zone: National";
            else
                filterSummary += "\nGeographic Zone: All Zones";

        } else if (zoneObject != null) {
            filterSummary += "\nGeographic Zone: " + zoneObject.getName();
        }
        if (adjustmentType != null) {
            AdjustmentType type = reportLookupService.getAdjustmentByName(adjustmentType);
            filterSummary += "\nAdjustment Type: " + type.getDescription();
        }
        return filterSummary;
    }

    public String getProgramGeoZoneFacility(Map<String, String[]> params) {

        String filterSummary = "";

        String program = StringHelper.getValue(params, PROGRAM);
        String zone = StringHelper.getValue(params, ZONE);
        String facility = StringHelper.getValue(params, FACILITY);

        if (program != null)
            filterSummary = String.format("Program: %s", "0".equals(program) ? "" : programService.getById(Long.parseLong(program)).getName());

        if (zone != null) {
            GeographicZone zoneObject = geoZoneRepsotory.getById(Long.parseLong(zone));
            filterSummary = filterSummary + (zoneObject == null ? "\nGeographic Zone: National" : String.format("%nGeographic Zone: %s", zoneObject.getName()));
        }

        if (facility != null) {
            Facility facilityObject = facilityRepository.getById(Long.parseLong(facility));
            filterSummary = filterSummary + (facilityObject == null ? "\nFacility: All Facilities" : String.format("%nFacility: %s", facilityObject.getName()));
        }

        return filterSummary;
    }

    public String getSelectedFilterString(Map<String, String[]> params) {
        String product = StringHelper.getValue(params, PRODUCT);
        String program = StringHelper.getValue(params, PROGRAM);
        String period = StringHelper.getValue(params, PERIOD);

        return new StringBuilder()
                .append("Program: ").append(programService.getById(Long.parseLong(program)).getName())
                .append("\nPeriod: ").append(periodService.getById(Long.parseLong(period)).getName())
                .append("\n")
                .append(getSelectedProductSummary(product))
                .toString();
    }

    public String getSelectedProductSummary(String product) {
        if (product == null || product.isEmpty() || "0".equals(product)) {
            return "\nProduct: All Products";
        } else if ("-1".equals(product)) {
            return "\nProduct: Indicator / Tracer Commodities";
        } else {
            Product productObject = productService.getById(Long.parseLong(product));
            if (productObject != null) {
                return "\nProduct: " + productObject.getPrimaryName();
            }
        }
        return "";
    }

    public String getSelectedDateRange(Map<String, String[]> params) {
        String startDate = StringHelper.getValue(params, STARTDATE);
        String endDate = StringHelper.getValue(params, ENDDATE);

        if (!(startDate == null || startDate.isEmpty() || "0".equals(startDate))
                && !(endDate == null || endDate.isEmpty() || "0".equals(endDate))) {
            return "\nDate Range: " + startDate + " - " + endDate;
        }
        return "";
    }

    public String getFacilityFilterString(Map<String, String[]> params) {
        String facility = StringHelper.getValue(params, FACILITY);

        if (facility != null) {
            Facility facilityObject = facilityRepository.getById(Long.parseLong(facility));
            return (facilityObject == null ? "\nFacility: All Facilities" : String.format("%nFacility: %s", facilityObject.getName()));
        }
        return "";
    }

    public String getSelectedPeriodRange(Map<String, String[]> params) {
        String startDate = StringHelper.getValue(params, PERIODSTART);
        String endDate = StringHelper.getValue(params, PERIODEND);

        if (!(startDate == null || startDate.isEmpty() || "0".equals(startDate))
                && !(endDate == null || endDate.isEmpty() || "0".equals(endDate))) {
            return "\nPeriod: " + startDate + " - " + endDate;
        }
        return "";
    }

    public String getGeoZoneFilterString(Map<String, String[]> params) {
        String district = StringHelper.getValue(params, DISTRICT);

        if (district != null) {
            GeographicZone zoneObject = geoZoneRepsotory.getById(Long.parseLong(district));
            return (zoneObject == null ? "\nGeographic Zone: All Region/Districts" : String.format("%nGeographic Zone: %s", zoneObject.getName()));
        }
        return "";
    }

    public String getGeoZoneFilterString(Long geographicZoneId) {
        GeographicZone zoneObject = geoZoneRepsotory.getById(geographicZoneId);
        return (zoneObject == null ? "\nGeographic Zone: All Region/Districts" : String.format("%nGeographic Zone: %s", zoneObject.getName()));

    }

    public String getReportCombinedFilterString(String... filterStrings) {

        StringBuilder sb = new StringBuilder();

        for (String filter : filterStrings) {
            sb.append(filter);
        }

        return sb.toString();
    }


}
