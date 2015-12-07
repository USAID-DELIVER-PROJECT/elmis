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
package org.openlmis.web.controller.vaccine.var;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.var.VarItems;
import org.openlmis.vaccine.service.var.VarItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Controller
@RequestMapping(value = "/vaccine/var_items")
public class VarItemsController  extends BaseController {

    @Autowired
    private VarItemsService service;

    @RequestMapping(value="get/{id}")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
        return OpenLmisResponse.response("var_items", service.getById(id));
    }

    @RequestMapping(value="getByShipment/{shipmentnumber}")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable String shipmentnumber) {
        return OpenLmisResponse.response("var_items", service.getItemsByPackage(shipmentnumber));
    }

    @RequestMapping(value="getByLot/{shipmentnumber}/{lotnumber}")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable String shipmentnumber,@PathVariable String lotnumber) {
        return OpenLmisResponse.response("var_items", service.getItemsByLot(shipmentnumber, lotnumber));
    }


    @RequestMapping(value="delete/{id}", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch (DataException e) {
            return OpenLmisResponse.error(e, BAD_REQUEST);
        }
        return OpenLmisResponse.success("Deleted Successful");
    }

    @RequestMapping(value="all")
    public ResponseEntity<OpenLmisResponse> getAll() {
        return OpenLmisResponse.response("var_items", service.getAll());
    }

    @RequestMapping(value="save", headers = ACCEPT_JSON, method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<OpenLmisResponse> save(@RequestBody VarItems var_items) {
        try {
            service.save(var_items);
        } catch (DataException e) {
            return OpenLmisResponse.error(e, BAD_REQUEST);
        }
        return OpenLmisResponse.response("var_items", service.getById(var_items.getId()));
    }
}

