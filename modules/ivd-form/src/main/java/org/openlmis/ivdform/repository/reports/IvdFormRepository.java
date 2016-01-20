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

package org.openlmis.ivdform.repository.reports;

import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.ivdform.domain.reports.DiseaseLineItem;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.dto.ReportStatusDTO;
import org.openlmis.ivdform.dto.RoutineReportDTO;
import org.openlmis.ivdform.repository.mapper.reports.IvdFormMapper;
import org.openlmis.ivdform.service.LineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IvdFormRepository {

  @Autowired
  IvdFormMapper mapper;

  @Autowired
  LineItemService lineItemService;

  @Autowired
  GeographicZoneService geographicZoneService;


  public void insert(VaccineReport report) {
    mapper.insert(report);
    saveDetails(report);
  }


  public void saveDetails(VaccineReport report) {
    lineItemService.saveLogisticsLineItems(report.getLogisticsLineItems(), report.getId());
    lineItemService.saveDiseaseLineItems(report.getDiseaseLineItems(), report.getId());
    lineItemService.saveCoverageLineItems(report.getCoverageLineItems(), report.getId());
    lineItemService.saveColdChainLIneItems(report.getColdChainLineItems(), report.getId());
    lineItemService.saveVitaminLineItems(report.getVitaminSupplementationLineItems(), report.getId());
    lineItemService.saveAdverseEffectLineItems(report.getAdverseEffectLineItems(), report.getId());
    lineItemService.saveCampaignLineItems(report.getCampaignLineItems(), report.getId());
  }

  public void update(VaccineReport report, Long userId) {
    report.setModifiedBy(userId);
    mapper.update(report);
    saveDetails(report);
  }

  public VaccineReport getById(Long id) {
    return mapper.getById(id);
  }

  public VaccineReport getByIdWithFullDetails(Long id) {
    return mapper.getByIdWithFullDetails(id);
  }

  public VaccineReport getByProgramPeriod(Long facilityId, Long programId, Long periodId) {
    return mapper.getByPeriodFacilityProgram(facilityId, programId, periodId);
  }

  public VaccineReport getLastReport(Long facilityId, Long programId) {
    return mapper.getLastReport(facilityId, programId);
  }

  public Long getScheduleFor(Long facilityId, Long programId) {
    return mapper.getScheduleFor(facilityId, programId);
  }

  public List<ReportStatusDTO> getReportedPeriodsForFacility(Long facilityId, Long programId) {
    return mapper.getReportedPeriodsForFacility(facilityId, programId);
  }

  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId) {
    return mapper.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }

  public List<DiseaseLineItem> getDiseaseSurveillance(Long reportId) {
    return mapper.getDiseaseSurveillance(reportId);
  }

  public Long findLastReportBeforePeriod(Long facilityId, Long programId, Long periodId) {
    return mapper.findPreviousReport(facilityId, programId, periodId);
  }

  public List<RoutineReportDTO> getApprovalPendingForms(String facilityIds) {
    return mapper.getApprovalPendingReports(facilityIds);
  }

  public List<VaccineReport> getRejectedReports(Long facilityId, Long programId) {
    return mapper.getRejectedReports(facilityId, programId);
  }
}
