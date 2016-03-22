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

package org.openlmis.ivdform.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.ivdform.domain.reports.AdverseEffectLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdverseEffectMapper {


  @Insert("INSERT INTO vaccine_report_adverse_effect_line_items " +
      "(reportId, productId, date, manufacturer, batch, expiry, cases, isInvestigated, investigationDate, notes,relatedToLineItemId, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      "( #{reportId}, #{productId}, #{date}, #{manufacturer}, #{batch}, #{expiry}, #{cases}, #{isInvestigated}, #{investigationDate}, #{notes}, #{relatedToLineItemId}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(AdverseEffectLineItem lineItem);

  @Update("UPDATE vaccine_report_adverse_effect_line_items " +
      " SET" +
      " reportId = #{reportId}" +
      " , productId = #{productId}" +
      " , date = #{date}" +
      " , manufacturer = #{manufacturer} " +
      " , batch = #{batch}" +
      " , expiry = #{expiry}" +
      " , cases = #{cases}" +
      " , relatedToLineItemId = #{relatedToLineItemId}" +
      " , investigationDate = #{investigationDate}" +
      " , isInvestigated = #{isInvestigated}" +
      " , notes = #{notes}" +
      " , modifiedBy = #{modifiedBy}" +
      " , modifiedDate = NOW() " +
      " WHERE id = #{id}")
  void update(AdverseEffectLineItem lineItem);

  @Select("SELECT e.*, p.primaryName as productName " +
      " from vaccine_report_adverse_effect_line_items e " +
      "     join products p on p.id = e.productId " +
      " where reportId = #{reportId} and relatedToLineItemId is null " +
      " order by e.id")
  @Results({
      @Result(property = "id", column = "id"),
      @Result(property = "relatedLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.AdverseEffectMapper.getRelatedLineItems")),
  })
  List<AdverseEffectLineItem> getLineItems(@Param("reportId") Long reportId);

  @Select("SELECT e.*, p.primaryName as productName " +
      " from vaccine_report_adverse_effect_line_items e " +
      "     join products p on p.id = e.productId " +
      " where relatedToLineItemId = #{id} " +
      " order by e.id")
  List<AdverseEffectLineItem> getRelatedLineItems(@Param("id") Long id);

  @Delete("DELETE from vaccine_report_adverse_effect_line_items where reportId = #{reportId} and relatedToLineItemId is not null")
  void deleteRelatedLineItems(@Param("reportId")Long reportId);

  @Delete("DELETE from vaccine_report_adverse_effect_line_items where reportId = #{reportId} and relatedToLineItemId is null")
  void deleteLineItems(@Param("reportId")Long reportId);
}
