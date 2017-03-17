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

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupplyPartner;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.SupplyPartnerService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@NoArgsConstructor
public class SupplyPartnerController extends BaseController {

  private static final String SUPPLY_PARTNERS = "supply_partners";
  private static final String SUPPLY_PARTNER = "supply_partner";
  private static final String PRODUCTS = "products";
  private static final String FACILITIES = "facilities";

  @Autowired
  SupplyPartnerService service;

  @Autowired
  FacilityService facilityService;

  @Autowired
  ProductService productService;


  @RequestMapping(value = "/supply-partners", headers = ACCEPT_JSON, method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response(SUPPLY_PARTNERS,service.getAll());
  }

  @Transactional
  @RequestMapping(value = "/supply-partners", headers = ACCEPT_JSON, method = RequestMethod.POST)
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody SupplyPartner partner, HttpServletRequest request){
     service.insert(partner, loggedInUserId(request));
     return OpenLmisResponse.response(SUPPLY_PARTNER, partner);
  }

  @Transactional
  @RequestMapping(value = "/supply-partners/{id}", headers = ACCEPT_JSON, method = RequestMethod.PUT)
  public ResponseEntity<OpenLmisResponse> update(@PathVariable("id") Long id,  @RequestBody SupplyPartner partner, HttpServletRequest request){
    service.update(partner, loggedInUserId(request));
    return OpenLmisResponse.response(SUPPLY_PARTNER, partner);
  }

  @RequestMapping(value="/supply-partners/{id}.json", headers = ACCEPT_JSON, method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getSupplyPartnerById(@PathVariable("id") Long id){
    return OpenLmisResponse.response(SUPPLY_PARTNER, service.getById(id));
  }

  @RequestMapping(value="/supply-partners/facility-list.json", method = RequestMethod.POST)
  public ResponseEntity<OpenLmisResponse> facilityListByCode(@RequestBody List<String> facilityCodes){
    return OpenLmisResponse.response(FACILITIES, facilityService.getListByCodes(facilityCodes));
  }

  @RequestMapping(value="/supply-partners/product-list.json", method = RequestMethod.POST)
  public ResponseEntity<OpenLmisResponse> productListByCode(@RequestBody List<String> facilityCodes){
    return OpenLmisResponse.response(PRODUCTS, productService.getListByCodes(facilityCodes));
  }

}
