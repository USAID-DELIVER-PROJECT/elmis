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

package org.openlmis.rnr.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.domain.DailyStockStatusLineItem;
import org.openlmis.rnr.dto.MSDStockStatusDTO;
import org.openlmis.rnr.repository.mapper.DailyStockStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public class DailyStockStatusRepository {

  @Autowired
  private DailyStockStatusMapper mapper;


  public void insert(DailyStockStatus stockStatus){
    mapper.insert(stockStatus);
    for(DailyStockStatusLineItem lineItem: stockStatus.getLineItems()){
      lineItem.setStockStatusSubmissionId(stockStatus.getId());
      lineItem.setCreatedBy(stockStatus.getCreatedBy());
      mapper.insertLineItem(lineItem);
    }
  }

  public void clearStatusForFacilityProgramDate(Long facilityId, Long programId, Date date){
    mapper.clearStatusForFacilityDate(facilityId, programId, date);
  }
@Transactional
  public Long saveMsdStockStatus(MSDStockStatusDTO statusDTO) {
    mapper.insertMSDStatus(statusDTO);
    return statusDTO.getId();
  }

  public MSDStockStatusDTO getByTransactionId(String ilId){
    return mapper.getByMSDILId(ilId);
  }
}
