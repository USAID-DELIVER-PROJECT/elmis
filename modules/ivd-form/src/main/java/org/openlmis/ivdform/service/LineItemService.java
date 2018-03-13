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

import org.openlmis.ivdform.domain.reports.*;
import org.openlmis.ivdform.repository.reports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;


@Service
public class LineItemService {

  @Autowired
  LogisticsLineItemRepository repository;

  @Autowired
  DiseaseLineItemRepository diseaseLineItemRepository;

  @Autowired
  CoverageItemRepository coverageItemRepository;

  @Autowired
  AdverseEffectLineItemRepository adverseLineItemRepository;

  @Autowired
  CampaignLineItemRepository campaignLineItemRepository;

  @Autowired
  ColdChainLineItemRepository coldChainLIRepository;

  @Autowired
  VitaminSupplementationLineItemRepository vitaminSupplementationLineItemRepository;

  public void saveLogisticsLineItems(VaccineReport dbVersion, List<LogisticsLineItem> lineItems, Long reportId, Long userId) {
    for (LogisticsLineItem lineItem : emptyIfNull(lineItems)) {
      if (null != dbVersion) {
        Optional<LogisticsLineItem> dbLogisticsLineItem = dbVersion.getLogisticsLineItems()
            .parallelStream()
            .filter(t -> t.getProductId().equals(lineItem.getProductId()))
            .findFirst();

        if (dbLogisticsLineItem.isPresent()) {
          dbLogisticsLineItem.get().copyValuesFrom(lineItem);
          dbLogisticsLineItem.get().setModifiedBy(userId);
          repository.update(dbLogisticsLineItem.get());
          continue;
        }
      }
      if (!lineItem.hasId()) {
        lineItem.setReportId(reportId);
        repository.insert(lineItem);
      }
    }
  }

  public void saveDiseaseLineItems(VaccineReport dbVersion, List<DiseaseLineItem> lineItems, Long reportId) {
    for (DiseaseLineItem lineItem : emptyIfNull(lineItems)) {
      if (null != dbVersion) {
        Optional<DiseaseLineItem> dbLineItems = dbVersion.getDiseaseLineItems()
            .parallelStream()
            .filter(t -> t.getDiseaseId() == lineItem.getDiseaseId())
            .findFirst();
        if (dbLineItems.isPresent()) {
          lineItem.setId(dbLineItems.get().getId());
          lineItem.setReportId(dbLineItems.get().getReportId());
          diseaseLineItemRepository.update(lineItem);
          continue;
        }
      }
      if (!lineItem.hasId()) {
        lineItem.setReportId(reportId);
        diseaseLineItemRepository.insert(lineItem);
      }
    }
  }

  public void saveCoverageLineItems(VaccineReport dbVersion, List<VaccineCoverageItem> lineItems, Long reportId, Long userId) {
    for (VaccineCoverageItem lineItem : emptyIfNull(lineItems)) {

      if (null != dbVersion) {
        Optional<VaccineCoverageItem> dbCoverageLineItem = dbVersion.getCoverageLineItems()
            .parallelStream()
            .filter(t -> t.getProductId().equals(lineItem.getProductId()) && t.getDoseId().equals(lineItem.getDoseId()))
            .findFirst();

        if (dbCoverageLineItem.isPresent()) {
          dbCoverageLineItem.get().copyValuesFrom(lineItem);
          dbCoverageLineItem.get().setModifiedBy(userId);
          coverageItemRepository.update(dbCoverageLineItem.get());
          continue;
        }
      }
      if (!lineItem.hasId()) {
        lineItem.setReportId(reportId);
        coverageItemRepository.insert(lineItem);
      }
    }
  }

  //TODO: Refactor this to be used for Both UI and Interfaced system
  public void saveAdverseEffectLineItemsForInterfaceAPI(VaccineReport dbVersion, List<AdverseEffectLineItem> adverseEffectLineItems, Long reportId, Long userId) {

    // delete and recreate the line items here.
    if (reportId == null && dbVersion != null) {
      reportId = dbVersion.getId();
    }
    //Fixed Delete Adverse Effect
    //adverseLineItemRepository.deleteLineItems(reportId);
    for (AdverseEffectLineItem lineItem : emptyIfNull(adverseEffectLineItems)) {
      lineItem.setReportId(reportId);

      if (lineItem.getId() != null) {
        List<AdverseEffectLineItem> lineItems = adverseLineItemRepository.getById(lineItem.getId());
        if (lineItems == null)
          adverseLineItemRepository.insert(lineItem);
        else
          adverseLineItemRepository.update(lineItem);
      } else
        adverseLineItemRepository.insert(lineItem);
      for (AdverseEffectLineItem relatedIssue : emptyIfNull(lineItem.getRelatedLineItems())) {
        relatedIssue.setReportId(reportId);
        relatedIssue.setRelatedToLineItemId(lineItem.getId());
        // copy important details from the main aefi
        relatedIssue.setCases(lineItem.getCases());
        relatedIssue.setDate(lineItem.getDate());
        relatedIssue.setIsInvestigated(lineItem.getIsInvestigated());
        relatedIssue.setInvestigationDate(lineItem.getInvestigationDate());
        relatedIssue.setCreatedBy(userId);
        adverseLineItemRepository.insert(relatedIssue);
      }
    }
  }


