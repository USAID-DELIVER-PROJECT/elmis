/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProductService;
import org.openlmis.vaccine.domain.inventory.VaccineInventoryProductConfiguration;
import org.openlmis.vaccine.dto.StockRequirementsDTO;
import org.openlmis.vaccine.dto.StockRequirements;
import org.openlmis.demographics.service.PopulationService;
import org.openlmis.vaccine.repository.StockRequirementsRepository;
import org.openlmis.vaccine.service.inventory.VaccineInventoryConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @deprecated
 * This class is intended to return a variety of stock-related information through its getStockRequirements member. Because the calculations it uses are VIMS specific, the class may be considered depreciated.
 */
@Deprecated
@Service
public class StockRequirementsService
{
    @Autowired
    ProductService productService;
    @Autowired
    private FacilityService facilityService = null;
    @Autowired
    private FacilityApprovedProductRepository facilityApprovedProductRepository = null;
    @Autowired
    private FacilityProgramProductService facilityProgramProductService = null;
    @Autowired
    private PopulationService populationService = null;
    @Autowired
    private VaccineInventoryConfigurationService vaccineConfigurationService;
    private List<StockRequirements> stockRequirements;
    @Autowired
    private StockRequirementsRepository repository;
    @Autowired
    private ConfigurationSettingService configurationSettingService;


    public List<StockRequirementsDTO> getStockRequirements(Long facilityId, Long programId)
    {

        //Delete all forecast for a facility and program
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);

        repository.resetFacilityStockRequirements(programId, facilityId, year);

        //Get facility in order to access its catchment population
        Facility facility = facilityService.getById(facilityId);
        if(facility == null)
            return null;

        List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = facilityApprovedProductRepository.getAllByFacilityAndProgramId(facilityId, programId);
        List<FacilityProgramProduct> programProductsByProgram = facilityProgramProductService.getActiveProductsForProgramAndFacility(programId, facilityId);
        //Catch non reference facilityProgram product used for supplies to speed up
        List<FacilityProgramProduct> programProductsByProgramCopy = facilityProgramProductService.getActiveProductsForProgramAndFacility(programId, facilityId);

