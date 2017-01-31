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

package org.openlmis.ivdform.view.xml;

import org.exolab.castor.types.DateTime;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.User;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.openlmis.ivdform.domain.DiscardingReason;
import org.openlmis.ivdform.domain.Manufacturer;
import org.openlmis.ivdform.domain.reports.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component("ivdFormInputTool")
public class IvdFormView extends AbstractView {

  private static final String PERIODS = "periods";
  private static final String DISTRICTS = "districts";
  private static final String FACILITIES = "facilities";
  private static Logger logger = LoggerFactory.getLogger(IvdFormView.class);

  Document doc;

  public IvdFormView(){
    setContentType("application/octet-stream");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // read template
    doc = readTemplate();
    Element rootNode = doc.getDocumentElement();
    Node data = rootNode.getElementsByTagName("xfa:data").item(0);

    Element report = doc.createElement("ivdReport");

    writeDetails(report, (Long)model.get("program_id"), model.get("url").toString(), model.get("year").toString(), (User) model.get("user") );
    VaccineReport vaccineReportTemplate = (VaccineReport) model.get("reportTemplate");
    writePeriods(report, (List<ProcessingPeriod>) model.get(PERIODS));
    writeRegions(report, (List<GeographicZone>) model.get(DISTRICTS));
    writeFacilities(report, (List<Facility>) model.get(FACILITIES));
    writeProducts(report, vaccineReportTemplate);
    writeStockStatus(report, vaccineReportTemplate);
    writeVaccinations(report, vaccineReportTemplate);
    writeDiseases(report, vaccineReportTemplate);
    writeSupplements(report, vaccineReportTemplate);
    writeManufacturers(report, (List< Manufacturer>) model.get("manufacturers") );
    writeAdjustmentReasons(report, (List<DiscardingReason>) model.get("adjustment_reasons") );
    writeOperationalStatuses(report, (List<EquipmentOperationalStatus>) model.get("operational_status") );
    //writeFacilityDetails(report, (List<VaccineReport>)model.get("facility_details"));
    writeDemographicEstimates(report, (List<VaccineReport>)model.get("facility_details"));
    writeEquipments(report, (List<VaccineReport>)model.get("facility_details"));

    data.appendChild(report);

    response.setHeader("Content-Transfer-Encoding", "binary");
    response.setHeader("Content-Disposition","attachment; filename=\"" + "ivd-reporting-form.xdp\"");
    writeResponse(response, doc);
  }

  private void createElement(Element parent, String name, String value){
    Element childElement = doc.createElement(name);
    childElement.setTextContent(value);
    parent.appendChild(childElement);
  }

