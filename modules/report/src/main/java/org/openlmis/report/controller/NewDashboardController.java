package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@NoArgsConstructor
@RequestMapping(value = "/dashboard")
@Controller
public class NewDashboardController extends BaseController {
private static final String REPORTING_RATE="reportingRate";
    private static final String STOCK_STATUS="stockStatus";
    private static final String ITEM_FILL_RATE="itemFillRate";
    @Autowired
private DashboardService dashboardService;
    @RequestMapping(value = "/reporting-rate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getOrderFillRate(@RequestParam("zoneId") Long zoneId,
                                                             @RequestParam("periodId") Long periodId,
                                                             @RequestParam("programId") Long programId) {
        return OpenLmisResponse.response(REPORTING_RATE, this.dashboardService.getReportingRate(zoneId, periodId, programId));
    }

    @RequestMapping(value = "/stock-staus-availablity", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getStockStatusAvailablity(@RequestParam("zoneId") Long zoneId,
                                                             @RequestParam("periodId") Long periodId,
                                                             @RequestParam("programId") Long programId) {
        return OpenLmisResponse.response(STOCK_STATUS, this.dashboardService.getStockStaus(zoneId, periodId, programId));
    }
    @RequestMapping(value = "/item-fill-rate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getItemFillRate(@RequestParam("zoneId") Long zoneId,
                                                                      @RequestParam("periodId") Long periodId,
                                                                      @RequestParam("programId") Long programId) {
        return OpenLmisResponse.response(ITEM_FILL_RATE, this.dashboardService.getItemFillRate(zoneId, periodId, programId));
    }

}
