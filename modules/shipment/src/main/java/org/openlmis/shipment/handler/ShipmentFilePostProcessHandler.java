/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.springframework.integration.support.MessageBuilder.withPayload;

/**
 * ShipmentFilePostProcessHandler sends the shipment file to archive folder if successfully processed
 * else to the error folder.
 */

@Component
@NoArgsConstructor
public class ShipmentFilePostProcessHandler {

  @Autowired
  private ShipmentService shipmentService;

  @Autowired
  private MessageChannel shipmentErrorChannel;

  @Autowired
  private MessageChannel shipmentArchiveOutputChannel;

  @Autowired
  private OrderService orderService;

  private static final Logger logger = Logger.getLogger(ShipmentFilePostProcessHandler.class);


  @Transactional
  public void process(Set<String> orderNumbers, File shipmentFile, boolean success, List<ShipmentLineItemDTO> skippedLineItems, Exception exp) {
    ObjectMapper mapper = new ObjectMapper();
    String productIssues = "";
    String exception = "";
    String orderNumber = (!orderNumbers.isEmpty())? orderNumbers.stream().findFirst().get():"";

    try {
      productIssues = mapper.writeValueAsString(skippedLineItems);
      exception = mapper.writeValueAsString(exp);
    }catch(Exception e){
      logger.warn(e.getMessage());
    }

    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo(orderNumber, shipmentFile.getName(), !success, productIssues, exception, (skippedLineItems.size() > 0));

    shipmentService.insertShipmentFileInfo(shipmentFileInfo);

    orderService.updateStatusAndShipmentIdForOrders(orderNumbers, shipmentFileInfo);

    Message<File> message = withPayload(shipmentFile).build();

    if (success) {
      shipmentArchiveOutputChannel.send(message);
      logger.debug("Shipment file " + shipmentFile.getName() + " archived");
    } else {
      shipmentErrorChannel.send(message);
      logger.warn("Shipment file " + shipmentFile.getName() + " copied to error folder");
    }

    if (!deleteQuietly(shipmentFile)) {
      logger.error("Unable to delete temporary shipment file " + shipmentFile.getName());
    }
  }


}
