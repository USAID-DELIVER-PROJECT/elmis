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

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.domain.DailyStockStatusLineItem;
import org.openlmis.rnr.dto.MSDStockStatusDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface DailyStockStatusMapper {

  @Insert("insert into daily_stock_status (facilityId, programId, date, source, createdBy, createdDate) " +
      "values " +
      "(#{facilityId}, #{programId}, #{date}, #{source}, #{createdBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(DailyStockStatus stockStatus);

  @Delete("DELETE from daily_stock_status where facilityId = #{facilityId} and programId = #{programId} and date = #{date}")
  void clearStatusForFacilityDate(@Param("facilityId") Long facilityId, @Param("programId")Long programId,  @Param("date") Date date);

  @Insert("insert into daily_stock_status_line_items " +
      "(stockStatusSubmissionId, productId, stockOnHand, lastTransactionDate, createdDate, createdBy) " +
      "values " +
      "(#{stockStatusSubmissionId}, #{productId}, #{stockOnHand}, #{lastTransactionDate}, NOW(), #{createdBy})")
  void insertLineItem(DailyStockStatusLineItem lineItem);

  @Insert(" INSERT INTO public.msd_stock_statuses(\n" +
          "             ilId, facilityId, productId, onHandDate, onHandQuantity, \n" +
          "            mos, createdDate, createdBy)\n" +
          "    VALUES ( #{ilId}, #{facilityId}, #{productId}, #{onHandDate}, #{onHandQuantity}, \n" +
          "            #{mos}, NOW(), #{createdBy}) ")
  @Options(useGeneratedKeys = true)
  Integer insertMSDStatus(MSDStockStatusDTO dto);

  @Select("select * from msd_stock_statuses where ilId=#{ilId} ")
  MSDStockStatusDTO getByMSDILId(String ilId);

}
