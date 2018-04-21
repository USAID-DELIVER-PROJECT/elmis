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

package org.openlmis.lookupapi.controller;

import io.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.lookupapi.model.Contact;
import org.openlmis.lookupapi.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@NoArgsConstructor
@Api(value = "Contacts", description = "APIs to get list of Contacts", position = 1)
public class ContactsController extends BaseController{

  @Autowired
  ContactService service;

  @RequestMapping(value = "/rest-api/contacts/facility-approvers", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public List<Contact> getApprovers(@RequestParam(value = "facilityId") Long facilityId){
    return service.getApprovers(facilityId);
  }

  @RequestMapping(value = "/rest-api/contacts/facility-contacts", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public List<Contact> getContacts(@RequestParam("facilityId") Long facilityId){
    return service.getFacilityContacts(facilityId);
  }


}
