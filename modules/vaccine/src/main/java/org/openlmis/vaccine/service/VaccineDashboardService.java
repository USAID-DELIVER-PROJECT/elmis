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
package org.openlmis.vaccine.service;

import lombok.NoArgsConstructor;
import org.joda.time.format.DateTimeFormat;
import org.openlmis.vaccine.repository.VaccineDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@NoArgsConstructor
public class VaccineDashboardService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    VaccineDashboardRepository repository;

    public HashMap<String, Object> getReportingSummary(){
        return repository.getReportingSummary();
    }

    public HashMap<String, Object> getReportingDetails(){
        return repository.getReportingDetails();
    }

    public HashMap<String, Object> getRepairingSummary(){
        return repository.getRepairingSummary();
    }

    public HashMap<String, Object> getRepairingDetails(){
        return repository.getRepairingDetails();
    }

    public HashMap<String, Object> getInvestigatingSummary(){
        return repository.getInvestigatingSummary();
    }

    public HashMap<String, Object> getInvestigatingDetails(){
        return repository.getInvestigatingDetails();
    }

    public List<HashMap<String, Object>> getMonthlyCoverage(){
        return repository.getMonthlyCoverage();
    }

    public List<HashMap<String, Object>> getMonthlyWastage(){
        return repository.getMonthlyWastage();
    }

    public List<HashMap<String, Object>> getMonthlySessions(String startDate, String endDate){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getMonthlySessions(fromDate, toDate);
    }

    public List<HashMap<String, Object>> getDistrictSessions(Long period){
        return repository.getDistrictSessions(period);
    }

    public List<HashMap<String, Object>> getBundling(String startDate, String endDate, Long productId){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getBundling(fromDate, toDate, productId);
    }



    public List<HashMap<String, Object>> getWastageByDistrict(){
        return repository.getWastageByDistrict();
    }


}
