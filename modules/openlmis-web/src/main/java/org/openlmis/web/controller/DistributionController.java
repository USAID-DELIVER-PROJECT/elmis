/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.UserService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class DistributionController extends BaseController {

  @Autowired
  DistributionService distributionService;

  @Autowired
  UserService userService;

  @RequestMapping(value = "/distributions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> create(@RequestBody Distribution distribution, HttpServletRequest request) {
    Distribution existingDistribution = distributionService.get(distribution);

    if (existingDistribution != null) {
      return returnInitiatedDistribution(distribution, existingDistribution);
    }

    distribution.setCreatedBy(loggedInUserId(request));
    distribution.setModifiedBy(loggedInUserId(request));

    Distribution initiatedDistribution = distributionService.create(distribution);

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", initiatedDistribution);
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success"));
    return openLmisResponse.response(CREATED);
  }

  private ResponseEntity<OpenLmisResponse> returnInitiatedDistribution(Distribution distribution, Distribution existingDistribution) {
    existingDistribution.setDeliveryZone(distribution.getDeliveryZone());
    existingDistribution.setPeriod(distribution.getPeriod());
    existingDistribution.setProgram(distribution.getProgram());

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", existingDistribution);
    User createdByUser = userService.getById(existingDistribution.getCreatedBy());
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.already.exists",
      createdByUser.getUserName(), existingDistribution.getCreatedDate()));
    return openLmisResponse.response(OK);
  }


}