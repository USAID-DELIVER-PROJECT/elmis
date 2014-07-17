/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductGroupService;
import org.openlmis.core.service.ProductService;
import org.openlmis.web.form.ProductDTO;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to listing products.
 */
@RequestMapping(value = "/products")
@Controller
public class ProductController extends BaseController {

  @Autowired
  private ProductGroupService groupService;

  @Autowired
  private ProductFormService formService;

  @Autowired
  private ProductService service;

  @RequestMapping(value = "/groups", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public List<ProductGroup> getAllGroups() {
    return groupService.getAll();
  }

  @RequestMapping(value = "/forms", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public List<ProductForm> getAllForms() {
    return formService.getAll();
  }

  @RequestMapping(value = "/dosageUnits", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public List<DosageUnit> getAllDosageUnits() {
    return service.getAllDosageUnits();
  }

  @RequestMapping(value = "/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public ProductDTO getById(@PathVariable(value = "id") Long id) {
    Product product = service.getById(id);
    List<ProgramProduct> programProducts = new ArrayList<>();
    return new ProductDTO(product, product.getModifiedDate(), programProducts);
  }

  @RequestMapping(method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ProductDTO productDTO, HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> response;

    Product product = productDTO.getProduct();
    try {
      Long userId = loggedInUserId(request);
      product.setCreatedBy(userId);
      product.setModifiedBy(userId);
      service.save(product);
    } catch (DataException e) {
      response = OpenLmisResponse.error(e, BAD_REQUEST);
      return response;
    }
    response = OpenLmisResponse.success(messageService.message("message.product.created.success", product.getName()));
    response.getBody().addData("productId", product.getId());
    return response;
  }

  @RequestMapping(value = "/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody ProductDTO productDTO, @PathVariable(value = "id") Long id,
                                                 HttpServletRequest request) {
    Product product = productDTO.getProduct();
    try {
      Long userId = loggedInUserId(request);
      product.setId(id);
      product.setModifiedBy(userId);
      service.save(product);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> success = success(messageService.message("message.product.updated.success", product.getName()));
    success.getBody().addData("productId", product.getId());
    return success;
  }
}