  public void saveAdverseEffectLineItems(VaccineReport dbVersion, List<AdverseEffectLineItem> adverseEffectLineItems, Long reportId, Long userId) {

    // delete and recreate the line items here.
    if(reportId == null && dbVersion != null){
      reportId = dbVersion.getId();
    }
    adverseLineItemRepository.deleteLineItems(reportId);

    for (AdverseEffectLineItem lineItem : emptyIfNull(adverseEffectLineItems)) {
      lineItem.setReportId(reportId);
      adverseLineItemRepository.insert(lineItem);
      for (AdverseEffectLineItem relatedIssue : emptyIfNull(lineItem.getRelatedLineItems())) {
        relatedIssue.setReportId(reportId);
        relatedIssue.setRelatedToLineItemId(lineItem.getId());
        // copy important details from the main aefi
        relatedIssue.setCases(lineItem.getCases());
        relatedIssue.setDate(lineItem.getDate());
        relatedIssue.setIsInvestigated(lineItem.getIsInvestigated());
        relatedIssue.setInvestigationDate(lineItem.getInvestigationDate());
        relatedIssue.setCreatedBy(userId);
        adverseLineItemRepository.insert(relatedIssue);
      }
    }
  }

  public void saveCampaignLineItems(VaccineReport dbVersion, List<CampaignLineItem> campaignLineItems, Long reportId, Long userId) {
    campaignLineItemRepository.deleteFor(reportId);
    for (CampaignLineItem lineItem : emptyIfNull(campaignLineItems)) {
      lineItem.setReportId(reportId);
      if (!lineItem.hasId()) {
        lineItem.setCreatedBy(userId);
        campaignLineItemRepository.insert(lineItem);
      } else {
        lineItem.setModifiedBy(userId);
        campaignLineItemRepository.update(lineItem);
      }
    }
  }

  public void saveColdChainLIneItems(VaccineReport dbVersion, List<ColdChainLineItem> coldChainLineItems, Long reportId, Long userId) {

    for (ColdChainLineItem lineItem : emptyIfNull(coldChainLineItems)) {
      if (null != dbVersion) {
        Optional<ColdChainLineItem> dbColdChainLI = dbVersion.getColdChainLineItems()
            .parallelStream()
            .filter(t -> t.getEquipmentInventoryId().equals(lineItem.getEquipmentInventoryId()))
            .findFirst();

        if (dbColdChainLI.isPresent()) {
          dbColdChainLI.get().copyValuesFrom(lineItem);
          dbColdChainLI.get().setModifiedBy(userId);
          coldChainLIRepository.update(dbColdChainLI.get());
          continue;
        }
      }
      lineItem.setReportId(reportId);
      if (!lineItem.hasId() && reportId != null) {
        lineItem.setCreatedBy(userId);
        coldChainLIRepository.insert(lineItem);
      }
    }
  }

  public void saveVitaminLineItems(VaccineReport dbVersion, List<VitaminSupplementationLineItem> vitaminSupplementationLineItems, Long reportId, Long userId) {

    for (VitaminSupplementationLineItem lineItem : emptyIfNull(vitaminSupplementationLineItems)) {
      if (null != dbVersion) {
        Optional<VitaminSupplementationLineItem> dbVitaminSupplementationLI = dbVersion.getVitaminSupplementationLineItems()
            .parallelStream()
            .filter(t -> t.getVitaminAgeGroupId().equals(lineItem.getVitaminAgeGroupId()) && t.getVaccineVitaminId().equals(lineItem.getVaccineVitaminId()))
            .findFirst();

        if (dbVitaminSupplementationLI.isPresent()) {
          dbVitaminSupplementationLI.get().copyValuesFrom(lineItem);
          dbVitaminSupplementationLI.get().setModifiedBy(userId);
          vitaminSupplementationLineItemRepository.update(dbVitaminSupplementationLI.get());
          continue;
        }
      }
      lineItem.setReportId(reportId);
      if (!lineItem.hasId()) {
        lineItem.setCreatedBy(userId);
        vitaminSupplementationLineItemRepository.insert(lineItem);
      }
    }
  }
}
