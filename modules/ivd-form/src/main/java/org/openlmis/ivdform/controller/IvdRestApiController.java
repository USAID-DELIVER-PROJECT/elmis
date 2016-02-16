package org.openlmis.ivdform.controller;


import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.ivdform.service.IvdFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/rest-api/ivd/")
public class IvdRestApiController extends BaseController {

  @Autowired
  IvdFormService service;

  @RequestMapping(value = "stock-status", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getStockStatusForProductInFacility(@RequestParam("facilityCode") String facilityCode, @RequestParam("productCode") String productCode, @RequestParam("programCode") String programCode, @RequestParam("periodId") Long periodId) {
    return OpenLmisResponse.response("status", service.getStockStatusForProductInFacility(facilityCode, productCode, programCode, periodId));
  }

  @RequestMapping(value = "facility-stock-status", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getStockStatusForAllProductInFacility(@RequestParam("facilityCode") String facilityCode, @RequestParam("programCode") String programCode, @RequestParam("periodId") Long periodId) {
    return OpenLmisResponse.response("status", service.getStockStatusForAllProductsInFacility(facilityCode, programCode, periodId));
  }
}
