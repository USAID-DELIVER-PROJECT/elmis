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

package org.openlmis.restapi.controller;

import io.swagger.annotations.ApiOperation;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.service.DailyStockStatusSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Controller
@NoArgsConstructor
public class DailyStockStatusController extends BaseController {

  @Autowired
  DailyStockStatusSubmissionService service;

  @ApiOperation(value = "Accepts Daily Stock Status!", httpMethod = "POST")
  @RequestMapping(value = "/rest-api/daily-stock-status.json", method = RequestMethod.POST)
  public ResponseEntity<OpenLmisResponse> submitDailyStockStatus(@RequestBody DailyStockStatus dailyStockStatus, BindingResult bindingResult, HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      return OpenLmisResponse.error(bindingResult.getGlobalError().toString(), HttpStatus.BAD_REQUEST);
    }
    dailyStockStatus.setCreatedBy(loggedInUserId(request.getUserPrincipal()));
    try {
      service.save(dailyStockStatus);
    }catch(SQLException exception){
      throw new DataException(exception.getMessage());
    }
    return OpenLmisResponse.success("Submission Accepted!");
  }

@ApiOperation(value = "Accepts Daily MSD Stock Status!", httpMethod = "POST")
  @RequestMapping(value = "/rest-api/msd-stock-status.json", method = RequestMethod.POST)
  public ResponseEntity<OpenLmisResponse> getMSDStockStatus(@RequestBody String dailyStockStatus, BindingResult bindingResult, HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      return OpenLmisResponse.error(bindingResult.getGlobalError().toString(), HttpStatus.BAD_REQUEST);
    }

  System.out.println(dailyStockStatus);
  System.out.println("reached Here");

    try {
      service.saveMSDStockStatus(dailyStockStatus,loggedInUserId(request.getUserPrincipal()));
    }catch(Exception exception){
      throw new DataException(exception.getMessage());
    }
  System.out.println("First Response");
    return OpenLmisResponse.success("Submission Accepted!");
  }

}
