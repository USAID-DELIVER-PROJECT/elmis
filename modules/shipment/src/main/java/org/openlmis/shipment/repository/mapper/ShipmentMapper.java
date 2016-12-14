/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentImportedOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the ShipmentFileInfo and ShipmentLineItem entity to corresponding representation in database.
 */

@Repository
public interface ShipmentMapper {

  @Insert({"INSERT INTO shipment_line_items",
    "(orderId, orderNumber, productCode, substitutedProductCode, substitutedProductName, quantityShipped, cost, packedDate, shippedDate, productName, dispensingUnit, productCategory,",
    "packsToShip, productCategoryDisplayOrder, productDisplayOrder, fullSupply, batch) VALUES",
    "(#{orderId}, #{orderNumber}, #{productCode}, #{substitutedProductCode},  #{substitutedProductName} ,#{quantityShipped}, #{cost}, #{packedDate}, #{shippedDate}, #{productName}, #{dispensingUnit}, #{productCategory},",
    "#{packsToShip}, #{productCategoryDisplayOrder}, #{productDisplayOrder}, #{fullSupply}, #{batch})"})
  @Options(useGeneratedKeys = true)
  void insertShippedLineItem(ShipmentLineItem shipmentLineItem);

  @Insert({"INSERT INTO shipment_file_info " +
      "(orderNumber, fileName, processingError, orderProcessingExceptions, skippedShipmentLineItems, hasSkippedLineItems) " +
      "VALUES " +
      "(#{orderNumber}, #{fileName},#{processingError}, #{orderProcessingExceptions}, cast(#{skippedShipmentLineItems} as json), #{hasSkippedLineItems} )"})
  @Options(useGeneratedKeys = true)
  void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo);

  @Select("SELECT * FROM shipment_file_info WHERE id = #{id}")
  ShipmentFileInfo getShipmentFileInfo(Long id);

  @Select({"SELECT * FROM shipment_line_items WHERE orderId = #{orderId}"})
  List<ShipmentLineItem> getLineItems(Long orderId);


  @Select("SELECT o.id as orderId, pr.id as programId, f.id as facilityId, pr.name as programName, f.name as facilityName, p.startDate as periodStartDate, p.endDate as periodEndDate, r.emergency as isEmergency  FROM orders o " +
      " join requisitions r on r.id = o.id " +
      " join facilities f on f.id = r.facilityId " +
      " join processing_periods p on p.id = r.periodId " +
      " join programs pr on pr.id = r.programId " +
      " where f.code = #{facilityCode} and r.id in (select orderId from shipment_line_items)" +
      " order by p.startDate")
  List<ShipmentImportedOrder> getShipmentImportedOrders(@Param("facilityCode") String facilityCode);

  @Select("SELECT skippedShipmentLineItems from shipment_file_info where orderNumber = (select orderNumber from orders where id = #{orderId})")
  String getSkippedLineItems(@Param("orderId") Long orderId);
}