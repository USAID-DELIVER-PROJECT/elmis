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

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.SupplyPartnerProgramRepository;
import org.openlmis.core.repository.SupplyPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class SupplyPartnerService {

  @Autowired
  private SupplyPartnerRepository repository;

  @Autowired
  private ProgramProductService programProductService;

  @Autowired
  FacilityService facilityService;

  @Autowired
  private SupplyPartnerProgramRepository supplyPartnerProgramRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProgramService programService;

  public List<SupplyPartner> getAll() {
    return repository.getAll();
  }

  public SupplyPartner getById(Long id) {
    return repository.getById(id);
  }

  public void insert(SupplyPartner partner) {
    repository.insert(partner);
    for (SupplyPartnerProgram program : partner.getSubscribedPrograms()) {
      program.setSupplyPartnerId(partner.getId());
      supplyPartnerProgramRepository.insert(program);
    }
  }

  public void update(SupplyPartner partner) {
    repository.update(partner);
    supplyPartnerProgramRepository.deleteForSupplyPartner(partner.getId());
    for (SupplyPartnerProgram program : partner.getSubscribedPrograms()) {
      program.setSupplyPartnerId(partner.getId());
      supplyPartnerProgramRepository.insert(program);
      configureProducts(program);
      configureFacilityMembership(program);
    }
  }

  private void configureProducts(SupplyPartnerProgram spp) {
    // check if this product has an entry in program_products
    Program program = programService.getById(spp.getDestinationProgramId());
    for (SupplyPartnerProgramProduct product : spp.getProducts()) {
      ProgramProduct pp = programProductService.getByProgramAndProductId(spp.getDestinationProgramId(), product.getProductId());
      ProgramProduct programProductSource = programProductService.getByProgramAndProductId(spp.getSourceProgramId(), product.getProductId());
      if (pp == null && programProductSource != null) {
        saveProgramProduct(program, product, programProductSource);
      }
    }

  }

  private void saveProgramProduct(Program program, SupplyPartnerProgramProduct product, ProgramProduct programProductSource) {
    ProgramProduct programProduct = new ProgramProduct();
    Product prod = productService.getById(product.getProductId());
    programProduct.setActive(true);
    programProduct.setProduct(prod);
    programProduct.setProgram(program);

    programProduct.setFullSupply(programProductSource.isFullSupply());
    programProduct.setDosesPerMonth(programProductSource.getDosesPerMonth());
    programProduct.setDisplayOrder(programProductSource.getDisplayOrder());
    programProduct.setProductCategoryId(programProductSource.getProductCategoryId());
    programProduct.setProductCategory(programProductSource.getProductCategory());
    programProduct.setCurrentPrice(programProductSource.getCurrentPrice());
    programProductService.save(programProduct);
  }

  private void configureFacilityMembership(SupplyPartnerProgram spp) {
    // check if this facility has been configured correctly? if not go ahead and configure it.
    Program program = new Program();
    program.setId(spp.getDestinationProgramId());

    RequisitionGroup group = new RequisitionGroup();
    group.setId(spp.getDestinationRequisitionGroupId());


    for (SupplyPartnerProgramFacility facility : spp.getFacilities()) {
      Facility facilityObj = facilityService.getById(facility.getFacilityId());
      List<ProgramSupported> supported = facilityObj
          .getSupportedPrograms()
          .stream()
          .filter(ps -> spp.getDestinationProgramId().equals(ps.getProgram().getId()))
          .collect(Collectors.toList());
      if (supported.size() == 0) {
        ProgramSupported ps = new ProgramSupported();
        ps.setProgram(program);
        ps.setFacilityId(facility.getFacilityId());
        ps.setActive(true);
        ps.setStartDate(new Date());
        facilityObj.getSupportedPrograms().add(ps);
        facilityService.update(facilityObj);
      }
      // configure requisition group membership
//      try{
//       RequisitionGroupMember member = new RequisitionGroupMember(group, facilityObj);
//       requisitionGroupMemberService.save(member);
//      }catch(Exception exp){
//        //EAT THAT EXCEPTION HERE WILL YA! - YEAHHHHH
//      }


    }
  }
}
