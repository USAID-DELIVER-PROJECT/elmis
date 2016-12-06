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
package org.openlmis.web.controller.vaccine;

import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.service.PerformanceCoverageDataProvider;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class VaccineReportController extends BaseController {

    public static final String PERIODS = "periods";
    @Autowired
    VaccineReportService service;

    @Autowired
    ProgramService programService;

    @Autowired
    UserService userService;

    @Autowired
    FacilityService facilityService;

@Autowired
PerformanceCoverageDataProvider coverageDataProvider;
    @RequestMapping(value = "vaccine-monthly-report")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_IVD')")
    public ResponseEntity<OpenLmisResponse> getVaccineMonthlyReport(@RequestParam("facility") Long facilityId, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId, HttpServletRequest request) {

        if (periodId == null || periodId == 0) return null;
        Long userId = this.loggedInUserId(request);
        return OpenLmisResponse.response("vaccineData", service.getMonthlyVaccineReport(facilityId, periodId, zoneId, userId));

    }

    @RequestMapping(value = "vaccine-usage-trend")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> vaccineUsageTrend( HttpServletRequest request,@RequestParam("facilityCode") String facilityCode, @RequestParam("productCode") String productCode, @RequestParam("period") Long periodId, @RequestParam("zone") Long zoneId) {
Long userId= this.loggedInUserId(request);
        return OpenLmisResponse.response("vaccineUsageTrend", service.vaccineUsageTrend(facilityCode, productCode, periodId, zoneId, userId));
    }

    @RequestMapping(value = "/orderRequisition/downloadPDF", method = RequestMethod.GET)

    public ModelAndView downloadPDF() {

        List<OrderRequisitionDTO> listOrders = new ArrayList<OrderRequisitionDTO>();

        return new ModelAndView("orderRequisitionPDF", "listOrders", listOrders);
    }

  @RequestMapping(value = "/performanceCoverage", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
  public ResponseEntity<OpenLmisResponse> performanceCoverage(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                              @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                              @RequestParam("district") Long districtId,
                                                              @RequestParam("product") Long product,
                                                              @RequestParam("doseId") Long doseId,  HttpServletRequest request
  ) {
      Long userId = this.loggedInUserId(request);
    return OpenLmisResponse.response("performanceCoverage",
            service.getPerformanceCoverageReportData(periodStart, periodEnd, districtId, product, doseId, userId));
  }

    @RequestMapping(value = "/denominatorName", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> denominatorName(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                                @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                                @RequestParam("district") Long districtId,
                                                                @RequestParam("product") Long product,
                                                                @RequestParam("doseId") Long doseId,  HttpServletRequest request
    ) {
        Long userId = this.loggedInUserId(request);

        String denominatorName=coverageDataProvider.getDenominatorName(periodStart,periodEnd,districtId,product,doseId,userId);

        return OpenLmisResponse.response("denominatorName",
                denominatorName);
    }

    @RequestMapping(value = "/adequaceyLevel", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> adequacyLevelOfSupply(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                                  @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                                  @RequestParam("district") Long districtId,
                                                                  @RequestParam("product") Long product) {


        return OpenLmisResponse.response("adequaceyLevel",
                service.getAdequacyLevelOfSupply(periodStart, periodEnd, districtId, product));
    }

    @RequestMapping(value = "/vaccine_products", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> getVaccineProductsList() {
        return OpenLmisResponse.response("products", this.service.getVaccineProductsList());

    }

    @RequestMapping(value = "/classificationVaccineUtilizationPerformance", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> classificationVaccineUtilizationPerformance(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                                                        @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                                                        @RequestParam("zone") Long zone,
                                                                                        @RequestParam("product") Long product) {


        return OpenLmisResponse.response("classificationVaccineUtilizationPerformance",
                service.getClassificationVaccineUtilizationPerformance(periodStart, periodEnd, zone, product));
    }

    @RequestMapping(value = "/categorizationVaccineUtilizationPerformance", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPORT')")
    public ResponseEntity<OpenLmisResponse> categorizationVaccineUtilizationPerformance(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                                                        @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                                                        @RequestParam("zone") Long zone,
                                                                                        @RequestParam("product") Long product) {


        return OpenLmisResponse.response("categorizationVaccineUtilizationPerformance",
                service.getCategorizationVaccineUtilizationPerformance(periodStart, periodEnd, zone, product));
    }

    @RequestMapping(value = "/staticYearList", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> loadYearList() {
        return OpenLmisResponse.response("years",
                service.loadYearList());
    }
    @RequestMapping(value = "/coverageAndDropoutCoefficient", method = RequestMethod.GET)
      public  ResponseEntity<OpenLmisResponse> getCoverageAndDropoutCoeffients(   @RequestParam("product") Long product){
        return OpenLmisResponse.response("coefficients", service.getCoverageAndDropoutCoeffients(product));
    }
}
