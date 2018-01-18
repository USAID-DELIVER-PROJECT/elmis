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

package org.openlmis.equipment.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.DailyColdTraceStatus;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.openlmis.equipment.dto.ColdTraceSummaryDTO;
import org.openlmis.equipment.dto.DailyColdTraceStatusDTO;
import org.openlmis.equipment.repository.mapper.DailyColdTraceStatusMapper;
import org.openlmis.equipment.repository.mapper.EquipmentOperationalStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DailyColdTraceStatusService {

  @Autowired
  private DailyColdTraceStatusMapper mapper;

  @Autowired
  private EquipmentInventoryService equipmentInventoryService;

  @Autowired
  private EquipmentOperationalStatusMapper equipmentOperationalStatusMapper;


  public void saveDailyStatus(DailyColdTraceStatus status, Long userId) {
    EquipmentInventory inventory = equipmentInventoryService.getInventoryBySerialNumber(status.getSerialNumber());
    if (inventory == null) {
      throw new DataException("Serial Number was not found. Please ensure that the equipment is registered as an equipment inventory at the facility.");
    }
    DailyColdTraceStatus persistedStatus = mapper.getForEquipmentForDate(inventory.getId(), status.getDate());
    status.setEquipmentInventory(inventory);
    if (persistedStatus != null) {
      status.setId(persistedStatus.getId());
      status.setCreatedBy(persistedStatus.getCreatedBy());
      status.setCreatedDate(persistedStatus.getCreatedDate());
      status.setModifiedBy(userId);
      mapper.update(status);
    } else {
      status.setCreatedBy(userId);
      mapper.insert(status);
    }
  }

  public List<DailyColdTraceStatus> findStatusForFacilityPeriod(Long facilityId, Long periodId) {
    return mapper.getForFacilityForPeriod(facilityId, periodId);
  }

  public List<EquipmentOperationalStatus> findPossibleStatuses() {
    return equipmentOperationalStatusMapper.getOperationalStatusByCategory("CCE");
  }

  public List<ColdTraceSummaryDTO> getLastSubmissionStatus(String code) {
    return mapper.getLastSubmission(code);
  }

  public List<DailyColdTraceStatusDTO> getStatusSubmittedFor(String serialNumber) {
    return mapper.getDailyStatusSubmittedFor(serialNumber);
  }


}
