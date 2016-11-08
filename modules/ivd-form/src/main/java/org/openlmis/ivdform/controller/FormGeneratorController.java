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

package org.openlmis.ivdform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.demographics.service.AnnualFacilityDemographicEstimateService;
import org.openlmis.equipment.service.EquipmentOperationalStatusService;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.exceptions.OutOfOrderFormSubmissionException;
import org.openlmis.ivdform.repository.reports.ColdChainLineItemRepository;
import org.openlmis.ivdform.service.DiscardingReasonsService;
import org.openlmis.ivdform.service.IvdFormService;
import org.openlmis.ivdform.service.ManufacturerService;
import org.openlmis.ivdform.view.pdf.SubmissionResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FormGeneratorController extends BaseController {

  private static final String DISTRICTS = "districts";
  private static final String FACILITIES = "facilities";
  private static final String PERIODS = "periods";
  private static final String REPORT_TEMPLATE = "reportTemplate";
  private static final String MANUFACTURERS = "manufacturers";
  private static final String ADJUSTMENT_REASONS = "adjustment_reasons";
  private static final String OPERATIONAL_STATUS = "operational_status";
  private static final String FACILITY_DETAILS = "facility_details";
  private static final String STATUS = "STATUS";
  public static final String USER = "user";
  public static final String YEAR = "year";

  @Autowired
  private IvdFormService service;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private GeographicZoneService geographicZoneService;

  @Autowired
  private ProcessingScheduleService scheduleService;

  @Autowired
  private ManufacturerService manufacturerService;

  @Autowired
  private DiscardingReasonsService reasonsService;

  @Autowired
  private AnnualFacilityDemographicEstimateService demographicEstimateService;

  @Autowired
  private EquipmentOperationalStatusService equipmentOperationalStatusService;

  @Autowired
  ColdChainLineItemRepository coldChainRepository;

  @Autowired
  UserService userService;

  @Value("${mail.base.url}")
  public String baseUrl;

  @RequestMapping(value = {"/rest-api/ivd-from/pdf-submit"}, method = {RequestMethod.POST}, consumes = MediaType.ALL_VALUE)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
  public ModelAndView saveFromPDF(HttpServletRequest request) throws ParserConfigurationException, IOException, SAXException {
    ModelAndView modelAndView = new ModelAndView("ivdFormResponseView");
    SubmissionResponseModel model;
    try {
      VaccineReport report = parsePDFSubmission(request);
      service.submitFromOtherApplications(report, loggedInUserId(request));
      model = new SubmissionResponseModel(messageService.message("ivd.form.successfully.submitted"), false);
    } catch (OutOfOrderFormSubmissionException exp) {
      model = new SubmissionResponseModel(messageService.message(exp.getMessage()) + " Expected period was " + exp.getExpected() + " but submission was for " + exp.getFound(), true);
    } catch (Exception exp) {
      model = new SubmissionResponseModel(messageService.message(exp.getMessage()), true);
    }
    modelAndView.addObject(STATUS, model);
    return modelAndView;
  }

  private VaccineReport parsePDFSubmission(HttpServletRequest request) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(request.getInputStream());
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(doc.getDocumentElement().getTextContent().toString(), VaccineReport.class);
  }


  @RequestMapping(value = {"/ivd-form/{programId}/{year}/download-reporting-form.xdp"}, method = RequestMethod.GET)
  public ModelAndView downloadReportingForm(@PathVariable(value = "programId") Long programId, @PathVariable(value = "year") Long year, HttpServletRequest request) {

    ModelAndView modelAndView = new ModelAndView("ivdFormInputTool");
    modelAndView.addObject("program_id", programId);
    modelAndView.addObject("url", baseUrl);
    //TODO clean out this hardcoded schedule. how? i don't know.
    modelAndView.addObject(PERIODS, scheduleService.getAllPeriodsForScheduleAndYear(45L, year));
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(loggedInUserId(request), programId, RightName.CREATE_IVD);
    modelAndView.addObject(FACILITIES, facilities);
    modelAndView.addObject(DISTRICTS, geographicZoneService.getDistrictsFor(facilities));
    VaccineReport templateReport = service.createNewVaccineReport(null, programId, null);
    modelAndView.addObject(REPORT_TEMPLATE, templateReport);
    modelAndView.addObject(MANUFACTURERS, manufacturerService.getAll() );
    modelAndView.addObject(ADJUSTMENT_REASONS, reasonsService.getAllReasons());
    modelAndView.addObject(OPERATIONAL_STATUS, equipmentOperationalStatusService.getAll());
    modelAndView.addObject(YEAR, year);
    modelAndView.addObject(USER, userService.getById( loggedInUserId(request)));

    List<VaccineReport> reports = new ArrayList<>();
    for(Facility facility: facilities){
      VaccineReport report = new VaccineReport();
      report.setFacilityDemographicEstimates(demographicEstimateService.getEstimateValuesForFacility(facility.getId(), programId, 2016));
      report.setFacility(facility);
      report.setColdChainLineItems(coldChainRepository.getNewEquipmentLineItems(programId, facility.getId()));
      reports.add(report);
    }
    modelAndView.addObject(FACILITY_DETAILS, reports);
    return modelAndView;
  }
}
