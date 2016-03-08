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

package org.openlmis.ivdform.service;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.service.*;
import org.openlmis.demographics.service.AnnualFacilityDemographicEstimateService;
import org.openlmis.ivdform.domain.VaccineDisease;
import org.openlmis.ivdform.domain.VaccineProductDose;
import org.openlmis.ivdform.domain.Vitamin;
import org.openlmis.ivdform.domain.VitaminSupplementationAgeGroup;
import org.openlmis.ivdform.domain.reports.*;
import org.openlmis.ivdform.dto.FacilityIvdSummary;
import org.openlmis.ivdform.dto.ReportStatusDTO;
import org.openlmis.ivdform.dto.RoutineReportDTO;
import org.openlmis.ivdform.dto.StockStatusSummary;
import org.openlmis.ivdform.repository.VitaminRepository;
import org.openlmis.ivdform.repository.VitaminSupplementationAgeGroupRepository;
import org.openlmis.ivdform.repository.reports.ColdChainLineItemRepository;
import org.openlmis.ivdform.repository.reports.IvdFormRepository;
import org.openlmis.ivdform.repository.reports.LogisticsLineItemRepository;
import org.openlmis.ivdform.repository.reports.StatusChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;


@Service
@NoArgsConstructor
public class IvdFormService {

  private static final String STOCK_STATUS_FOUND = "STOCK_STATUS_FOUND";
  private static final String STOCK_STATUS_NOT_FOUND = "STOCK_STATUS_NOT_FOUND";
  @Autowired
  IvdFormRepository repository;

  @Autowired
  ProgramProductService programProductService;

  @Autowired
  DiseaseService diseaseService;

  @Autowired
  ProcessingPeriodRepository periodService;

  @Autowired
  ProductDoseService productDoseService;

  @Autowired
  ColdChainLineItemRepository coldChainRepository;

  @Autowired
  VitaminRepository vitaminRepository;

  @Autowired
  VitaminSupplementationAgeGroupRepository ageGroupRepository;

  @Autowired
  ProgramService programService;

  @Autowired
  TabVisibilityService tabVisibilityService;

  @Autowired
  StatusChangeRepository reportStatusChangeRepository;

  @Autowired
  AnnualFacilityDemographicEstimateService annualFacilityDemographicEstimateService;

  @Autowired
  LogisticsLineItemRepository logisticsLineItemRepository;

  @Autowired
  MessageService messageService;

  @Autowired
  FacilityService facilityService;

  @Autowired
  CommaSeparator commaSeparator;

  @Autowired
  ConfigurationSettingService configurationSettingService;

  @Autowired
  GeographicZoneService geographicZoneService;

  @Autowired
  IVDNotificationService ivdNotificationService;

  private static final String DATE_FORMAT = "yyyy-MM-dd";