        //1. Filter vaccines only here.
        for (FacilityProgramProduct facilityProgramProduct : programProductsByProgram)
        {

            //Set productId
            Long productId = facilityProgramProduct.getProduct().getId();

            //Set minStock, maxStock, and eop
            for(FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts)
            {

                if(productId.equals(facilityTypeApprovedProduct.getProgramProduct().getProduct().getId()))
                {

                    StockRequirements requirements = new StockRequirements();
                    StockRequirementsDTO existingRequirements = repository.getByProductId(programId, facilityId, productId, year);
                    //For Vaccine Program: Get Vaccine product configuration to make forecast by bundling
                    VaccineInventoryProductConfiguration vaccineProductConfiguration = vaccineConfigurationService.getByProductId(productId);
                    //Program info
                    requirements.setProgramId(programId);
                    //Set facility info
                    requirements.setFacilityId(facilityId);
                    requirements.setFacilityCode(facility.getFacilityType().getCode());
                    requirements.setPresentation(facilityProgramProduct.getProduct().getDosesPerDispensingUnit());
                    requirements.setYear(year);

                    //Set our ISA to the most specific one possible
                    ISA isa = facilityProgramProduct.getOverriddenIsa();
                    if(isa == null) {
                        if(facilityProgramProduct.getProgramProductIsa() != null) {
                            isa = facilityProgramProduct.getProgramProductIsa().getIsa();
                        }
                    }

                    requirements.setIsa(isa);
                    requirements.setProductId(productId);
                    requirements.setProductName(facilityProgramProduct.getProduct().getPrimaryName());

                    //Set population
                    Long populationSource = (isa != null) ? isa.getPopulationSource() : null;
                    requirements.setPopulation(populationService.getPopulation(facility, facilityProgramProduct.getProgram(), populationSource));

                    ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
                    ProductCategory category = programProduct.getProductCategory();
                    requirements.setProductCategory(category.getName());
                    requirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
                    requirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
                    requirements.setEop(facilityTypeApprovedProduct.getEop());

                    if (existingRequirements != null && requirements.getIsaValue() != 0) {
                        requirements.setId(existingRequirements.getId());
                        update(requirements);
                    } else {
                        save(requirements);
                    }

                    if(vaccineProductConfiguration !=null && vaccineProductConfiguration.getAdministrationMode() != null){
                        setVaccineAdministrationModeRequirement(programProductsByProgramCopy, requirements, vaccineProductConfiguration.getAdministrationMode());
                    }
                    if(vaccineProductConfiguration !=null && vaccineProductConfiguration.getDilutionSyringe() != null){
                        setVaccineDilutionSyringeRequirement(programProductsByProgramCopy, requirements, vaccineProductConfiguration.getDilutionSyringe());
                    }

                }
            }

        }
        return repository.getAll(programId, facilityId,year);
    }


    private void save(StockRequirements requirements)
    {
        StockRequirementsDTO existingRequirement = repository.getByProductId(requirements.getProgramId(), requirements.getFacilityId(), requirements.getProductId(), requirements.getYear());
        if(requirements.getIsaValue() >0 && existingRequirement==null) {
            repository.save(requirements);
        }

    }

    private void update(StockRequirements requirements) {
        repository.update(requirements);
    }

    private void updateBundling(StockRequirementsDTO requirements) {
        repository.updateBundling(requirements);
    }



    //For Vaccine Program
    private void setVaccineAdministrationModeRequirement(List<FacilityProgramProduct> programProductsByProgram, StockRequirements vaccineRequirements, Long supplyProductId) {

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);

        for(FacilityProgramProduct facilityProgramProduct:programProductsByProgram)
        {
            if(supplyProductId.equals(facilityProgramProduct.getProduct().getId())) {
                StockRequirementsDTO existingRequirement = repository.getByProductId(vaccineRequirements.getProgramId(), vaccineRequirements.getFacilityId(), supplyProductId, year);
                StockRequirements newSupplyRequirements = new StockRequirements();

                newSupplyRequirements.setProgramId(vaccineRequirements.getProgramId());
                newSupplyRequirements.setFacilityId(vaccineRequirements.getFacilityId());
                newSupplyRequirements.setProductId(supplyProductId);
                newSupplyRequirements.setFacilityCode(vaccineRequirements.getFacilityCode());
                newSupplyRequirements.setYear(year);
                newSupplyRequirements.setPopulation(vaccineRequirements.getPopulation());

                //ISA
                ISA isa = facilityProgramProduct.getOverriddenIsa();
                if (isa == null) {
                    if (facilityProgramProduct.getProgramProductIsa() != null) {
                        isa = facilityProgramProduct.getProgramProductIsa().getIsa();
                    }
                }
                isa.setDosesPerYear(vaccineRequirements.getIsa().getDosesPerYear());
                isa.setWhoRatio(vaccineRequirements.getIsa().getWhoRatio());
                newSupplyRequirements.setIsa(isa);

                //Get product EOP for order level calculation
                FacilityTypeApprovedProduct facilityTypeApprovedProduct=facilityApprovedProductRepository.getFacilityApprovedProductByProgramProductIdAndFacilityTypeCode(facilityProgramProduct.getId(), vaccineRequirements.getFacilityCode());
                newSupplyRequirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
                newSupplyRequirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
                newSupplyRequirements.setEop(facilityTypeApprovedProduct.getEop());

                //Category
                ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
                if(programProduct != null) {
                    ProductCategory category = programProduct.getProductCategory();
                    newSupplyRequirements.setProductCategory(category.getName());
                }
                if (existingRequirement != null) {

                    existingRequirement.setAnnualNeed(existingRequirement.getAnnualNeed() + newSupplyRequirements.getAnnualNeed());
                        existingRequirement.setSupplyPeriodNeed(existingRequirement.getSupplyPeriodNeed() + newSupplyRequirements.getSupplyPeriodNeed());
                        existingRequirement.setReorderLevel(existingRequirement.getReorderLevel() + newSupplyRequirements.getReorderLevel());
                        existingRequirement.setBufferStock(existingRequirement.getBufferStock() + newSupplyRequirements.getBufferStock());
                        existingRequirement.setMaximumStock(existingRequirement.getMaximumStock() + newSupplyRequirements.getMaximumStock());
                        existingRequirement.setIsaValue(existingRequirement.getIsaValue() + newSupplyRequirements.getIsaValue());
                        updateBundling(existingRequirement);

                } else {
                      save(newSupplyRequirements);
                }
                setSafetyBox(programProductsByProgram,newSupplyRequirements);

            }

        }
    }


    private void setVaccineDilutionSyringeRequirement(List<FacilityProgramProduct> programProductsByProgram,StockRequirements vaccineRequirements, Long supplyProductId){

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        StockRequirements newSupplyRequirements = new StockRequirements();
        StockRequirementsDTO existingRequirement = repository.getByProductId(vaccineRequirements.getProgramId(), vaccineRequirements.getFacilityId(), supplyProductId, year);

        for(FacilityProgramProduct facilityProgramProduct:programProductsByProgram)
        {

            if(supplyProductId.equals(facilityProgramProduct.getProduct().getId())) {

                newSupplyRequirements.setProgramId(vaccineRequirements.getProgramId());
                newSupplyRequirements.setFacilityId(vaccineRequirements.getFacilityId());
                newSupplyRequirements.setProductId(supplyProductId);
                newSupplyRequirements.setFacilityCode(vaccineRequirements.getFacilityCode());
                newSupplyRequirements.setYear(year);

                Integer vaccineAnnualNeed=vaccineRequirements.getAnnualNeed();
                Integer vaccinePresentation=vaccineRequirements.getPresentation();
                ISA supplyIsa = facilityProgramProduct.getOverriddenIsa();
                if (supplyIsa == null) {
                    if (facilityProgramProduct.getProgramProductIsa() != null) {
                        supplyIsa = facilityProgramProduct.getProgramProductIsa().getIsa();
                    }
                }
                newSupplyRequirements.setIsa(supplyIsa);
                Double supplyWastageFactor=supplyIsa.getWastageFactor();
                Double supplyIsaValue=((vaccineAnnualNeed/vaccinePresentation) * supplyWastageFactor)/12;

                newSupplyRequirements.setIsaValue(supplyIsaValue.intValue());

               //Get product EOP for order level calculation
                FacilityTypeApprovedProduct facilityTypeApprovedProduct=facilityApprovedProductRepository.getFacilityApprovedProductByProgramProductIdAndFacilityTypeCode(facilityProgramProduct.getId(), vaccineRequirements.getFacilityCode());
                newSupplyRequirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
                newSupplyRequirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
                newSupplyRequirements.setEop(facilityTypeApprovedProduct.getEop());

                    ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
                if(programProduct !=null) {
                    ProductCategory category = programProduct.getProductCategory();
                    newSupplyRequirements.setProductCategory(category.getName());
                }
                if (existingRequirement != null) {
                    if(newSupplyRequirements.getIsaValue() != null) {
                        existingRequirement.setAnnualNeed(existingRequirement.getAnnualNeed() + newSupplyRequirements.getAnnualNeed());
                        existingRequirement.setSupplyPeriodNeed(existingRequirement.getSupplyPeriodNeed() + newSupplyRequirements.getSupplyPeriodNeed());
                        existingRequirement.setReorderLevel(existingRequirement.getReorderLevel() + newSupplyRequirements.getReorderLevel());
                        existingRequirement.setBufferStock(existingRequirement.getBufferStock() + newSupplyRequirements.getBufferStock());
                        existingRequirement.setMaximumStock(existingRequirement.getMaximumStock() + newSupplyRequirements.getMaximumStock());
                        existingRequirement.setIsaValue(existingRequirement.getIsaValue() + newSupplyRequirements.getIsaValue());
                        updateBundling(existingRequirement);
                    }
                }
                else{
                    save(newSupplyRequirements);
                }
                setSafetyBox(programProductsByProgram, newSupplyRequirements);
            }
        }
    }

    private void setSafetyBox(List<FacilityProgramProduct> programProductsByProgram, StockRequirements supplyRequirements) {
       ConfigurationSetting configurationSetting=configurationSettingService.getByKey("VACCINE_STOCK_REQUIREMENTS_SAFETY_BOX_CODE");
        if(configurationSetting != null)
        {
            Product safetyBox=productService.getByCode(configurationSetting.getValue());
            if(safetyBox != null)
            {
                StockRequirements newSafetyBoxRequirements = new StockRequirements();
                StockRequirementsDTO existingRequirement = repository.getByProductId(supplyRequirements.getProgramId(), supplyRequirements.getFacilityId(), safetyBox.getId(), supplyRequirements.getYear());

                for(FacilityProgramProduct facilityProgramProduct:programProductsByProgram){

                    if(safetyBox.getId().equals(facilityProgramProduct.getProduct().getId())){
                        newSafetyBoxRequirements.setProgramId(supplyRequirements.getProgramId());
                        newSafetyBoxRequirements.setFacilityId(supplyRequirements.getFacilityId());
                        newSafetyBoxRequirements.setProductId(safetyBox.getId());
                        newSafetyBoxRequirements.setYear(supplyRequirements.getYear());

                        ISA isa = facilityProgramProduct.getOverriddenIsa();
                        if (isa == null) {
                            if (facilityProgramProduct.getProgramProductIsa() != null) {
                                isa = facilityProgramProduct.getProgramProductIsa().getIsa();
                            }
                        }
                        newSafetyBoxRequirements.setIsa(isa);
                        Double safetyBoxIsaValue=((supplyRequirements.getAnnualNeed()/100)*isa.getWastageFactor())/12;
                        safetyBoxIsaValue = (safetyBoxIsaValue < 1) ? 1 : safetyBoxIsaValue;
                        newSafetyBoxRequirements.setIsaValue(safetyBoxIsaValue.intValue());

                        FacilityTypeApprovedProduct facilityTypeApprovedProduct=facilityApprovedProductRepository.getFacilityApprovedProductByProgramProductIdAndFacilityTypeCode(facilityProgramProduct.getId(), supplyRequirements.getFacilityCode());
                        newSafetyBoxRequirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
                        newSafetyBoxRequirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
                        newSafetyBoxRequirements.setEop(facilityTypeApprovedProduct.getEop());

                        ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
                        if(programProduct !=null) {
                            ProductCategory category = programProduct.getProductCategory();
                            newSafetyBoxRequirements.setProductCategory(category.getName());
                        }
                        if(existingRequirement != null){
                            if(newSafetyBoxRequirements.getIsaValue() != null) {
                                existingRequirement.setAnnualNeed(existingRequirement.getAnnualNeed() + newSafetyBoxRequirements.getAnnualNeed());
                                existingRequirement.setSupplyPeriodNeed(existingRequirement.getSupplyPeriodNeed() + newSafetyBoxRequirements.getSupplyPeriodNeed());
                                existingRequirement.setReorderLevel(existingRequirement.getReorderLevel() + newSafetyBoxRequirements.getReorderLevel());
                                existingRequirement.setBufferStock(existingRequirement.getBufferStock() + newSafetyBoxRequirements.getBufferStock());
                                existingRequirement.setMaximumStock(existingRequirement.getMaximumStock() + newSafetyBoxRequirements.getMaximumStock());
                                existingRequirement.setIsaValue(existingRequirement.getIsaValue() + newSafetyBoxRequirements.getIsaValue());
                                updateBundling(existingRequirement);
                            }
                        }
                        else{
                            save(newSafetyBoxRequirements);
                        }

                    }
                }

            }
        }
    }
}
