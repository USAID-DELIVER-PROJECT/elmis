/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.rnr.domain.ManualTestesLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTestsLineItemMapper {


    @Select("SELECT manualtest.*, testtypes.name AS testTypeName " +
            "FROM manual_test_line_item manualtest  " +
            "  JOIN manual_test_types testtypes " +
            "  ON testtypes.id = manualtest.testtypeid where rnrId = #{rnrId} order by testtypes.displayorder")
    List<ManualTestesLineItem> getManualTestLineItemsByRnrId(Long rnrId);

    @Select("SELECT id AS testTypeId, name AS testTypeName, null AS remark, " +
            "null AS testCount FROM manual_test_types;")
    List<ManualTestesLineItem> getGeneratedEmptyManualTestLineItem();

    @Insert(" INSERT INTO manual_test_line_item " +
            "(rnrid, testtypeid, testcount, remark, createdBy, createdDate, modifiedBy, modifiedDate) " +
            " VALUES " +
            "(#{rnrId},#{testTypeId},#{testCount},#{remark}, #{createdBy}, COALESCE(#{createdDate}, NOW()), #{modifiedBy}, COALESCE(#{modifiedDate}, NOW()))")
    @Options(useGeneratedKeys = true)
    Integer insertManualTestLineItem(ManualTestesLineItem item);

    @Update("UPDATE manual_test_line_item SET " +
            " rnrid = #{rnrId}, " +
            " testtypeid = #{testTypeId}, " +
            " testcount = #{testCount}, " +
            " remark = #{remark}, " +
            " modifiedBy = #{modifiedBy}, " +
            " modifiedDate = COALESCE(#{modifiedDate}, NOW()) where id = #{id}")
    Integer updateManualTestLineItem(ManualTestesLineItem item);
}
