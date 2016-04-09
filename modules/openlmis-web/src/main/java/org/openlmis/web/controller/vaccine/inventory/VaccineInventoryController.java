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


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;

import org.openlmis.report.util.Constants;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.service.inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "/vaccine/inventory/")
public class VaccineInventoryController extends BaseController {

    private static final String PROGRAM_PRODUCT_LIST = "programProductList";

    private static final String FACILITY_TYPE_PROGRAM_PRODUCT_LIST = "facilityProduct";

    private static final  String PRINT_DEMAND_FORECASTING = "demand-forecasting";

    @Autowired
    ProgramService programService;
    @Autowired
    ProgramProductService programProductService;

    @Autowired
    VaccineInventoryService service;

    @Autowired
    TemplateService templateService;
    @Autowired
    ConfigurationSettingService settingService;
    @Autowired
    private JasperReportsViewFactory jasperReportsViewFactory;

    @Autowired
    private FacilityService facilityService;

    @RequestMapping(value = "programProducts/programId/{programId}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK , VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = programProductService.getByProgram(new Program(programId));
        return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

    @RequestMapping(value = "programProducts/facilityId/{facilityId}/programId/{programId}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK , VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgramAndType(@PathVariable Long facilityId,@PathVariable Long programId) {
        List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = service.facilityTypeApprovedProduct(facilityId,programId);
        return response(FACILITY_TYPE_PROGRAM_PRODUCT_LIST, facilityTypeApprovedProducts);
    }

    @RequestMapping(value = "programs")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK , VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return OpenLmisResponse.response("programs", programService.getAllIvdPrograms());
    }

    @RequestMapping(value = "lots/byProduct/{productId}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK')")
    public ResponseEntity getLotsByProductId(@PathVariable Long productId) {

        return OpenLmisResponse.response("lots", service.getLotsByProductId(productId));
    }

    @RequestMapping(value = "lot/create", method = PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_LOT')")
    public ResponseEntity saveLot(@RequestBody Lot lot) {
         return OpenLmisResponse.response("lot", service.insertLot(lot));
    }

    //TODO To delete this code on production
    @RequestMapping(value = "delete-requisitions", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity deleteRequisitions(){
        return OpenLmisResponse.response("deleteRequisitions", service.deleteRequisitions());
    }

    @RequestMapping(value = "delete-distributions", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity deleteDistributions(){
        return OpenLmisResponse.response("deleteDistributions", service.deleteDistributions());
    }

    @RequestMapping(value = "delete-stock-cards", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity deleteStockCards() {
        return OpenLmisResponse.response("deleteStockCards", service.deleteStockCards());
    }

    @RequestMapping(value = "delete-lots", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity deleteLots() {
        return OpenLmisResponse.response("deleteLots", service.deleteLots());
    }
    //TODO  End To delete this code on production

    public static String  getCommaSeparatedIds(List<Long> idList){
        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    @RequestMapping(value = "demand-forecasting/print", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printDemandForecasting(HttpServletRequest request) throws JRException, IOException, ClassNotFoundException {
        Long userId = loggedInUserId(request);
        Template orPrintTemplate = templateService.getByName(PRINT_DEMAND_FORECASTING);
        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");
        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);
        map.put(Constants.OPERATOR_NAME, configuration.getValue());
        configuration=  settingService.getByKey(Constants.REPORT_PROGRAM_TITLE);
        map.put(Constants.REPORT_PROGRAM_TITLE,configuration.getValue());
        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("FACILITY_ID", facilityService.getHomeFacility(userId).getId());

        return new ModelAndView(jasperView, map);
    }


}
