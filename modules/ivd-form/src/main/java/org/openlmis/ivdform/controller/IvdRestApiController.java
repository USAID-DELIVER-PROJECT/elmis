package org.openlmis.ivdform.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api("IVD Rest APIs")
public class IvdRestApiController extends BaseController {

  @Autowired
  IvdFormService service;

  @RequestMapping(value = "stock-status", method = RequestMethod.GET)
  @ApiOperation(position = 0, value = "Get Facility Stock Status By Product")
  public ResponseEntity<OpenLmisResponse> getStockStatusForProductInFacility(@RequestParam("facilityCode") String facilityCode, @RequestParam("productCode") String productCode, @RequestParam("programCode") String programCode, @RequestParam("periodId") Long periodId) {
    return OpenLmisResponse.response("status", service.getStockStatusForProductInFacility(facilityCode, productCode, programCode, periodId));
  }

  @ApiOperation(position = 1, value = "Get Full Facility Stock Status")
  @RequestMapping(value = "facility-stock-status", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getStockStatusForAllProductInFacility(@RequestParam("facilityCode") String facilityCode, @RequestParam("programCode") String programCode, @RequestParam("periodId") Long periodId) {
    return OpenLmisResponse.response("status", service.getStockStatusForAllProductsInFacility(facilityCode, programCode, periodId));
  }
}