  private void writeResponse(HttpServletResponse response, Document doc) throws IOException, TransformerException {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "no");

    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(response.getWriter());
    try {
      transformer.transform(source, result);
    }catch (javax.xml.transform.TransformerException exp){
      logger.warn("Transformer Warning: ", exp);
    }
  }

  private Document readTemplate() throws IOException, ParserConfigurationException, SAXException {
    Resource resource = new ClassPathResource("form_template.xml");
    InputStream stream = resource.getInputStream();
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    return dBuilder.parse(stream);
  }

  private void writeDetails(Element report, Long programId, String url, String year, User user){

    Element detailsElement = doc.createElement("details");

    createElement(detailsElement, "programId", programId.toString());
    createElement(detailsElement, "baseUrl", url);
    createElement(detailsElement, "url", String.format("%s/rest-api/ivd-from/pdf-submit", url));
    createElement(detailsElement, "generatedDate",  new DateTime().toDate().toString());
    createElement(detailsElement, "year", year);
    createElement(detailsElement, "username", user.getUserName());
    createElement(detailsElement, "userFirstName", user.getFirstName());
    createElement(detailsElement, "userLastName", user.getLastName());
    report.appendChild(detailsElement);
  }

  private void writePeriods(Element report, List<ProcessingPeriod> periods){

    Element periodsElement = doc.createElement("periods");
    Collections.sort(periods, Comparator.comparing(ProcessingPeriod::getStartDate));
    for(ProcessingPeriod period: periods){
      Element processingPeriodElement  = doc.createElement("period");

      createElement(processingPeriodElement, "periodId", period.getId().toString());
      createElement(processingPeriodElement, "periodName", period.getName());

      periodsElement.appendChild(processingPeriodElement);
    }
    report.appendChild(periodsElement);
  }

  private void writeRegions(Element report, List<GeographicZone> zones){

    Element periodsElement = doc.createElement("regions");
    for(GeographicZone zone: zones){
      Element regionElement  = doc.createElement("region");
      createElement(regionElement, "regionId", zone.getId().toString() );
      createElement(regionElement, "regionName", zone.getName() );

      periodsElement.appendChild(regionElement);
    }
    report.appendChild(periodsElement);
  }

  private void writeFacilities(Element report, List<Facility> facilities){

    Element periodsElement = doc.createElement("facilities");
    for(Facility facility: facilities){
      Element facilityElement  = doc.createElement("facility");

      createElement(facilityElement, "regionId", facility.getGeographicZone().getId().toString() );
      createElement(facilityElement, "facilityId", facility.getId().toString() );
      createElement(facilityElement, "facilityName", facility.getName() );

      periodsElement.appendChild(facilityElement);
    }
    report.appendChild(periodsElement);
  }

  private void writeEquipments(Element report, List<VaccineReport> reports){

    Element equipmentRoot = doc.createElement("equipments");
    for(VaccineReport reportT: reports){

      for(ColdChainLineItem li : reportT.getColdChainLineItems()){
        Element equipment = doc.createElement("equipment");
        createElement(equipment, "facilityId", reportT.getFacility().getId().toString());
        createElement(equipment, "equipmentInventoryId", li.getEquipmentInventoryId().toString());
        createElement(equipment, "energySource", li.getEnergySource());
        createElement(equipment, "equipmentName", li.getEquipmentName());
        createElement(equipment, "model", li.getModel());
        createElement(equipment, "serial", (li.getSerial() == null) ? "-": li.getSerial());

        equipmentRoot.appendChild(equipment);
      }
    }
    report.appendChild(equipmentRoot);
  }

  private void writeDemographicEstimates(Element report, List<VaccineReport> reports){

    Element demographicEstimatesRoot = doc.createElement("demographics");
    for(VaccineReport reportT: reports){
      for(AnnualFacilityEstimateEntry entry: reportT.getFacilityDemographicEstimates()){
        Element estimate = doc.createElement("estimate");
        createElement(estimate, "facilityId", reportT.getFacility().getId().toString() );
        createElement(estimate, "estimateName", entry.getCategory().getName());
        createElement(estimate, "estimateValue", entry.getValue().toString());
        demographicEstimatesRoot.appendChild(estimate);
      }
    }
    report.appendChild(demographicEstimatesRoot);
  }

  private void writeFacilityDetails(Element report, List<VaccineReport> reports){

    Element periodsElement = doc.createElement("facilityDetails");
    for(VaccineReport reportT: reports){
      Element facilityElement  = doc.createElement("facility");

      createElement(facilityElement, "facilityId", reportT.getFacility().getId().toString() );
      createElement(facilityElement, "facilityName", reportT.getFacility().getName());

      Element demographics = doc.createElement("demographics");
      facilityElement.appendChild(demographics);
      for(AnnualFacilityEstimateEntry entry: reportT.getFacilityDemographicEstimates()){
        Element estimate = doc.createElement("estimate");
        createElement(estimate, "id", entry.getDemographicEstimateId().toString());
        createElement(estimate, "name", entry.getCategory().getName());
        createElement(estimate, "value", entry.getValue().toString());
        demographics.appendChild(estimate);
      }
      Element equipments = doc.createElement("equipments");
      facilityElement.appendChild(equipments);
      for(ColdChainLineItem li : reportT.getColdChainLineItems()){
        Element equipment = doc.createElement("equipment");

        createElement(equipment, "equipmentInventoryId", li.getEquipmentInventoryId().toString());
        createElement(equipment, "energySource", li.getEnergySource());
        createElement(equipment, "equipmentName", li.getEquipmentName());
        createElement(equipment, "model", li.getModel());
        createElement(equipment, "serial", li.getSerial());

        equipments.appendChild(equipment);
      }
      facilityElement.appendChild(equipments);
      periodsElement.appendChild(facilityElement);
    }
    report.appendChild(periodsElement);
  }



  private void writeProducts(Element report, VaccineReport template) {
    Element productsElement = doc.createElement("products");
    for (LogisticsLineItem li : template.getLogisticsLineItems()) {
      Element productElement = doc.createElement("product");
      createElement(productElement, "productId", li.getProductId().toString());
      createElement(productElement, "productName", li.getProductName());
      productsElement.appendChild(productElement);
    }
    report.appendChild(productsElement);
  }

  private void writeVaccinations(Element report, VaccineReport template) {
    Element productsElement = doc.createElement("vaccinations");
    Integer displayOrder  = 1;
    for (VaccineCoverageItem li : template.getCoverageLineItems()) {
      Element productElement = doc.createElement("vaccine");

      createElement(productElement, "productId", li.getProductId().toString());
      createElement(productElement, "productName", li.getProductName());
      createElement(productElement, "doseId", li.getDoseId().toString());
      createElement(productElement, "doseName", li.getDisplayName());
      createElement(productElement, "displayOrder", displayOrder.toString());
      createElement(productElement, "trackMale", li.getTrackMale().toString());
      createElement(productElement, "trackFemale", li.getTrackFemale().toString());
      displayOrder ++;
      productsElement.appendChild(productElement);
    }
    report.appendChild(productsElement);
  }

  private void writeStockStatus(Element report, VaccineReport template) {
    Element stockStatus = doc.createElement("stockStatus");

    Element productsElement = doc.createElement("products");
    for (LogisticsLineItem li : template.getLogisticsLineItems()) {
      Element productElement = doc.createElement("product");

      createElement(productElement, "productId", li.getProductId().toString());
      createElement(productElement, "productName", li.getProductName());
      createElement(productElement, "productCode", li.getProductCode());
      createElement(productElement, "productCategory", li.getProductCategory());
      createElement(productElement, "displayOrder", li.getDisplayOrder().toString());
      createElement(productElement, "unit", li.getDosageUnit());

      productsElement.appendChild(productElement);
    }
    stockStatus.appendChild(productsElement);
    report.appendChild(stockStatus);
  }

  private void writeDiseases(Element report, VaccineReport template) {
    Element diseasesElement = doc.createElement("diseases");

    for (DiseaseLineItem li : template.getDiseaseLineItems()) {
      Element diseaseElement = doc.createElement("disease");
      createElement(diseaseElement, "diseaseId", li.getDiseaseId().toString());
      createElement(diseaseElement, "diseaseName", li.getDiseaseName());
      createElement(diseaseElement, "displayOrder", li.getDisplayOrder().toString());

      diseasesElement.appendChild(diseaseElement);
    }
    report.appendChild(diseasesElement);
  }

  private void writeSupplements(Element report, VaccineReport template) {
    Element supplementsElement = doc.createElement("supplements");
    for (VitaminSupplementationLineItem li : template.getVitaminSupplementationLineItems()) {
      Element supplementElement = doc.createElement("supplement");
      createElement(supplementElement, "vitaminId", li.getVaccineVitaminId().toString());
      createElement(supplementElement, "vitaminName", li.getVitaminName());
      createElement(supplementElement, "ageGroupId", li.getVitaminAgeGroupId().toString());
      createElement(supplementElement, "ageGroupName", li.getAgeGroup());
      createElement(supplementElement, "displayOrder", li.getDisplayOrder().toString());
      supplementsElement.appendChild(supplementElement);
    }
    report.appendChild(supplementsElement);
  }

  private void writeManufacturers(Element report, List<Manufacturer> manufacturers) {
    Element manufacturersElement = doc.createElement("manufacturers");
    for (Manufacturer manufacturer : manufacturers) {
      Element manufacturerElement = doc.createElement("manufacturer");
      createElement(manufacturerElement, "manufacturerId", manufacturer.getId().toString());
      createElement(manufacturerElement, "manufacturerName", manufacturer.getName());
      manufacturersElement.appendChild(manufacturerElement);
    }
    report.appendChild(manufacturersElement);
  }

  private void writeAdjustmentReasons(Element report, List<DiscardingReason> reasons) {
    Element reasonsElement = doc.createElement("adjustmentReasons");
    for (DiscardingReason reason : reasons) {
      Element reasonElement = doc.createElement("reason");
      createElement(reasonElement, "reasonId", reason.getId().toString());
      createElement(reasonElement, "reasonName", reason.getName());
      reasonsElement.appendChild(reasonElement);
    }
    report.appendChild(reasonsElement);
  }

  private void writeOperationalStatuses(Element report, List<EquipmentOperationalStatus> equipmentOperationalStatuses) {
    Element reasonsElement = doc.createElement("coldChainOperationalStatuses");
    for (EquipmentOperationalStatus status : equipmentOperationalStatuses) {
      Element statusElement = doc.createElement("status");
      createElement(statusElement, "statusId", status.getId().toString());
      createElement(statusElement, "statusName", status.getName());
      reasonsElement.appendChild(statusElement);
    }
    report.appendChild(reasonsElement);
  }

}
