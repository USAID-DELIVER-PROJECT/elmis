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

import lombok.NoArgsConstructor;
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentImportedOrder;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@NoArgsConstructor
public class ShipmentController extends BaseController{

  @Autowired
  OrderService orderService;

  @Autowired
  ShipmentService shipmentService;

  @RequestMapping(value = "/rest-api/shipment/order-list", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public @ResponseBody List<ShipmentImportedOrder> getOrderList(@RequestParam("facilityCode") String facility ) {
    return shipmentService.getShipmentImportedOrders(facility);
  }

  @RequestMapping(value = "/rest-api/shipment/line-items", method = RequestMethod.GET, headers = ACCEPT_JSON)
  public @ResponseBody List<ShipmentLineItem> getShipmentLineItems(@RequestParam("orderid") Long orderId ) {
    return shipmentService.getLineItems(orderId);
  }

  @RequestMapping(value = "/rest-api/shipment/skipped-shipment-items", method = RequestMethod.GET)
  public @ResponseBody List<ShipmentLineItemDTO> getSkippedLineItems(@RequestParam("orderid") Long orderId){
    return shipmentService.getSkippedShipmentLineItems(orderId);
  }

}
