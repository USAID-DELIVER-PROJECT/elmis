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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class VaccineDashboardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineDashboardService.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    VaccineDashboardRepository repository;

    public Map<String, Object> getReportingSummary(Long userId) {

        Map<String, Object> reportingSummaryList=null ;
        try {
            reportingSummaryList = repository.getReportingSummary(userId);
        } catch (Exception ex) {
            LOGGER.warn("error while loading Reporting summary:... ", ex);
        }
        return reportingSummaryList;
    }

    public List<HashMap<String, Object>> getReportingDetails(Long userId) {
        List<HashMap<String, Object>> reportingDetails = null;
        try {
        reportingDetails= repository.getReportingDetails(userId);
        } catch (Exception ex) {
            LOGGER.warn("error while loading Reporting summary:... ", ex);

        }
        return reportingDetails;
    }

    public Map<String, Object> getRepairingSummary(Long userId) {
        Map<String, Object> repairingDetailList = null;
        try {


            repairingDetailList = repository.getRepairingSummary(userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getRepairingDetails(Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getRepairingDetails(userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public Map<String, Object> getInvestigatingSummary(Long userId) {
        Map<String, Object> repairingDetailList = null;
        try {


            repairingDetailList =repository.getInvestigatingSummary(userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getInvestigatingDetails(Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getInvestigatingDetails(userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getMonthlyCoverage(String startDate, String endDate, Long userId, Long product) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getMonthlyCoverage(fromDate, toDate, userId, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityCoverage(Long period, Long product, Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityCoverage(period, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityCoverageDetails(String startDate, String endDate, Long product, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityCoverageDetails(fromDate, toDate, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilitySessions(Long period, Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilitySessions(period, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilitySessionsDetails(String startDate, String endDate, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilitySessionsDetails(fromDate, toDate, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityWastage(Long period, Long product, Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityWastage(period, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityWastageDetails(String startDate, String endDate, Long product, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityWastageDetails(fromDate, toDate, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityDropout(Long period, Long product, Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getFacilityDropout(period, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityDropoutDetails(String startDate, String endDate, Long product, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getFacilityDropoutDetails(fromDate, toDate, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getDistrictCoverage(Long period, Long product) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictCoverage(period, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getMonthlyWastage(String startDate, String endDate, Long product) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getMonthlyWastage(fromDate, toDate, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getMonthlySessions(String startDate, String endDate) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getMonthlySessions(fromDate, toDate);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getDistrictSessions(Long period) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getDistrictSessions(period);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getBundling(String startDate, String endDate, Long productId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getBundling(fromDate, toDate, productId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }


    public List<HashMap<String, Object>> getWastageByDistrict(Long period, Long product) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getWastageByDistrict(period, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getMonthlyDropout(String startDate, String endDate, Long product) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getMonthlyDropout(fromDate, toDate, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getDistrictDropout(Long period, Long product) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictDropout(period, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getMonthlyStock(String startDate, String endDate, Long product) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getMonthlyStock(fromDate, toDate, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getDistrictStock(Long period, Long product) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictStock(period, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }
    public  boolean isDistrictUser(Long userId){
        boolean districtUser = false;
        try {
            districtUser= repository.isDistrictUser(userId)>0;
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return districtUser;
    }

    public List<HashMap<String, Object>> getFacilityStock(Long period, Long product, Long userId) {
        List<HashMap<String, Object>> facilityStockList = null;

        try {
            facilityStockList = repository.getFacilityStock(period, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return facilityStockList;
    }

    public List<HashMap<String, Object>> getFacilityStockDetail(String startDate, String endDate, Long product, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> facilityStockDetailList = null;

        try {
            facilityStockDetailList =repository.getFacilityStockDetail(fromDate, toDate, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return facilityStockDetailList;
    }
}
