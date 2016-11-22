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
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.report.mapper.UserSummaryReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.params.UserSummaryParams;
import org.openlmis.report.util.ParameterAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class UserSummaryReportProvider extends ReportDataProvider {
    private UserSummaryReportMapper reportMapper;

    private UserSummaryParams userSummaryParam = null;


    @Autowired
    public UserSummaryReportProvider(UserSummaryReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), sortCriteria, rowBounds);
    }

    public UserSummaryParams getReportFilterData(Map<String, String[]> filterCriteria) {
        return ParameterAdaptor.parse(filterCriteria, UserSummaryParams.class);
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        UserSummaryParams userSummaryParams = this.getReportFilterData(params);
        StringBuilder filterString = new StringBuilder();
        Program program = null;
        Role role = null;
        String programName="";
        String roleName="";
        String superVisoryName="";
        SupervisoryNode supervisoryNode = null;
        if (userSummaryParams.getProgramId()==0) {
            filterString.append("Program : All");
        }else {
            program= this.reportMapper.getProgram(userSummaryParams.getProgramId());
            programName=program==null?"": program.getName();
            filterString.append("Program : ").append(programName);
        }
        if (userSummaryParams.getRoleId()==0) {
            filterString.append(", Role : All");

        }else {
            role= this.reportMapper.getRole(userSummaryParams.getRoleId());
            roleName=role==null?"": role.getName();
            filterString.append(", Role : ").append(roleName);
        }
        if (userSummaryParams.getSupervisoryNodeId()==0) {
            filterString.append(", Supervisory Node : All");
        }else {
            supervisoryNode= this.reportMapper.getSuperVisoryNode(userSummaryParams.getSupervisoryNodeId());
            superVisoryName=supervisoryNode==null?"": supervisoryNode.getName();
            filterString.append(", Supervisory Node : ").append(superVisoryName);
        }
        return filterString.toString();

    }

    public List<HashMap> getUserAssignments() {
        return reportMapper.getUserRoleAssignments();
    }


}
