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
package org.openlmis.web.controller.seasonaling;

import org.apache.log4j.Logger;
import org.openlmis.core.domain.OrderQuantityAdjustmentFactor;
import org.openlmis.core.domain.OrderQuantityAdjustmentProduct;
import org.openlmis.core.domain.OrderQuantityAdjustmentType;
import org.openlmis.core.exception.DataException;

import org.openlmis.core.service.OrderQuantityAdjustmentFactorService;
import org.openlmis.core.service.OrderQuantityAdjustmentProductService;
import org.openlmis.core.service.OrderQuantityAdjustmentTypeService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Controller
@RequestMapping(value = "/season-rationing")
public class SeasonRationingLookupController extends BaseController {
    public static final String SEASONALRATIONINGTYPELIST = "seasonalityRationingsList";
    public static final String SEASONALRATIONINGTYPE = "seasonalityRationingType";
    public static final String ADJUSTMENTFACTOR = "adjustmentFactor";
    public static final String ADJUSTMENTFACTORLIST = "adjustmentFactorList";
    public static final String ADJUSTMENT_PRODUCTS = "adjustmentProducts";
    private static final Logger LOGGER = Logger.getLogger(SeasonRationingLookupController.class);
    public  static final  String DUPLICATE_CODE_EXCEPTION="Duplicate Code Exists in DB.";
    public  static final  String SUCCESSULLY_DELETED="Deleted successfully";
    @Autowired
    private OrderQuantityAdjustmentTypeService quantityAdjustmentTypeService;
    @Autowired
    private OrderQuantityAdjustmentFactorService adjustmentFactorService;

    @Autowired
    private OrderQuantityAdjustmentProductService adjustmentProductService;

