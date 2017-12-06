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


import org.openlmis.report.model.params.ColdChainEquipmentReportParam;

import java.util.Map;


public class ColdChainEquipmentReportQueryBuilder
{
    public static String getQuery(Map params)
    {

        ColdChainEquipmentReportParam filter = (ColdChainEquipmentReportParam)params.get("filterCriteria");
        Long userId = (Long) params.get("userId");

        return " SELECT e.*, vw.district_name districtName, vw.region_name regionName FROM vw_cold_chain_equipment e  " +
                "JOIN vw_districts vw ON e.geozoneId = vw.district_id   "+
                  writePredicates(filter)
       + "      AND vw.district_id in (select district_id from vw_user_facilities where user_id = '" + userId + "'::INT and program_id = fn_get_vaccine_program_id())  "

                +"  ORDER BY vw.region_name, vw.district_name, e.facilityname  ";

    }

    private static String writePredicates(ColdChainEquipmentReportParam params) {

        String predicate = " ";

        String facilityLevel = params.getFacilityLevel();
        Long zone = params.getZone();

        if (  facilityLevel.equalsIgnoreCase("cvs")
                || facilityLevel.equalsIgnoreCase("rvs")
                || facilityLevel.equalsIgnoreCase("dvs")) {
                predicate += "  where facilitytypecode = #{filterCriteria.facilityLevel}::text ";

        } else {
            predicate += "  where facilitytypecode NOT IN ('cvs','rvs','dvs') ";

        }
        if (zone != 0 && zone != null) {
            predicate += " AND (district_id = " + zone + " or zone_id = " + zone + " or region_id = " + zone + " or parent = " + zone + ")";
        }

        return predicate;

    }


}
