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

package org.openlmis.web.controller.equipment;

import com.wordnik.swagger.annotations.Api;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.equipment.dto.DailyColdTraceStatusDTO;
import org.openlmis.equipment.service.DailyColdTraceStatusService;
import org.openlmis.restapi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Api(value = "Cold Trace", description = "APIs to report cold trace status")
public class DailyColdTraceStatusController extends BaseController {

  @Autowired
  private DailyColdTraceStatusService service;

  @RequestMapping(value = "/equipment/cold-trace/status", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> findStatusForPeriod(@RequestParam("facility") Long facilityId, @RequestParam("period") Long periodId) {
    return OpenLmisResponse.response("cold_trace_status", service.findStatusForFacilityPeriod(facilityId, periodId));
  }

  @RequestMapping(value = "/rest-api/equipment/cold-trace", method = RequestMethod.POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody DailyColdTraceStatusDTO status, Principal principal) {
    status.validate();
    service.saveDailyStatus(status.buildEntity(), loggedInUserId(principal));
    return OpenLmisResponse.success("Daily cold trace status submitted for " + status.getDate().toString());
  }

  @RequestMapping(value = "/rest-api/equipment/cold-trace/operational-status-options", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> findPossibleStatuses() {
    return OpenLmisResponse.response("statuses", service.findPossibleStatuses());
  }


  @RequestMapping(value = "/rest-api/equipment/cold-trace/regional-submission-status", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getLastSubmissions(@RequestParam("regionCode") String regionCode) {
    return OpenLmisResponse.response("statuses", service.getLastSubmissionStatus(regionCode));
  }

  @RequestMapping(value = "/rest-api/equipment/cold-trace/submissions-for-equipment", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getListOfSubmissions(@RequestParam("serialNumber") String serialNumber) {
    return OpenLmisResponse.response("statuses", service.getStatusSubmittedFor(serialNumber));
  }


}
