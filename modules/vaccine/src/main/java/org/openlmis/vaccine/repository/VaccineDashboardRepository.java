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
import java.util.Map;

@Component
public class VaccineDashboardRepository {

    @Autowired
    VaccineDashboardMapper mapper;

    public Map<String, Object> getReportingSummary(Long user) {
        return mapper.getReportingSummary(user);
    }

    public List<HashMap<String, Object>> getReportingDetails(Long userId) {
        return mapper.getReportingDetails(userId);
    }

    public Map<String, Object> getRepairingSummary(Long userId) {
        return mapper.getRepairingSummary(userId);
    }

    public List<HashMap<String, Object>> getRepairingDetails(Long userId) {
        return mapper.getRepairingDetails(userId);
    }

    public Map<String, Object> getInvestigatingSummary(Long userId) {
        return mapper.getInvestigatingSummary(userId);
    }

    public List<HashMap<String, Object>> getInvestigatingDetails(Long userId) {
        return mapper.getInvestigatingDetails(userId);
    }

    public List<HashMap<String, Object>> getMonthlyCoverage(Date startDate, Date endDate, Long userId, Long product) {
        return mapper.getMonthlyCoverage(startDate, endDate, userId, product);
    }

    public List<HashMap<String, Object>> getDistrictCoverage(Long period, Long product, Long user) {
        return mapper.getDistrictCoverage(period, product,user);
    }

    public List<HashMap<String, Object>> getFacilityCoverage(Long period, Long product, Long userId) {
        return mapper.getFacilityCoverage(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityCoverageDetails(Date startDate, Date endDate, Long productId, Long userId) {
        return mapper.getFacilityCoverageDetails(startDate, endDate, productId, userId);
    }

    public List<HashMap<String, Object>> getFacilitySessions(Long period, Long userId) {
        return mapper.getFacilitySessions(period, userId);
    }

    public List<HashMap<String, Object>> getFacilitySessionsDetails(Date startDate, Date endDate, Long userId) {
        return mapper.getFacilitySessionsDetails(startDate, endDate, userId);
    }

    public List<HashMap<String, Object>> getFacilityWastage(Long period, Long product, Long userId) {
        return mapper.getFacilityWastage(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityWastageDetails(Date startDate, Date endDate, Long productId, Long userId) {
        return mapper.getFacilityWastageDetails(startDate, endDate, productId, userId);
    }

    public List<HashMap<String, Object>> getFacilityDropout(Long period, Long product, Long userId) {
        return mapper.getFacilityDropout(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityDropoutDetails(Date startDate, Date endDate, Long productId, Long userId) {
        return mapper.getFacilityDropoutDetails(startDate, endDate, productId, userId);
    }

    public List<HashMap<String, Object>> getMonthlyWastage(Date startDate, Date endDate, Long productId) {
        return mapper.getMonthlyWastage(startDate, endDate, productId);
    }

    public List<HashMap<String, Object>> getWastageByDistrict(Long period, Long product,Long user) {
        return mapper.getWastageByDistrict(period, product,user);
    }

    public List<HashMap<String, Object>> getMonthlySessions(Date startDate, Date endDate) {

        return mapper.getMonthlySessions(startDate, endDate);
    }

    public List<HashMap<String, Object>> getDistrictSessions(Long period,Long user) {

        return mapper.getDistrictSessions(period,user);
    }


    public List<HashMap<String, Object>> getBundling(Date startDate, Date endDate, Long productId) {

        return mapper.getBundling(startDate, endDate, productId);
    }
    

    public List<HashMap<String, Object>> getMonthlyDropout(Date startDate, Date endDate, Long product) {

        return mapper.getMonthlyDropout(startDate, endDate, product);
    }

    public List<HashMap<String, Object>> getDistrictDropout(Long period, Long product,Long user) {
        return mapper.getDistrictDropout(period, product,user);
    }

    public List<HashMap<String, Object>> getMonthlyStock(Date startDate, Date endDate, Long product) {

        return mapper.getMonthlyStock(startDate, endDate, product);
    }

    public List<HashMap<String, Object>> getDistrictStock(Long period, Long product) {
        return mapper.getDistrictStock(period, product);
    }

    public Long isDistrictUser(Long userId) {
        return mapper.isDistrictUser(userId);
    }

    public List<HashMap<String, Object>> getFacilityStock(Long period, Long product, Long userId) {
        return mapper.getFacilityStock(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityStockDetail(Date fromDate, Date toDate, Long product, Long userId) {
        return mapper.getFacilityStockDetail(fromDate, toDate, product, userId);
    }

    public List<HashMap<String, Object>> getStockStatusByMonthly(Date startDate, Date endDate, Long userId, Long product) {
        return mapper.getStockStatusByMonthly(startDate, endDate, userId, product);
    }

    public List<HashMap<String, Object>> getDistrictStockStatus(Long period, Long product, Long user) {
        return mapper.getDistrictStockStatus(period, product, user);
    }

    public List<HashMap<String, Object>> getFacilityStockStatus(Long period, Long product, Long userId) {
        return mapper.getFacilityStockStatus(period, product, userId);
    }

    public List<HashMap<String, Object>> getFacilityStockStatusDetails(Date startDate, Date endDate, Long productId, Long userId) {
        return mapper.getFacilityStockStatusDetails(startDate, endDate, productId, userId);
    }

    public Map<String, Object> getVaccineCurrentPeriod() {
        return mapper.getVaccineCurrentReportingPeriod();
    }

    public Map<String, Object> getUserZoneInformation(Long userId) {
        return mapper.getUserZoneInformation(userId);
    }

    public List<HashMap<String, Object>> getFacilityVaccineInventoryStockStatus(Long facilityId, String date) {
        return mapper.getFacilityVaccineInventoryStockStatus(facilityId, date);
    }

    public List<HashMap<String, Object>> getSupervisedFacilitiesVaccineInventoryStockStatus(String facilityIds, Long productId, String date, String level) {
        return mapper.getSupervisedFacilitiesProductStockStatus(facilityIds, productId, date, level);
    }
}