    public ResponseEntity<OpenLmisResponse> saveSeasonalityRationingType(OrderQuantityAdjustmentType quantityAdjustmentType, boolean createOperation) {
        try {
            if (createOperation) {

                this.quantityAdjustmentTypeService.addOrderQuantityAdjustmentType(quantityAdjustmentType);
            } else {
                this.quantityAdjustmentTypeService.updateOrderQuantityAdjustmentType(quantityAdjustmentType);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(SEASONALRATIONINGTYPE, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(quantityAdjustmentType.getId()));
            response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
            return response;
        } catch (DuplicateKeyException exp) {
            LOGGER.warn(DUPLICATE_CODE_EXCEPTION,exp);
            return OpenLmisResponse.error(DUPLICATE_CODE_EXCEPTION, HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            LOGGER.warn(" data exception", e);
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.warn(" exception", e);
            return OpenLmisResponse.error(DUPLICATE_CODE_EXCEPTION, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> getSeasonalityRationingDetail1(@PathVariable("id") Long id) {
        OrderQuantityAdjustmentType adjustmentType = this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(id);
        return OpenLmisResponse.response(SEASONALRATIONINGTYPE, adjustmentType);
    }


    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> updateSeasnalityRationingType(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {

        quantityAdjustmentType.setModifiedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedDate(new Date());
        return saveSeasonalityRationingType(quantityAdjustmentType, false);
    }

    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/seasonalityRationingTypes", method = RequestMethod.POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> createSeasonalityRationingType(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType, HttpServletRequest request) {
        quantityAdjustmentType.setCreatedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedBy(loggedInUserId(request));
        quantityAdjustmentType.setModifiedDate(new Date());
        quantityAdjustmentType.setCreatedDate(new Date());
        return saveSeasonalityRationingType(quantityAdjustmentType, true);
    }


    @RequestMapping(value = "/seasonalityRationingTypes/{id}", method = DELETE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> removeSeasonRationingType(@PathVariable("id") long id) {

        OrderQuantityAdjustmentType quantityAdjustmentType= new OrderQuantityAdjustmentType();
        quantityAdjustmentType.setId(Long.valueOf(id));
        this.quantityAdjustmentTypeService.deleteOrderQuantityAdjustmentType(quantityAdjustmentType);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + SUCCESSULLY_DELETED);
        response.getBody().addData(SEASONALRATIONINGTYPE, quantityAdjustmentType);


        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
        return response;
    }


    @RequestMapping(value = "/seasonalityRationingTypes", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> searchSeasonRationingTypeList(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.searchForQuantityAdjustmentType(param));
    }

    @RequestMapping(value = "/seasonalityRationingTypes_remove", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> deleteSeasonalRationingType(@RequestBody OrderQuantityAdjustmentType quantityAdjustmentType) {

        this.quantityAdjustmentTypeService.deleteOrderQuantityAdjustmentType(quantityAdjustmentType);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + quantityAdjustmentType.getId()) + SUCCESSULLY_DELETED);
        response.getBody().addData(SEASONALRATIONINGTYPE, quantityAdjustmentType);
        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());

        return response;
    }

    @RequestMapping(value = "/seasonalityRationingTypeList", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> loadAllSeasonRationingTypes() {
        return OpenLmisResponse.response(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
    }
    /*
    orderQuanitityAdjustmentFactory
     */

    public ResponseEntity<OpenLmisResponse> saveAdjustmentFactor(OrderQuantityAdjustmentFactor adjustmentFactor, boolean createOperation) {
        try {
            if (createOperation) {

                this.adjustmentFactorService.addOrderQuantityAdjustmentFactor(adjustmentFactor);
            } else {
                this.adjustmentFactorService.updateOrderQuantityAdjustmentFactor(adjustmentFactor);
            }
            ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + "' " + (createOperation ? "created" : "updated") + " successfully");
            response.getBody().addData(ADJUSTMENTFACTOR, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentType(adjustmentFactor.getId()));
            response.getBody().addData(ADJUSTMENTFACTORLIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
            return response;
        } catch (DuplicateKeyException exp) {
            LOGGER.warn("", exp);
            return OpenLmisResponse.error(DUPLICATE_CODE_EXCEPTION, HttpStatus.BAD_REQUEST);
        } catch (DataException e) {
            LOGGER.warn("", e);
            return error(e, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.warn("", e);
            return OpenLmisResponse.error(DUPLICATE_CODE_EXCEPTION, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/adjustmentFactors/{id}", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAdjustmentFactorDetail1(@PathVariable("id") Long id) {

        OrderQuantityAdjustmentFactor adjustmentFactor = this.adjustmentFactorService.loadOrderQuantityAdjustmentFactorDetail(id);
        return OpenLmisResponse.response(ADJUSTMENTFACTOR, adjustmentFactor);
    }


    @RequestMapping(value = "/adjustmentFactors/{id}", method = PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> updateAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        adjustmentFactor.setModifiedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedDate(new Date());
        return saveAdjustmentFactor(adjustmentFactor, false);
    }

    //////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/adjustmentFactors", method = RequestMethod.POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> createAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor, HttpServletRequest request) {
        adjustmentFactor.setCreatedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedBy(loggedInUserId(request));
        adjustmentFactor.setModifiedDate(new Date());
        adjustmentFactor.setCreatedDate(new Date());
        return saveAdjustmentFactor(adjustmentFactor, true);
    }


    @RequestMapping(value = "/adjustmentFactors/{id}", method = DELETE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> removeAdjustmentFactor(@PathVariable("id") long id) {
        OrderQuantityAdjustmentFactor adjustmentFactor = new OrderQuantityAdjustmentFactor();
        adjustmentFactor.setId(Long.valueOf(id));
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + SUCCESSULLY_DELETED);
        response.getBody().addData(ADJUSTMENTFACTOR, adjustmentFactor);
        this.adjustmentFactorService.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        response.getBody().addData(SEASONALRATIONINGTYPELIST, this.quantityAdjustmentTypeService.loadOrderQuantityAdjustmentTypeList());
        return response;
    }

    @RequestMapping(value = "/adjustmentFactors", method = RequestMethod.GET)
        @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> searchAdjustmentFactorList(@RequestParam(required = true) String param) {
        return OpenLmisResponse.response(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.searchAdjustmentFactor(param));
    }

    @RequestMapping(value = "/adjustmentFactors_remove", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING')")
    public ResponseEntity<OpenLmisResponse> deleteAdjustmentFactor(@RequestBody OrderQuantityAdjustmentFactor adjustmentFactor) {

        this.adjustmentFactorService.deleteOrderQuantityAdjustmentFactor(adjustmentFactor);
        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(("'" + adjustmentFactor.getId()) + SUCCESSULLY_DELETED);
        response.getBody().addData(ADJUSTMENTFACTOR, adjustmentFactor);
        response.getBody().addData(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.loadOrderQuantityAdjustmentFactor());

        return response;
    }

    @RequestMapping(value = "/adjustmentFactorList", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> loadAllAdjustmentFactories() {
        return OpenLmisResponse.response(ADJUSTMENTFACTORLIST, this.adjustmentFactorService.loadOrderQuantityAdjustmentFactor());
    }

    @RequestMapping(value = "/adjustmentProducts", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAllAdjustmentProducts() {
        return OpenLmisResponse.response(ADJUSTMENT_PRODUCTS, this.adjustmentProductService.getAll());
    }
    @RequestMapping(value = "/adjustmentProducts", method = RequestMethod.POST)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_SEASONALITY_RATIONING, MANAGE_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> saveAdjustments(@RequestBody OrderQuantityAdjustmentProduct adjustmentProduct, HttpServletRequest request) {
        ResponseEntity<OpenLmisResponse> response;

        try {
            Long userId = loggedInUserId(request);
            adjustmentProduct.setCreatedBy(userId);
            adjustmentProduct.setModifiedBy(userId);
            this.adjustmentProductService.save(adjustmentProduct);
        } catch (DataException e) {
            LOGGER.warn("",e);
            response = OpenLmisResponse.error(e, BAD_REQUEST);
            return response;
        }
        response = OpenLmisResponse.success(messageService.message("message.product.seasonality.adjustment.created.success", adjustmentProduct.getProduct().getName()));

        return response;

    }
    @RequestMapping(value = "/search", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getByProductAndFacility(@RequestParam(value = "productId") Long productId,
                                                                    @RequestParam(value = "facilityId") Long facilityId) {
        return OpenLmisResponse.response(ADJUSTMENT_PRODUCTS, this.adjustmentProductService.getByProductAndFacility(productId, facilityId));
    }
}
