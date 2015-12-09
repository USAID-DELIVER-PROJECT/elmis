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
package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.repository.mapper.VaccineDashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class VaccineDashboardRepository {

    @Autowired
    VaccineDashboardMapper mapper;

    public HashMap<String, Object> getReportingSummary(){
        return mapper.getReportingSummary();
    }

    public HashMap<String, Object> getReportingDetails(){
        return mapper.getReportingDetails();
    }

    public HashMap<String, Object> getRepairingSummary(){
        return mapper.getRepairingSummary();
    }

    public HashMap<String, Object> getRepairingDetails(){
        return mapper.getRepairingDetails();
    }

    public HashMap<String, Object> getInvestigatingSummary(){
        return mapper.getInvestigatingSummary();
    }

    public HashMap<String, Object> getInvestigatingDetails(){
        return mapper.getInvestigatingDetails();
    }

    public List<HashMap<String, Object>> getMonthlyCoverage(){
        return mapper.getMonthlyCoverage();
    }

    public List<HashMap<String, Object>> getMonthlyWastage(){
        return mapper.getMonthlyWastage();
    }

    public List<HashMap<String, Object>> getWastageByDistrict(){
        return mapper.getWastageByDistrict();
    }

    public List<HashMap<String, Object>> getMonthlySessions(Date startDate, Date endDate){

        return mapper.getMonthlySessions(startDate, endDate);
    }
}
