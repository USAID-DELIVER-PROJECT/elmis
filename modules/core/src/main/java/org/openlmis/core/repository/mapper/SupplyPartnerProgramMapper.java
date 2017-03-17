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

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.SupplyPartner;
import org.openlmis.core.domain.SupplyPartnerProgram;
import org.openlmis.core.domain.SupplyPartnerProgramFacility;
import org.openlmis.core.domain.SupplyPartnerProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyPartnerProgramMapper {

  @Select("select * from supply_partners where id in (select supplyPartnerId from supply_partner_programs where programid = #{programId})")
  List<SupplyPartner> getPartnersThatSupportProgram(@Param("programId") Long programId);

  @Select("select * from supply_partner_programs where supplyPartnerId = #{partnerId}")
  @Results( value = {
      @Result(column = "id", property = "id"),
      @Result(column = "id", property = "products", many = @Many(select = "org.openlmis.core.repository.mapper.SupplyPartnerProgramMapper.getProducts")),
      @Result(column = "id", property = "facilities", many = @Many(select = "org.openlmis.core.repository.mapper.SupplyPartnerProgramMapper.getFacilities"))
  })
  List<SupplyPartnerProgram> getProgramsForPartner(@Param("partnerId") Long partnerId );

  @Select("select distinct ps.* from supply_partner_programs ps " +
      " join supply_partner_program_facilities f on f.supplyPartnerProgramId = ps.id " +
      " where f.facilityId = #{facilityId} and ps.sourceProgramId = #{programId}")
  List<SupplyPartnerProgram> getSubscriptions(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("select distinct ps.* from supply_partner_programs ps " +
      " join supply_partner_program_facilities f on f.supplyPartnerProgramId = ps.id " +
      " where f.facilityId = #{facilityId} and ps.sourceProgramId = #{programId}")
  @Results( value = {
      @Result(column = "id", property = "id"),
      @Result(column = "id", property = "products", many = @Many(select = "org.openlmis.core.repository.mapper.SupplyPartnerProgramMapper.getProducts")),
      @Result(column = "id", property = "facilities", many = @Many(select = "org.openlmis.core.repository.mapper.SupplyPartnerProgramMapper.getFacilities"))
  })
  List<SupplyPartnerProgram> getSubscriptionsWithDetails(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("select s.*, f.name, f.code, s.active from supply_partner_program_facilities s " +
      " join facilities f on f.id = s.facilityId " +
      " where supplyPartnerProgramId = #{id}")
  List<SupplyPartnerProgramFacility> getFacilities(@Param("id") Long id);

  @Select("select s.*, p.primaryName, p.code, s.active from supply_partner_program_products s " +
      " join products p on p.id = s.productId " +
      " where supplyPartnerProgramId = #{id}")
  List<SupplyPartnerProgramProduct> getProducts(@Param("id") Long id);

  @Insert("insert into supply_partner_programs " +
      " ( supplyPartnerId, sourceProgramId, destinationProgramId, destinationSupervisoryNodeId, destinationRequisitionGroupId, createdBy, createdDate ) " +
      " values " +
      " (#{supplyPartnerId}, #{sourceProgramId}, #{destinationProgramId}, #{destinationSupervisoryNodeId}, #{destinationRequisitionGroupId}, #{createdBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  int insert(SupplyPartnerProgram spp);

  @Delete("delete from supply_partner_programs where supplyPartnerId = #{id}")
  int delete(@Param("id") Long id);

  @Update("update supply_partner_programs " +
      "set supplyPartnerId = #{supplyPartnerId}, " +
      "sourceProgramId = #{sourceProgramId}, " +
      "destinationProgramId = #{destinationProgramId}," +
      "destinationSupervisoryNodeId = #{destinationSupervisoryNodeId}, " +
      "destinationRequisitionGroupId = #{destinationRequisitionGroupId}" +
      "where " +
      "id = #{id}")
  int update(SupplyPartnerProgram spp);

  @Delete("delete from supply_partner_program_facilities where supplyPartnerProgramId = #{id}")
  void deleteFacilities(@Param("id") Long supplyPartnerProgramId);

  @Delete("delete from supply_partner_program_products where supplyPartnerProgramId = #{id}")
  void deleteProducts(@Param("id") Long supplyPartnerProgramId);

  @Insert("insert into supply_partner_program_facilities (supplyPartnerProgramId, facilityId, createdDate) values(#{supplyPartnerProgramId}, #{facilityId}, NOW())")
  void insertFacilities(SupplyPartnerProgramFacility facility);

  @Insert("insert into supply_partner_program_products (supplyPartnerProgramId, productId, createdDate) values (#{supplyPartnerProgramId}, #{productId}, NOW())")
  void insertProduct(SupplyPartnerProgramProduct product);
}
