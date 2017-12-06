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

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.mapper.ColdChainEquipmentReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.ColdChainEquipmentReportParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Component
@NoArgsConstructor
public class ColdChainEquipmentReportDataProvider extends ReportDataProvider{


    @Autowired
    private ColdChainEquipmentReportMapper reportMapper;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramService programService;


    @Override
    @Transactional
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }


    public ColdChainEquipmentReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        ColdChainEquipmentReportParam param = new ColdChainEquipmentReportParam();

       param.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);
       param.setZone(StringHelper.isBlank(filterCriteria, "zone") ? 0: Long.parseLong(filterCriteria.get("zone")[0]));

        List<Program>programs = programService.getAllIvdPrograms();

        // List of facilities includes supervised and home facility
        List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), programs.get(0).getId(), MANAGE_EQUIPMENT_INVENTORY);
        facilities.add(facilityService.getHomeFacility(this.getUserId()));

        StringBuilder str = new StringBuilder();
        str.append("{");
        for (Facility f : facilities) {
            str.append(f.getId());
            str.append(",");
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length()-1);
        }
        str.append("}");
        param.setFacilityIds(str.toString());

        return param;
    }

    @Override
    @Transactional
    public String getFilterSummary(Map<String, String[]> params) {
        //TODO: Implement This
        return "No Filter";
    }

}
