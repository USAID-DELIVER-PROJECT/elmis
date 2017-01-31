/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentImportedOrder;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Repository class for shipment file related database operations.
 */

@Repository
@NoArgsConstructor
public class ShipmentRepository {

  private ShipmentMapper shipmentMapper;

  private static org.slf4j.Logger logger = LoggerFactory.getLogger(ShipmentRepository.class);

  @Autowired
  public ShipmentRepository(ShipmentMapper shipmentMapper) {
    this.shipmentMapper = shipmentMapper;
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);
  }

  public void save(ShipmentLineItem shipmentLineItem) {
    try {
      shipmentMapper.insertShippedLineItem(shipmentLineItem);
    } catch (DataIntegrityViolationException exception) {
      throw new DataException("error.incorrect.length");
    }
  }

  public List<ShipmentLineItem> getLineItems(Long orderId) {
    return shipmentMapper.getLineItems(orderId);
  }

  public List<ShipmentImportedOrder> getShipmentImportedOrders(String facilityCode) {
    return shipmentMapper.getShipmentImportedOrders(facilityCode);
  }

  public List<ShipmentLineItemDTO> getSkippedLineItems(Long orderId) {
    String list = shipmentMapper.getSkippedLineItems(orderId);
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<ShipmentLineItemDTO> result = mapper.readValue(list, new TypeReference<List<ShipmentLineItemDTO>>() {
      });
      return result;
    } catch (Exception exp) {
      logger.warn(exp.getMessage());
    }
    return Collections.emptyList();
  }
}
