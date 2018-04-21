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
package org.openlmis.web.controller.vaccine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.service.VaccineDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/vaccine/dashboard/")
@Api("Immunization Rest APIs")
public class VaccineDashboardController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(VaccineDashboardController.class);
    @Autowired
    VaccineDashboardService service;

    @RequestMapping(value = "summary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getReportingSummary(HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        Map<String, Object> summary = new HashMap<>();
        try {
            summary.put("reportingSummary", service.getReportingSummary(userId));
            summary.put("repairing", service.getRepairingSummary(userId));
            summary.put("investigating", service.getInvestigatingSummary(userId));
        } catch (Exception ex) {

            LOGGER.warn("for user" + userId + " " + ex.getMessage(), ex);
        }
        return OpenLmisResponse.response("summary", summary);
    }

    @RequestMapping(value = "reporting-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getReportingDetails(HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("reportingDetails", service.getReportingDetails(userId));
    }

    @RequestMapping(value = "repairing-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getRepairingDetails(HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("repairingDetails", service.getRepairingDetails(userId));
    }

    @RequestMapping(value = "investigating-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getInvestigatingDetails(HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("investigatingDetails", service.getInvestigatingDetails(userId));
    }

    @RequestMapping(value = "monthly-coverage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCoverageByMonthly(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("monthlyCoverage", service.getMonthlyCoverage(startDate, endDate, userId, product));
    }

    @RequestMapping(value = "facility-coverage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityCoverage(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityCoverage", service.getFacilityCoverage(period, product, userId));
    }

    @RequestMapping(value = "facility-coverage-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityCoverageDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityCoverageDetails", service.getFacilityCoverageDetails(startDate, endDate, product, userId));
    }

    @RequestMapping(value = "facility-sessions.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilitySessions(@RequestParam("period") Long period, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilitySessions", service.getFacilitySessions(period, userId));
    }

    @RequestMapping(value = "facility-sessions-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilitySessionsDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilitySessionsDetails", service.getFacilitySessionsDetails(startDate, endDate, userId));
    }

    @RequestMapping(value = "facility-wastage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityWastage(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityWastage", service.getFacilityWastage(period, product, userId));
    }

    @RequestMapping(value = "facility-wastage-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityWastageDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityWastageDetails", service.getFacilityWastageDetails(startDate, endDate, product, userId));
    }


    @RequestMapping(value = "facility-dropout.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityDropout(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityDropout", service.getFacilityDropout(period, product, userId));
    }

    @RequestMapping(value = "facility-dropout-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityDropoutDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityDropoutDetails", service.getFacilityDropoutDetails(startDate, endDate, product, userId));
    }

    @RequestMapping(value = "district-coverage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictCoverage(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("districtCoverage", service.getDistrictCoverage(period, product, userId));
    }

    @RequestMapping(value = "monthly-wastage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getWastageByMonthly(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product) {

        return OpenLmisResponse.response("wastageMonthly", service.getMonthlyWastage(startDate, endDate, product));
    }

    @RequestMapping(value = "district-wastage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getWastageByDistrict(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("districtWastage", service.getWastageByDistrict(period, product, userId));
    }


    @RequestMapping(value = "sessions.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getSessions(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {


        return OpenLmisResponse.response("monthlySessions", service.getMonthlySessions(startDate, endDate));
    }

    @RequestMapping(value = "district-sessions.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictSessions(@RequestParam("period") Long period, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("districtSessions", service.getDistrictSessions(period, userId));
    }

    @RequestMapping(value = "monthly-dropout.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getMonthlyDropout(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product) {
        return OpenLmisResponse.response("monthlyDropout", service.getMonthlyDropout(startDate, endDate, product));
    }

    @RequestMapping(value = "district-dropout.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictDropout(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long user = this.loggedInUserId(request);
        return OpenLmisResponse.response("districtDropout", service.getDistrictDropout(period, product, user));
    }


    @RequestMapping(value = "bundle.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getBundling(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long productId) {
        return OpenLmisResponse.response("bundling", service.getBundling(startDate, endDate, productId));
    }

    @RequestMapping(value = "monthly-stock.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getMonthlyStock(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product) {
        return OpenLmisResponse.response("monthlyStock", service.getMonthlyStock(startDate, endDate, product));
    }

    @RequestMapping(value = "district-stock.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictStock(@RequestParam("period") Long period, @RequestParam("product") Long product) {
        return OpenLmisResponse.response("districtStock", service.getDistrictStock(period, product));
    }

    @RequestMapping(value = "facility-stock.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityStock(@RequestParam("period") Long period,
                                                             @RequestParam("product") Long product,
                                                             HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("facilityStock", service.getFacilityStock(period, product, userId));
    }

    @RequestMapping(value = "facility-stock-detail.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityStockDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                                                                    @RequestParam("product") Long product,
                                                                    HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("facilityStockDetail", service.getFacilityStockDetail(startDate, endDate, product, userId));
    }

    @RequestMapping(value = "isDistrictUser.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> isDistrictUser(HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        boolean isUserDistrict = false;
        try {
            isUserDistrict = this.service.isDistrictUser(userId);
        } catch (Exception ex) {

            LOGGER.warn("for user" + userId + " " + ex.getMessage(), ex);
        }
        return OpenLmisResponse.response("district_user", isUserDistrict);
    }

    @RequestMapping(value = "monthly-stock-status.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getStockStatusByMonthly(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("monthlyStockStatus", service.getStockStatusByMonthly(startDate, endDate, userId, product));
    }

    @RequestMapping(value = "facility-stock-status.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityStockStatus(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityStockStatus", service.getFacilityStockStatus(period, product, userId));
    }

    @RequestMapping(value = "facility-stock-status-details.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityStockStatusDetails(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);

        return OpenLmisResponse.response("facilityStockStatusDetails", service.getFacilityStockStatusDetails(startDate, endDate, product, userId));
    }

    @RequestMapping(value = "district-stock-status.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictStockStatus(@RequestParam("period") Long period, @RequestParam("product") Long product, HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("districtStockStatus", service.getDistrictStockStatus(period, product, userId));
    }

    @RequestMapping(value = "vaccine-current-period.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getVaccineCurrentPeriod() {

        return OpenLmisResponse.response("vaccineCurrentPeriod", service.getVaccineCurrentReportingPeriod());
    }

    @RequestMapping(value = "user-geographic-zone-preference.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getUserZoneInformation(HttpServletRequest request) {
        return OpenLmisResponse.response("UserGeographicZonePreference", service.getUserZoneInformation(loggedInUserId(request)));
    }

    @RequestMapping(value = "facility-inventory-stock-status.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFacilityVaccineInventoryStockStatus(@RequestParam("facilityId") Long facilityId,
                                                                                   @Param("date") String date, HttpServletRequest request) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = (date == null) ? formatter.format(new Date()) : date;
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("facilityStockStatus", service.getFacilityVaccineInventoryStockStatus(loggedInUserId(request), dateString));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "supervised-facilities-inventory-stock-status.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getSupervisedFacilitiesVaccineInventoryStockStatus(@RequestParam("productId") Long productId,
                                                                                               @RequestParam("date") String date,
                                                                                               @RequestParam("level") String level,
                                                                                               HttpServletRequest request) {

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = (date == null) ? formatter.format(new Date()) : date;
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("facilityStockStatus", service.getSupervisedFacilitiesVaccineInventoryStockStatus(userId, productId, dateString, level));
        response.getBody().addData("date", dateString);
        return response;
    }


    @RequestMapping(value = "stock-status-over-view.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getStockStatusOverView(
            @Param("category") Long category, @Param("level") String level,
            HttpServletRequest request) {

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("stockOverView", service.getStockStatusOverView(userId, category, dateString, level));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "vaccineInventoryStockDetails.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> vaccineInventoryStockDetails(
            @Param("status") String status, @Param("category") String category,
            HttpServletRequest request) {
        Long productId = 0L;
        String level = " ";

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("vaccineInventoryStockDetails", service.getInventoryStockStatusDetail(category, userId, status, dateString, level));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "vaccineInventoryDetails.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> vaccineInventoryStockDetails(
            @Param("category") Long category, @Param("level") String level,
            HttpServletRequest request) {
        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("vaccineInventoryStockDetails", service.getVaccineInventoryStockByStatus(category, level, userId));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "vaccineInventoryFacilityDetails.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> vaccineInventoryStockDetails(
            @Param("category") Long category, @Param("level") String level, @Param("product") String product, String color,
            HttpServletRequest request) {

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("facilities", service.getVaccineInventoryFacilitiesByProduct(category, level, userId, product, color));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "vaccineStockEvent.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> vaccineStockEventByMonth(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("period") Long period,
            @Param("year") Long year,
            @Param("district") Long district
    ) {

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("events",
                service.getStockEventByMonth(product, period, year, district));
        response.getBody().addData("date", dateString);
        return response;
    }

    @RequestMapping(value = "availableStock.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getAvailableStockForDashboard(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("period") Long period,
            @Param("year") Long year,
            @Param("district") Long district
    ) {

        Long userId = this.loggedInUserId(request);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("availableStock",
                service.getAvailableStockForDashboard(product, period, year, userId));
        response.getBody().addData("date", dateString);
        return response;
    }


    @RequestMapping(value = "immunizationAPI.json", method = RequestMethod.GET)
    @ApiOperation(position = 1, value = "Get All Monthly Vaccine Immunization ")
    public ResponseEntity<OpenLmisResponse> getImmmunizationAPI(
            HttpServletRequest request

    ) {

        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("immunization",
                service.getVaccineImmunization());
        return response;
    }

    @RequestMapping(value = "fullStockAvailability.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getFullStockAvailability(
            HttpServletRequest request,
            @Param("period") Long periodId,
            @Param("year") Long year

    ) {

        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("fullStocks",
                service.getFullStockAvailability(loggedInUserId(request), periodId, year));
        return response;
    }


    @RequestMapping(value = "getNationalPerformance.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getPerformanceReport(
            HttpServletRequest request,
            @Param("productId") Long productId,
            @Param("periodId") Long periodId,
            @Param("year") Long year

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("performance", service.getNationalPerformance(loggedInUserId(request), productId, periodId, year));
        return response;
    }

    @RequestMapping(value = "reportingTarget.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getPerformanceReport(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("reportingTarget", service.reportingTarget(loggedInUserId(request), periodId, year));
        return response;
    }

    @RequestMapping(value = "categorization.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictCategorization(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year,
            @Param("doseId") Long doseId,
            @Param("product") Long product

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("categories", service.getDistrictCategorization(loggedInUserId(request), product, doseId, year, periodId));
        return response;
    }

    @RequestMapping(value = "VaccineCoverageByRegionAndProduct.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getVaccineCoverageByRegionAndProduct(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year,
            @Param("productId") Long productId,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("coverage", service.getVaccineCoverageByRegionAndProduct(
                loggedInUserId(request), productId, periodId, year, doseId));
        return response;
    }

    @RequestMapping(value = "VaccineNationalCoverage.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getNationalVaccineCoverage(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year,
            @Param("product") Long product,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("natioanl_coverage", service.getNationalVaccineCoverage(
                loggedInUserId(request), product, doseId, periodId, year));
        return response;
    }

    @RequestMapping(value = "VaccineNationalCoverageByProductAndDose.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getNationalCoverageProductAndDose(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year,
            @Param("product")Long product

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("natioanl_coverage", service.getNationalCoverageProductAndDose(
                loggedInUserId(request), periodId, year,product));
        return response;
    }

    @RequestMapping(value = "VaccineDistrictInvetorySummary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictInventorySummary(
            HttpServletRequest request

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_summary", service.getDistrictInventorySummary(
                loggedInUserId(request)));
        return response;
    }

    @RequestMapping(value = "VaccineRegionInvetorySummary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getRegionInventorySummary(
            HttpServletRequest request

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("region_summary", service.getRegionInventorySummary(
                loggedInUserId(request)));
        return response;
    }

    @RequestMapping(value = "InventorySummaryByLocation.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getInventorySummaryByLocation(
            HttpServletRequest request, @Param("facilityLevel") String facilityLevel,
            @Param("status") String status

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("inventory_summary", service.getInventorySummaryByLocation(facilityLevel));
        return response;
    }

    @RequestMapping(value = "InventorySummaryByMaterial.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getInventorySummaryByMaterial(
            HttpServletRequest request, @Param("facilityLevel") String facilityLevel,
            @Param("status") String status

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("inventory_summary", service.getInventorySummaryByMaterial(facilityLevel));
        return response;
    }


    @RequestMapping(value = "GetCoverageForMap.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCoverageForMap(
            HttpServletRequest request,
            @Param("periodId") Long periodId,
            @Param("year") Long year,
            @Param("product") Long product,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("map_data", service.getVaccineCoverageForMap(loggedInUserId(request), product, periodId, year, doseId));
        return response;
    }


    @RequestMapping(value = "getInventorySummaryByMaterialFacilityList.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getInventorySummaryByMaterialFacilityList(
            HttpServletRequest request,
            @Param("facilityLevel") String facilityLevel,
            @Param("product") String product,
            @Param("indicator") String indicator

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("facility_list", service.getInventorySummaryByMaterialFacilityList(facilityLevel, product, indicator));
        return response;
    }

    @RequestMapping(value = "GetCoverageByDistrictSummary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCoverageByDistrict(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("period") Long period,
            @Param("year") Long year,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_coverage", service.getCoverageByDistrict(
                loggedInUserId(request), product, period, year, doseId));
        return response;
    }

    @RequestMapping(value = "GetCoverageByRegionSummary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCoverageByRegionClassification(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("period") Long period,
            @Param("year") Long year,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("region_coverage", service.getCoverageByRegionSummary(
                loggedInUserId(request), product, period, year, doseId));
        return response;
    }

    @RequestMapping(value = "GetCategorizationByDistrictSummary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCategorizationByDistrict(
            HttpServletRequest request,
            @Param("year") Long year

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_categorization", service.getCategorizationByDistrict(
                loggedInUserId(request), year));
        return response;
    }

    @RequestMapping(value = "GetCategorizationByDistrictDrillDown.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getCategorizationByDistrictDrillDown(
            HttpServletRequest request,
            @Param("category") String category,
            @Param("period") String period

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_categorization_summary", service.getCategorizationByDistrictDrillDown(
                loggedInUserId(request), category, period));
        return response;
    }

    @RequestMapping(value = "GetDistrict_classification_summary.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictClassification(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("year") Long year

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_classification_summary", service.getDistrictClassification(
                loggedInUserId(request), product, year));
        return response;
    }

  @RequestMapping(value = "GetDistrict_classification_drill_down.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistrictClassificationDrillDown(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("year") Long year,
            @Param("indicator") String indicator,
            @Param("period") String period

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("district_classification_summary", service.getDistrictClassificationDrillDown(
                loggedInUserId(request), product, year,indicator,period));
        return response;
    }

    @RequestMapping(value = "GetDistributionOfDistrictPerPerformance.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistributionOfDistrictPerPerformance(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("year") Long year,
            @Param("doseId") Long doseId

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("distribution_per_district", service.getDistributionOfDistrictPerPerformance(
                loggedInUserId(request), product, year,doseId));
        return response;
    }

    @RequestMapping(value = "GetPerformanceMonitoring.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getPerformanceMonitoring(
            HttpServletRequest request,
            @Param("product") Long product,
            @Param("year") Long year

    ) {
        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.response("performance", service.getPerformanceMonitoring(
                loggedInUserId(request), product, year));
        return response;
    }


}
