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
import org.openlmis.core.domain.*;
import org.openlmis.core.service.*;
import org.openlmis.report.util.StringHelper;
import org.openlmis.vaccine.repository.VaccineDashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
public class VaccineDashboardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineDashboardService.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    VaccineDashboardRepository repository;
    @Autowired
    ProgramService programService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProcessingPeriodService processingPeriodService;
    @Autowired
    private GeographicZoneService zoneService;

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

    public List<HashMap<String, Object>> getDistrictCoverage(Long period, Long product,Long user) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictCoverage(period, product,user);
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

    public List<HashMap<String, Object>> getDistrictSessions(Long period,Long user) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getDistrictSessions(period,user);
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


    public List<HashMap<String, Object>> getWastageByDistrict(Long period, Long product,Long user) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getWastageByDistrict(period, product,user);
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

    public List<HashMap<String, Object>> getDistrictDropout(Long period, Long product,Long user) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictDropout(period, product,user);
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
    public List<HashMap<String, Object>> getStockStatusByMonthly(String startDate, String endDate, Long userId, Long product) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getStockStatusByMonthly(fromDate, toDate, userId, product);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityStockStatus(Long period, Long product, Long userId) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityStockStatus(period, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }

    public List<HashMap<String, Object>> getFacilityStockStatusDetails(String startDate, String endDate, Long product, Long userId) {
        Date fromDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate).toDate();
        Date toDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate).toDate();
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList =repository.getFacilityStockStatusDetails(fromDate, toDate, product, userId);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }
    public List<HashMap<String, Object>> getDistrictStockStatus(Long period, Long product,Long user) {
        List<HashMap<String, Object>> repairingDetailList = null;
        try {


            repairingDetailList = repository.getDistrictStockStatus(period, product, user);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return repairingDetailList;
    }
public Map<String, Object> getVaccineCurrentReportingPeriod(){
    return repository.getVaccineCurrentPeriod();
}
    public Map<String, Object> getUserZoneInformation(Long userId) {
        return repository.getUserZoneInformation(userId);
    }

    public List<HashMap<String, Object>> getFacilityVaccineInventoryStockStatus(Long facilityId, String date) {
        List<HashMap<String, Object>> stockStatusList = null;
        try {
            stockStatusList = repository.getFacilityVaccineInventoryStockStatus(facilityId, date);
        } catch (Exception ex) {
            LOGGER.warn("error occured.... ", ex);
        }
        return stockStatusList;
    }

    public List<HashMap<String, Object>> getSupervisedFacilitiesVaccineInventoryStockStatus(Long userId, Long productId, String date, String level) {
        List<HashMap<String, Object>> stockStatusList = null;

        List<Program> vaccineProgram = programService.getAllIvdPrograms();
        if (vaccineProgram != null) {
            Long programId = vaccineProgram.get(0).getId();
            List<org.openlmis.core.domain.Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, RightName.MANAGE_STOCK);

            String facilityIds = StringHelper.getStringFromListIds(facilities);
            try {
                stockStatusList = repository.getSupervisedFacilitiesVaccineInventoryStockStatus(facilityIds, productId, date, level);
            } catch (Exception ex) {
                LOGGER.warn("error occured.... ", ex);
            }
        }
        return stockStatusList;

    }

    public List<HashMap<String, Object>> getStockStatusOverView(Long userId,Long category,  String dateString, String level) {
        System.out.println(category);
        Long categoryId = 0L;
        Facility homeFacility = facilityService.getHomeFacility(userId);
       // System.out.println(homeFacility);
       // FacilityType ft = facilityService.getFacilityTypeById(homeFacility.getFacilityType().getId());
        if(level == null){
            level = "dvs";
        }
        if(category == null){
            ProductCategory pc= productCategoryService.getByCode("Vaccine");
            category = pc.getId();
        }
        return repository.getStockStatusOverView(userId,category,dateString,level);
    }

    public List<HashMap<String, Object>> getInventoryStockStatusDetail(String category,Long userId, String status,String dateString, String level) {
         if(category == null){
             ProductCategory pc= productCategoryService.getByCode("Vaccine");
             category = pc.getId().toString();
         }

        return repository.getInventoryStockStatusDetail(category,userId,status, dateString, level);
    }


    public List<HashMap<String, Object>> getVaccineInventoryStockByStatus(Long category, String level,Long userId) {
        if(category == null){
            ProductCategory pc= productCategoryService.getByCode("Vaccine");
            category = pc.getId();
        }

        return repository.getVaccineInventoryStockByStatus(category,level,userId);
    }

    public List<HashMap<String,Object>> getVaccineInventoryFacilitiesByProduct(Long category, String level, Long userId, String product, String color) {
       Long productId = 0L;
        if(category == null){
            ProductCategory pc= productCategoryService.getByCode("Vaccine");
            category = pc.getId();
        }
        if(product !=null){
            Product pr = productService.getByPrimaryName(product);
            productId = pr.getId();
        }

        return  repository.getVaccineInventoryFacilitiesByProduct(category,level,userId, productId,color);
    }

  /*  public List<HashMap<String, Object>> getStockEventByMonth(Long ) {
        return repository.geStockEventByMonth();
    }*/

    public List<HashMap<String, Object>> getStockEventByMonth(Long product, Long period, Long year, Long district) {

        ProcessingPeriod period1= processingPeriodService.getById(period);
       Long period2 =0L;
        Date value;
        if(period1 != null){
            value = period1.getStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            period2= Long.valueOf(cal.get(Calendar.MONTH));
            System.out.println(period2 + 1);
        }
        Long levelId = 0L;
        GeographicZone geographicZone =  zoneService.getById(district);
        if(geographicZone !=null){
            levelId = geographicZone.getLevel().getId();
        }
        Facility facility = facilityService.getByGeographicZoneId(district,levelId);

        Long id;
        if(facility !=null)
            id = facility.getId();
        else
           id = 0L;
//        System.out.println(facility.getName());
        return repository.geStockEventByMonth(product,period2+1,year,id);

    }

    public List<HashMap<String,Object>>getAvailableStockForDashboard(Long product, Long period,
                                                                     Long year, Long userId){
        System.out.println(product);
        return repository.getAvailableStockForDashboard(product,period,year,userId);
    }

    public List<HashMap<String,Object>>getVaccineImmunization(){
        return repository.getVaccineImmunization();
    }

    public List<HashMap<String,Object>>getFullStockAvailability(Long userId,Long periodId,Long year){
        return repository.getFullStockAvailability(userId,periodId,year);
    }

    public List<HashMap<String,Object>>getNationalPerformance(Long userId,Long productId,Long periodId, Long year){
        return repository.getNationalPerformance(userId,productId,periodId,year);
    }
    public List<HashMap<String,Object>>reportingTarget(Long userId,Long periodId, Long year){
        return repository.reportingTarget(userId,periodId,year);
    }
    public List<HashMap<String,Object>>getDistrictCategorization(Long userId,Long product,Long doseId, Long year,Long periodId){
        return repository.getDistrictCategorization(userId,product,doseId,year,periodId);
    }

    public List<HashMap<String,Object>>getVaccineCoverageByRegionAndProduct(Long userId, Long productId, Long periodId, Long year,Long doseId){
        return repository.getVaccineCoverageByRegionAndProduct(userId,productId,periodId,year,doseId);
    }

    public List<HashMap<String,Object>>getNationalVaccineCoverage(Long userId, Long product,Long doseId,Long periodId, Long year){
        return repository.getNationalVaccineCoverage(userId, product,doseId,periodId,year);
    }

}
