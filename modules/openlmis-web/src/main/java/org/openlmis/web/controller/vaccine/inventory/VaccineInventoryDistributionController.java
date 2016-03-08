/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine.inventory;


import org.apache.ibatis.annotations.Param;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.service.StockRequirementsService;
import org.openlmis.vaccine.service.inventory.VaccineInventoryDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/vaccine/inventory/distribution")
public class VaccineInventoryDistributionController extends BaseController {
    private static final String PROGRAMS = "programs";
    private static final String FACILITIES = "facilities";
    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    private static final String FORECAST = "forecast";
    private static final String CURRENT_PERIOD = "currentPeriod";
    private static final String DISTRIBUTION = "distribution";
    private static final String LAST_PERIOD = "lastPeriod";
    private static final String SUPERVISOR_ID = "supervisorId";

    @Autowired
    VaccineInventoryDistributionService service;
    @Autowired
    FacilityService facilityService;

    @Autowired
    ProgramProductService programProductService;

    @Autowired
    StockRequirementsService requirementsService;

    @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineDistribution distribution, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("distributionId", service.save(distribution, userId));
    }

    @RequestMapping(value = "get-distributed/{facilityId}/{programId}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getAll(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("Distributions", service.getDistributionForFacilityByPeriod(facilityId, programId));
    }

    @RequestMapping(value = "supervised-facilities/{programId}.json", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getUserSupervisedFacilities(@PathVariable Long programId, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response(FACILITIES, facilityService.getUserSupervisedFacilities(userId, programId, RightName.MANAGE_STOCK));
    }

    @RequestMapping(value = "by-voucher-number", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getDistributionByVoucherNumber(@Param("voucherNumber") String voucherNumber,
                                                                           HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("distribution", service.getDistributionByVoucherNumber(userId,voucherNumber));
    }

    @RequestMapping(value = "saveConsolidatedDistributionList", method = POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> saveConsolidatedDistributionList(@RequestBody List<VaccineDistribution> distribution, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        return OpenLmisResponse.response("distributionIds", service.saveConsolidatedList(distribution, userId));
    }


    @RequestMapping(value = "getAllDistributionsForNotification", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> getAllDistributionsForNotification(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        return OpenLmisResponse.response("remarks", service.getAllDistributionsForNotification(facilityId));
    }

    @RequestMapping(value = "UpdateDistributionsForNotification/{id}", method = GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getAllDistributionsForNotification(@PathVariable Long id, HttpServletRequest request) {
        return OpenLmisResponse.response("updated", service.UpdateNotification(id));
    }

    @RequestMapping(value = "last-period/{facilityId}/{programId}", method = GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getSupervisedLastPeriod(@PathVariable Long facilityId, @PathVariable Long programId,
                                                                    HttpServletRequest request) {
        return OpenLmisResponse.response("last-period", service.getLastPeriod(facilityId, programId));
    }

    @RequestMapping(value = "facility-distribution-forecast-lastPeriod/{facilityId}/{programId}", method = GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getDataByFacility(@PathVariable Long facilityId, @PathVariable Long programId,
                                                              HttpServletRequest request) {
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(DISTRIBUTION, service.getDistributionForFacilityByPeriod(facilityId, programId));
        response.getBody().addData(LAST_PERIOD, service.getLastPeriod(facilityId, programId));
        response.getBody().addData(CURRENT_PERIOD, service.getCurrentPeriod(facilityId, programId));
        response.getBody().addData(FORECAST, requirementsService.getStockRequirements(facilityId, programId));
        response.getBody().addData(PROGRAM_PRODUCT_LIST, programProductService.getByProgram(new Program(programId)));

        return response;
    }

    @RequestMapping(value = "distribution-supervisorid/{facilityId}", method = GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK, VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getDistributionByToFacilityId(@PathVariable Long facilityId,
                                                                          HttpServletRequest request) {
        if (null == facilityId) {
            return OpenLmisResponse.error(messageService.message("error.facility.unknown"), HttpStatus.BAD_REQUEST);
        } else {
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(DISTRIBUTION, service.getDistributionByToFacility(facilityId));
            response.getBody().addData(SUPERVISOR_ID, service.getSupervisorFacilityId(facilityId));
            return response;
        }
    }

}
