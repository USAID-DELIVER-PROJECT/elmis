/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.

 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.ivdform.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.service.IvdFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@Api("IVD Rest APIs")
public class IvdFormController extends BaseController {

  private static final String PERIODS = "periods";
  private static final String REPORT = "report";
  private static final String PENDING_SUBMISSIONS = "pending_submissions";


  @Autowired
  private IvdFormService service;

  @RequestMapping(value = {"/vaccine/report/periods/{facilityId}/{programId}", "/rest-api/ivd/periods/{facilityId}/{programId}"}, method = RequestMethod.GET)
  @ApiOperation(position = 2, value = "Get Open IVD Periods for Facility")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId) {
    return OpenLmisResponse.response(PERIODS, service.getPeriodsFor(facilityId, programId, new Date()));
  }

  @RequestMapping(value = {"/vaccine/report/view-periods/{facilityId}/{programId}", "/rest-api/ivd/view-periods/{facilityId}/{programId}"}, method = RequestMethod.GET)
  @ApiOperation(position = 3, value = "Get Periods for Which facility has IVD Submissions")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_IVD')")
  public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId) {
    return OpenLmisResponse.response(PERIODS, service.getReportedPeriodsFor(facilityId, programId));
  }


  @RequestMapping(value = {"rest-api/ivd/initialize/{facilityId}/{programId}/{periodId}"}, method = RequestMethod.GET)
  @ApiOperation(position = 4, value = "Initiate and IVD form")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> initializeFromRestAPI(
          @PathVariable Long facilityId,
          @PathVariable Long programId,
          @PathVariable Long periodId,
          HttpServletRequest request
  ) {
    return OpenLmisResponse.response(REPORT, service.initialize(facilityId, programId, periodId, loggedInUserId(request), true));
  }

  @RequestMapping(value = {"/vaccine/report/initialize/{facilityId}/{programId}/{periodId}"}, method = RequestMethod.GET)
  @ApiOperation(position = 4, value = "Initiate and IVD form")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> initialize(
      @PathVariable Long facilityId,
      @PathVariable Long programId,
      @PathVariable Long periodId,
      HttpServletRequest request
  ) {
    return OpenLmisResponse.response(REPORT, service.initialize(facilityId, programId, periodId, loggedInUserId(request), false));
  }

  @RequestMapping(value = {"/vaccine/report/get/{id}.json", "/rest-api/ivd/get/{id}.json"}, method = RequestMethod.GET)
  @ApiOperation(position = 9, value = "Get IVD form ")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD, VIEW_IVD, APPROVE_IVD')")
  public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id) {
    return OpenLmisResponse.response(REPORT, service.getById(id));
  }

  @RequestMapping(value = {"/vaccine/report/save"}, method = {RequestMethod.PUT})
  @ApiOperation(position = 5, value = "Save IVD form")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.save(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }


  @RequestMapping(value = {"/vaccine/report/submit", "/rest-api/ivd/submit"}, method = {RequestMethod.PUT, RequestMethod.POST})
  @ApiOperation(position = 6, value = "Submit IVD form")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.submit(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }

  @RequestMapping(value = "/vaccine/report/approval-pending")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'APPROVE_IVD')")
  public ResponseEntity<OpenLmisResponse> getPendingFormsForApproval(@RequestParam("program") Long programId, HttpServletRequest request) {
    return OpenLmisResponse.response(PENDING_SUBMISSIONS, service.getApprovalPendingForms(this.loggedInUserId(request), programId));
  }

  @RequestMapping(value = {"/vaccine/report/approve", "/rest-api/ivd/approve"}, method = RequestMethod.PUT)
  @ApiOperation(position = 7, value = "Approve IVD form")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'APPROVE_IVD')")
  public ResponseEntity<OpenLmisResponse> approve(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.approve(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }

  @RequestMapping(value = {"/vaccine/report/reject"}, method = RequestMethod.PUT)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'APPROVE_IVD')")
  public ResponseEntity<OpenLmisResponse> reject(@RequestBody VaccineReport report, HttpServletRequest request) {
    service.reject(report, loggedInUserId(request));
    return OpenLmisResponse.response(REPORT, report);
  }

}
