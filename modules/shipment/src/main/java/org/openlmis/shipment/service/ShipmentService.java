/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentImportedOrder;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling Shipment file and shipment file line items
 */

@Service
@NoArgsConstructor
public class ShipmentService {
  @Autowired
  private ShipmentRepository shipmentRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private RequisitionService requisitionService;

  @Autowired
  private ProgramProductService programProductService;

  public void save(ShipmentLineItem shipmentLineItem) {
    if (shipmentLineItem.getQuantityShipped() < 0) {
      throw new DataException("error.negative.shipped.quantity");
    }
    RnrLineItem lineItem;
    if ((lineItem = requisitionService.getNonSkippedLineItem(shipmentLineItem.getOrderId(), shipmentLineItem.getProductCode())) != null) {
      shipmentLineItem.fillReferenceFields(lineItem);
    } else {
      Product product = productService.getByCode(shipmentLineItem.getProductCode());
      if (product == null) {
        throw new DataException("error.unknown.product");
        //TODO: log product in database and get past this line.
      }

      Long programId = requisitionService.getProgramId(shipmentLineItem.getOrderId());
      ProgramProduct programProduct = programProductService.getByProgramAndProductId(programId, product.getId());
      if (programProduct != null) {
        programProduct.setProduct(product);
        shipmentLineItem.fillReferenceFields(programProduct);
      } else {
        shipmentLineItem.fillReferenceFields(product);
      }
    }

    if (shipmentLineItem.getSubstitutedProductCode() != null) {
      if (productService.getByCode(shipmentLineItem.getSubstitutedProductCode()) == null) {
        throw new DataException("error.unknown.product");
        //TODO: log product in database and get past this line.
      }
    }

    shipmentRepository.save(shipmentLineItem);
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
  }

  public List<ShipmentLineItem> getLineItems(long orderId) {
    return shipmentRepository.getLineItems(orderId);
  }

  public List<ShipmentImportedOrder> getShipmentImportedOrders(String facilityCode){
    return shipmentRepository.getShipmentImportedOrders(facilityCode);
  }

  public List<ShipmentLineItemDTO> getSkippedShipmentLineItems(Long orderId) {
    return shipmentRepository.getSkippedLineItems(orderId);
  }
}
