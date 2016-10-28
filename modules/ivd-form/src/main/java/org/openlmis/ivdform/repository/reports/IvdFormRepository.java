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
import org.openlmis.ivdform.domain.reports.ReportStatus;
import org.openlmis.ivdform.domain.reports.ReportStatusChange;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.dto.ReportStatusDTO;
import org.openlmis.ivdform.dto.RoutineReportDTO;
import org.openlmis.ivdform.repository.mapper.reports.IvdFormMapper;
import org.openlmis.ivdform.repository.mapper.reports.StatusChangeMapper;
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
  StatusChangeMapper statusChangeMapper;

  @Autowired
  GeographicZoneService geographicZoneService;


  public void insert(VaccineReport report, Long userId) {
    mapper.insert(report);
    saveDetails(null, report, userId);
  }


  private void saveDetails(VaccineReport dbVersion, VaccineReport report, Long userId) {
    lineItemService.saveLogisticsLineItems(dbVersion, report.getLogisticsLineItems(), report.getId(), userId);
    lineItemService.saveDiseaseLineItems(dbVersion, report.getDiseaseLineItems(), report.getId());
    lineItemService.saveCoverageLineItems(dbVersion, report.getCoverageLineItems(), report.getId(),userId);
    lineItemService.saveColdChainLIneItems(dbVersion, report.getColdChainLineItems(), report.getId(), userId);
    lineItemService.saveVitaminLineItems(dbVersion, report.getVitaminSupplementationLineItems(), report.getId(), userId);
    lineItemService.saveAdverseEffectLineItems(dbVersion, report.getAdverseEffectLineItems(), report.getId(), userId);
    lineItemService.saveCampaignLineItems(dbVersion, report.getCampaignLineItems(), report.getId(), userId);
  }

  public void update(VaccineReport fromDb, VaccineReport report, Long userId) {
    fromDb.setModifiedBy(userId);
    fromDb.copyValuesFrom(report);
    mapper.update(fromDb);
    saveDetails(fromDb, report, userId);
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

  public VaccineReport getDraftReport(Long facilityId, Long programId) {
    return mapper.getDraftLastReport(facilityId, programId);
  }

  public void changeStatus(VaccineReport report, ReportStatus status, Long userId) {
    report.setModifiedBy(userId);
    report.setStatus(status);
    mapper.setStatus(report);

    ReportStatusChange change = new ReportStatusChange(report, status, userId);
    statusChangeMapper.insert(change);
  }
}
