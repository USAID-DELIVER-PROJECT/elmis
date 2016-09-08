/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine.inventory;


import org.openlmis.core.domain.Pagination;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.service.inventory.VaccineInventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static java.lang.Integer.parseInt;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(value = "/vaccine/inventory/report")
public class VaccineInventoryReportController extends BaseController {

    @Autowired
    VaccineInventoryReportService service;

    @RequestMapping(value = "/distributionCompleteness", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> completenessAndTimeliness(@RequestParam(value = "periodStart", required = false) String periodStart,
                                                                      @RequestParam(value = "periodEnd", required = false) String periodEnd,
                                                                      @RequestParam("district") Long districtId,
                                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                      @Value("${search.page.size}") String limit) {

        Pagination pagination = new Pagination(page, parseInt(limit));
        OpenLmisResponse openLdrResponse = new OpenLmisResponse("distributionCompleteness", service.getDistributionCompletenessReport(periodStart, periodEnd, districtId, pagination));
        pagination.setTotalRecords(service.getTotalDistributionCompletenessReport(periodStart, periodEnd, districtId));
        openLdrResponse.addData("pagination", pagination);
        return openLdrResponse.response(OK);
//
    }

    @RequestMapping(value = "/getDistributedFacilities", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getDistributedFacilities(@RequestParam("periodId") Long periodId,
                                                                      @RequestParam("facilityId") Long facilityId,
                                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                      @Value("${search.page.size}") String limit) {

        Pagination pagination = new Pagination(page, parseInt(limit));
        OpenLmisResponse openLdrResponse = new OpenLmisResponse("distributedFacilities", service.getDistributedFacilities(periodId,facilityId, pagination));
        pagination.setTotalRecords(service.getTotalDistributedFacilities(periodId,facilityId));
        openLdrResponse.addData("pagination", pagination);
        return openLdrResponse.response(OK);
//
    }




}
