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
package org.openlmis.ivdform.controller;

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.ivdform.dto.VaccineServiceConfigDTO;
import org.openlmis.ivdform.service.ProductDoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/vaccine/product-dose/")
public class ProductDoseController extends BaseController {

  private static final String PROTOCOL = "protocol";

  @Autowired
  private ProductDoseService service;

  @RequestMapping(value = "get/{programId}")
  public ResponseEntity<OpenLmisResponse> getProgramProtocol(@PathVariable Long programId) {
    VaccineServiceConfigDTO dto = service.getProductDoseForProgram(programId);
    return OpenLmisResponse.response(PROTOCOL, dto);
  }

  @RequestMapping(value = "save", headers = ACCEPT_JSON, method = RequestMethod.PUT)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody VaccineServiceConfigDTO config) {
    service.save(config.getProtocols());
    return OpenLmisResponse.response(PROTOCOL, config);
  }

}
