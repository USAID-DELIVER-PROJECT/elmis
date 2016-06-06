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
import org.openlmis.core.domain.Product;
import org.openlmis.ivdform.domain.VaccineProductDose;
import org.openlmis.ivdform.domain.reports.VaccineCoverageItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoverageMapper {

  @Insert("INSERT into vaccine_report_coverage_line_items " +
      " (reportId, productId, doseId, displayName, displayOrder , trackMale, trackFemale, regularMale, regularFemale, outreachMale, outreachFemale, campaignMale, campaignFemale, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{reportId}, #{productId}, #{doseId}, #{displayName}, #{displayOrder}, #{trackMale}, #{trackFemale}, #{regularMale}, #{regularFemale}, #{outreachMale}, #{outreachFemale}, #{campaignMale}, #{campaignFemale}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineCoverageItem item);

  @Update("UPDATE vaccine_report_coverage_line_items " +
      " SET " +
      " reportId = #{reportId} " +
      " , skipped = #{skipped}" +
      " , productId = #{productId} " +
      " , doseId  = #{doseId} " +
      " , displayName = #{displayName}  " +
      " , displayOrder = #{displayOrder}  " +
      " , trackMale = #{trackMale}  " +
      " , trackFemale = #{trackFemale}  " +
      " , regularMale = #{regularMale} " +
      " , regularFemale = #{regularFemale} " +
      " , outreachMale = #{outreachMale} " +
      " , outreachFemale = #{outreachFemale} " +
      " , campaignMale = #{campaignMale} " +
      " , campaignFemale = #{campaignFemale} " +
      " , modifiedBy = #{modifiedBy} " +
      " , modifiedDate = NOW()" +
      " WHERE id = #{id} ")
  void update(VaccineCoverageItem item);

  @SuppressWarnings("unused")
  @Select("SELECT id, code, primaryName FROM products where id = #{id}")
  Product getProductDetails(Long id);

  @Select("SELECT * from vaccine_report_coverage_line_items WHERE id = #{id}")
  VaccineCoverageItem getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_report_coverage_line_items " +
      "WHERE reportId = #{reportId} " +
      "   and productId = #{productId} " +
      "   and doseId = #{doseId} " +
      "order by id")
  @Results(value = {
      @Result(property = "productId", column = "productId"),
      @Result(property = "product", javaType = Product.class, column = "productId",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.CoverageMapper.getProductDetails")),
  })
  VaccineCoverageItem getCoverageByReportProductDosage(@Param("reportId") Long reportId, @Param("productId") Long productId, @Param("doseId") Long doseId);

  @Select("select d.* from vaccine_reports r " +
      " join vaccine_report_coverage_line_items cli on cli.reportId = r.id " +
      " join vaccine_product_doses d on d.doseId = cli.doseId and cli.productId = d.productId and d.programId = r.programId" +
      " where cli.id = #{id} " +
      " order by cli.id")
  VaccineProductDose getVaccineDoseDetail(@Param("id") Long id);

  @Select("SELECT " +
      "li.*, " +
      "(select sum(regularMale + regularFemale) from vaccine_report_coverage_line_items ili join vaccine_reports ir on ir.id = ili.reportId join processing_periods ipps on ipps.id = ir.periodId where ir.facilityId = r.facilityId and ili.productId = li.productId and li.doseId = ili.doseId and pps.startDate > ipps.startDate and extract(year from ipps.startDate) = extract(year from pps.startDate) ) as previousRegular , " +
      "(select sum(outreachMale + outreachFemale) from vaccine_report_coverage_line_items ili join vaccine_reports ir on ir.id = ili.reportId join processing_periods ipps on ipps.id = ir.periodId where ir.facilityId = r.facilityId and ili.productId = li.productId and li.doseId = ili.doseId and pps.startDate > ipps.startDate and extract(year from ipps.startDate) = extract(year from pps.startDate) ) as previousOutreach " +
      " from " +
      "   vaccine_report_coverage_line_items li join vaccine_reports r on r.id = li.reportId " +
      "     join processing_periods pps on pps.id = r.periodId " +
      "WHERE " +
      "     reportId = #{reportId} " +
      "order by li.id ASC")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "productId", column = "productId"),
      @Result(property = "product", javaType = Product.class, column = "productId",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.CoverageMapper.getProductDetails")),
      @Result(property = "vaccineProductDose", column = "id", one = @One(select = "org.openlmis.ivdform.repository.mapper.reports.CoverageMapper.getVaccineDoseDetail"))
  })
  List<VaccineCoverageItem> getLineItems(@Param("reportId") Long reportId);

}