  @Transactional
  public VaccineReport initialize(Long facilityId, Long programId, Long periodId, Long userId) {
    VaccineReport report = repository.getByProgramPeriod(facilityId, programId, periodId);
    if (report != null) {
      return report;
    }
    report = createNewVaccineReport(facilityId, programId, periodId);
    repository.insert(report);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.DRAFT, userId);
    reportStatusChangeRepository.insert(change);
    return report;
  }

  @Transactional
  public void save(VaccineReport report, Long userId) {
    repository.update(report, userId);
  }

  @Transactional
  public void submit(VaccineReport report, Long userId) {
    report.setStatus(ReportStatus.SUBMITTED);
    repository.update(report, userId);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.SUBMITTED, userId);
    reportStatusChangeRepository.insert(change);
    ivdNotificationService.sendIVDStatusChangeNotification(report, userId);
  }

  private VaccineReport createNewVaccineReport(Long facilityId, Long programId, Long periodId) {
    VaccineReport report;
    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
    List<VaccineDisease> diseases = diseaseService.getAll();
    List<VaccineProductDose> dosesToCover = productDoseService.getForProgram(programId);
    List<ColdChainLineItem> coldChainLineItems = coldChainRepository.getNewEquipmentLineItems(programId, facilityId);
    List<Vitamin> vitamins = vitaminRepository.getAll();
    List<VitaminSupplementationAgeGroup> ageGroups = ageGroupRepository.getAll();

    VaccineReport previousReport = this.getPreviousReport(facilityId, programId, periodId);

    report = new VaccineReport();
    report.setFacilityId(facilityId);
    report.setProgramId(programId);
    report.setPeriodId(periodId);
    report.setStatus(ReportStatus.DRAFT);

    // 1. copy the products list and initiate the logistics tab.
    report.initializeLogisticsLineItems(programProducts, previousReport);

    // 2. copy the product + dosage settings and initiate the coverage tab.
    report.initializeCoverageLineItems(dosesToCover);

    // 3. copy the disease list and initiate the disease tab.
    report.initializeDiseaseLineItems(diseases);

    // 4. initialize the cold chain line items.
    report.initializeColdChainLineItems(coldChainLineItems);

    report.initializeVitaminLineItems(vitamins, ageGroups);
    return report;
  }

  private VaccineReport getPreviousReport(Long facilityId, Long programId, Long periodId) {
    Long reportId = repository.findLastReportBeforePeriod(facilityId, programId, periodId);
    return repository.getByIdWithFullDetails(reportId);
  }

  public List<ReportStatusDTO> getReportedPeriodsFor(Long facilityId, Long programId) {
    return repository.getReportedPeriodsForFacility(facilityId, programId);
  }

  public List<ReportStatusDTO> getPeriodsFor(Long facilityId, Long programId, Date endDate) {
    Date startDate = programService.getProgramStartDate(facilityId, programId);

    // find out which schedule this facility is in?
    Long scheduleId = repository.getScheduleFor(facilityId, programId);
    VaccineReport lastRequest = repository.getLastReport(facilityId, programId);

    if (lastRequest != null) {
      lastRequest.setPeriod(periodService.getById(lastRequest.getPeriodId()));
      startDate = lastRequest.getPeriod().getStartDate();
    }

    List<ReportStatusDTO> results = new ArrayList<>();
    List<ProcessingPeriod> periods = periodService.getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    if (lastRequest != null) {

      List<VaccineReport> rejectedReports = repository.getRejectedReports(facilityId, programId);
      for (VaccineReport rReport : rejectedReports) {
        results.add(createReportStatusDto(facilityId, programId, rReport));
      }

      if (lastRequest.getStatus().equals(ReportStatus.DRAFT)) {
        results.add(createReportStatusDto(facilityId, programId, lastRequest));
      }
    }


    for (ProcessingPeriod period : emptyIfNull(periods)) {
      if (lastRequest == null || !lastRequest.getPeriodId().equals(period.getId())) {
        ReportStatusDTO reportStatusDTO = new ReportStatusDTO();

        reportStatusDTO.setPeriodName(period.getName());
        reportStatusDTO.setPeriodId(period.getId());
        reportStatusDTO.setProgramId(programId);
        reportStatusDTO.setFacilityId(facilityId);

        results.add(reportStatusDTO);
      }
    }
    return results;
  }

  private static ReportStatusDTO createReportStatusDto(Long facilityId, Long programId, VaccineReport report) {
    ReportStatusDTO reportStatusDTO = new ReportStatusDTO();
    reportStatusDTO.setPeriodName(report.getPeriod().getName());
    reportStatusDTO.setPeriodId(report.getPeriod().getId());
    reportStatusDTO.setStatus(report.getStatus().toString());
    reportStatusDTO.setProgramId(programId);
    reportStatusDTO.setFacilityId(facilityId);
    reportStatusDTO.setId(report.getId());
    return reportStatusDTO;
  }

  public VaccineReport getById(Long id) {
    VaccineReport report = repository.getByIdWithFullDetails(id);
    report.setTabVisibilitySettings(tabVisibilityService.getVisibilityForProgram(report.getProgramId()));
    DateTime periodStartDate = new DateTime(report.getPeriod().getStartDate());
    report.setFacilityDemographicEstimates(annualFacilityDemographicEstimateService.getEstimateValuesForFacility(report.getFacilityId(), report.getProgramId(), periodStartDate.getYear()));
    return report;
  }

  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId) {
    return repository.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }

  public List<RoutineReportDTO> getApprovalPendingForms(Long userId, Long programId) {
    String facilityIds = commaSeparator.commaSeparateIds(facilityService.getUserSupervisedFacilities(userId, programId, RightName.APPROVE_IVD));
    return repository.getApprovalPendingForms(facilityIds);
  }

  public void approve(VaccineReport report, Long userId) {
    report.setStatus(ReportStatus.APPROVED);
    Long reportSubmitterUserId = getReportSubmitterUserId(report.getId());
    repository.update(report, userId);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.APPROVED, userId);
    reportStatusChangeRepository.insert(change);
    ivdNotificationService.sendIVDStatusChangeNotification(report, reportSubmitterUserId);
  }

  public void reject(VaccineReport report, Long userId) {
    report.setStatus(ReportStatus.REJECTED);
    Long reportSubmitterUserId = getReportSubmitterUserId(report.getId());
    repository.update(report, userId);
    ReportStatusChange change = new ReportStatusChange(report, ReportStatus.REJECTED, userId);
    reportStatusChangeRepository.insert(change);
    ivdNotificationService.sendIVDStatusChangeNotification(report, reportSubmitterUserId);
  }

  public FacilityIvdSummary getStockStatusForAllProductsInFacility(String facilityCode, String programCode, Long periodId) {
    FacilityIvdSummary summary = new FacilityIvdSummary(facilityCode, programCode, periodId);
    List<LogisticsLineItem> list = logisticsLineItemRepository.getApprovedLineItemListFor(programCode, facilityCode, periodId);
    if (!emptyIfNull(list).isEmpty()) {
      Facility facility = facilityService.getFacilityByCode(facilityCode);

      Long reportId = this.getReportIdForFacilityAndPeriod(facility.getId(), periodId);
      VaccineReport report = repository.getByIdWithFullDetails(reportId);
      summary.setEquipments(report.getColdChainLineItems());

      summary.setStatus(STOCK_STATUS_FOUND);
      summary.setProducts(new ArrayList<StockStatusSummary>());
      for (LogisticsLineItem item : list) {
        summary.getProducts().add(populateStockStatusSummary(facilityCode, item.getProductCode(), programCode, periodId, item));
      }
    } else {
      summary.setStatus(STOCK_STATUS_NOT_FOUND);
    }
    return summary;
  }

  public StockStatusSummary getStockStatusForProductInFacility(String facilityCode, String productCode, String programCode, Long periodId) {
    LogisticsLineItem periodicLLI = logisticsLineItemRepository.getApprovedLineItemFor(programCode, productCode, facilityCode, periodId);
    return populateStockStatusSummary(facilityCode, productCode, programCode, periodId, periodicLLI);
  }

  private StockStatusSummary populateStockStatusSummary(String facilityCode, String productCode, String programCode, Long periodId, LogisticsLineItem periodicLLI) {
    StockStatusSummary response = new StockStatusSummary();
    response.setPeriodId(periodId);
    response.setProductCode(productCode);
    if (periodicLLI != null) {
      response.setDaysOutOfStock(periodicLLI.getDaysStockedOut());
      response.setStockStatus(periodicLLI.getClosingBalance());
      response.setProductId(periodicLLI.getProductId());
      response.setStatus(STOCK_STATUS_FOUND);

      List<LogisticsLineItem> previousThreeSubmissions = logisticsLineItemRepository.getUpTo3PreviousPeriodLineItemsFor(programCode, productCode, facilityCode, periodId);
      response.setAmc(calculateAMC(previousThreeSubmissions));
    } else {
      response.setStatus(STOCK_STATUS_NOT_FOUND);
    }
    return response;
  }

    private Long getReportSubmitterUserId(Long vaccineReportId){
        VaccineReport previousReport =  repository.getById(vaccineReportId);
        return previousReport != null ? previousReport.getModifiedBy() : null;
    }

  private static Long calculateAMC(List<LogisticsLineItem> previousThree) {
    Long sum = 0L;
    int count = 0;
    for (LogisticsLineItem lineItem : emptyIfNull(previousThree)) {
      if (lineItem.getQuantityIssued() != null) {
        sum += lineItem.getQuantityIssued();
        count++;
      }
    }
    return (count == 0) ? 0L : sum / count;
  }
}
