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

package org.openlmis.lookupapi.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.*;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.lookupapi.mapper.DosageUnitReportMapper;
import org.openlmis.lookupapi.mapper.GeographicLevelReportMapper;
import org.openlmis.report.mapper.lookup.*;
import org.openlmis.report.model.dto.*;
import org.openlmis.report.model.dto.DosageUnit;
import org.openlmis.report.model.dto.Facility;
import org.openlmis.report.model.dto.FacilityType;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.dto.ProductCategory;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.dto.Regimen;
import org.openlmis.report.model.dto.RegimenCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class LookupService {


  @Autowired
  private ProgramReportMapper programMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private DosageUnitReportMapper dosageUnitMapper;

  @Autowired
  private RegimenRepository regimenRepository;

  @Autowired
  private GeographicLevelReportMapper geographicLevelMapper;

  @Autowired
  private FacilityLookupReportMapper facilityReportMapper;

  @Autowired
  private ProcessingPeriodReportMapper processingPeriodMapper;

  @Autowired
  private RegimenCategoryReportMapper regimenCategoryReportMapper;

  @Autowired
  private ProductCategoryReportMapper productCategoryMapper;

  @Autowired
  private AdjustmentTypeReportMapper adjustmentTypeReportMapper;

  @Autowired
  private ProductReportMapper productMapper;

  @Autowired
  private FacilityTypeReportMapper facilityTypeMapper;

  @Autowired
  private GeographicZoneReportMapper geographicZoneMapper;

  @Autowired
  private RegimenReportMapper regimenReportMapper;

  public List<Program> getAllPrograms() {
    return programMapper.getAll();
  }

  public Program getProgramByCode(String code) {
    return programMapper.getProgramByCode(code);
  }

  public List<RegimenCategory> getAllRegimenCategories() {
    return regimenCategoryReportMapper.getAll();
  }

  public List<DosageFrequency> getAllDosageFrequencies() {
    return regimenRepository.getAllDosageFrequencies();
  }

  public List<RegimenProductCombination> getAllRegimenProductCombinations() {
    return regimenRepository.getAllRegimenProductCombinations();
  }

  public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents() {
    return regimenRepository.getAllRegimenCombinationConstituents();
  }

  public List<ProcessingSchedule> getAllProcessingSchedules() {
    return processingScheduleMapper.getAll();
  }

  public List<ProcessingPeriod> getAllProcessingPeriods() {
    return processingPeriodMapper.getAll();
  }

  public Product getProductByCode(String code) {
    return productMapper.getProductByCode(code);
  }

  public List<GeographicLevel> getAllGeographicLevels() {
    return geographicLevelMapper.getAll();
  }

  public List<org.openlmis.report.model.dto.GeographicZone> getAllZones() {
    return geographicZoneMapper.getAll();
  }

  public List<ProductCategory> getAllProductCategories() {
    return this.productCategoryMapper.getAll();
  }

  public List<org.openlmis.core.domain.Product> getFullProductList(RowBounds rowBounds) {
    return productMapper.getFullProductList(rowBounds);
  }

  public List<RegimenConstituentDosage> getAllRegimenConstituentDosages() {
    return regimenRepository.getAllRegimenConstituentsDosages();
  }

  public List<Regimen> getAllRegimens() {
    return regimenReportMapper.getAll();
  }

  public List<DosageUnit> getDosageUnits() {
    return dosageUnitMapper.getAll();
  }

  public List<FacilityType> getAllFacilityTypes() {
    return facilityTypeMapper.getAllFacilityTypes();
  }

  public List<Facility> getAllFacilities(RowBounds bounds) {
    return facilityReportMapper.getAll(bounds);
  }

  public Facility getFacilityByCode(String code) {
    return facilityReportMapper.getFacilityByCode(code);
  }

  public List<ProgramProduct> getAllProgramProducts() {
    return programProductMapper.getAll();
  }

  public List<FacilityTypeApprovedProduct> getAllFacilityTypeApprovedProducts() {
    return facilityApprovedProductMapper.getAll();
  }

  public List<AdjustmentType> getAllAdjustmentTypes() {
    return adjustmentTypeReportMapper.getAll();
  }

}
