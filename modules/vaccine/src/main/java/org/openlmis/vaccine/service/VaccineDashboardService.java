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

    public HashMap<String, Object> getReportingSummary(Long userId){
        return repository.getReportingSummary(userId);
    }

    public HashMap<String, Object> getReportingDetails(Long userId){
        return repository.getReportingDetails(userId);
    }

    public HashMap<String, Object> getRepairingSummary(Long userId){
        return repository.getRepairingSummary(userId);
    }

    public HashMap<String, Object> getRepairingDetails(Long userId){
        return repository.getRepairingDetails(userId);
    }

    public HashMap<String, Object> getInvestigatingSummary(Long userId){
        return repository.getInvestigatingSummary(userId);
    }

    public HashMap<String, Object> getInvestigatingDetails(Long userId){
        return repository.getInvestigatingDetails(userId);
    }

    public List<HashMap<String, Object>> getMonthlyCoverage(String startDate, String endDate, Long userId, Long product){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getMonthlyCoverage(fromDate, toDate,userId, product);
    }
    public List<HashMap<String, Object>> getFacilityCoverage(Long period, Long product, Long userId){
        return repository.getFacilityCoverage(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityCoverageDetails(String startDate, String endDate, Long product, Long userId){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getFacilityCoverageDetails(fromDate,toDate, product, userId);
    }

    public List<HashMap<String, Object>> getFacilitySessions(Long period, Long userId){
        return repository.getFacilitySessions(period, userId);
    }

    public List<HashMap<String, Object>> getFacilitySessionsDetails(String startDate, String endDate, Long userId){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getFacilitySessionsDetails(fromDate, toDate, userId);
    }
    public List<HashMap<String, Object>> getFacilityWastage(Long period, Long product, Long userId){
        return repository.getFacilityWastage(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityWastageDetails(String startDate, String endDate, Long product, Long userId){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getFacilityWastageDetails(fromDate, toDate, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityDropout(Long period, Long product, Long userId){
        return repository.getFacilityDropout(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityDropoutDetails(String startDate, String endDate, Long product, Long userId){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getFacilityDropoutDetails(fromDate,toDate, product, userId);
    }

    public List<HashMap<String, Object>> getDistrictCoverage(Long period, Long product){
        return repository.getDistrictCoverage(period, product);
    }

    public List<HashMap<String, Object>> getMonthlyWastage(String startDate, String endDate, Long product){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getMonthlyWastage(fromDate, toDate, product);
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



    public List<HashMap<String, Object>> getWastageByDistrict(Long period, Long product){
        return repository.getWastageByDistrict(period, product);
    }
    public List<HashMap<String, Object>> getMonthlyDropout(String startDate, String endDate, Long product){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getMonthlyDropout(fromDate, toDate, product);
    }
    public List<HashMap<String, Object>> getDistrictDropout(Long period, Long product){
        return repository.getDistrictDropout(period, product);
    }

    public List<HashMap<String, Object>> getMonthlyStock(String startDate, String endDate, Long product){
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();

        return repository.getMonthlyStock(fromDate, toDate, product);
    }
    public List<HashMap<String, Object>> getDistrictStock(Long period, Long product){
        return repository.getDistrictStock(period, product);
    }

}
