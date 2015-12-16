/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.domain.inventory.VoucherNumberCode;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryDistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class VaccineInventoryDistributionService {

    @Autowired
    VaccineInventoryDistributionRepository repository;

    @Autowired
    ProgramService programService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    MessageService messageService;

    public  static  final String cvsRegionCode="label.vaccine.voucher.number.region.for.cvs";

    public  static  final String cvsDistrictCode="label.vaccine.voucher.number.district.for.cvs";

    public  static  final String rvsDistrictCode="label.vaccine.voucher.number.district.for.rvs";

    public List<Facility> getFacilities(Long userId) {
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        return getOneLevelSupervisedFacilities(facilityId);
    }

    public List<Facility> getOneLevelSupervisedFacilities(Long facilityId) {
        return repository.getOneLevelSupervisedFacilities(facilityId);
    }

    public Long save(VaccineDistribution distribution, Long userId) {
        //Get supervised facility period
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId = programs.get(0).getId();

        ProcessingPeriod period = getCurrentPeriod(facilityId, programId, distribution.getDistributionDate());
        if (period != null) {
            distribution.setPeriodId(period.getId());
        }

        distribution.setVoucherNumber(generateVoucherNumber(facilityId,programId));

        if (distribution.getId() != null) {
            distribution.setModifiedBy(userId);
            repository.updateDistribution(distribution);
        } else {
            distribution.setCreatedBy(userId);
            repository.saveDistribution(distribution);
        }

        for (VaccineDistributionLineItem lineItem : distribution.getLineItems()) {
            lineItem.setDistributionId(distribution.getId());
            if (lineItem.getId() != null) {
                repository.updateDistributionLineItem(lineItem);
            } else {
                repository.saveDistributionLineItem(lineItem);
            }

            if (lineItem.getLots() != null) {
                for (VaccineDistributionLineItemLot lot : lineItem.getLots()) {
                    lot.setDistributionLineItemId(lineItem.getId());
                    if (lot.getId() != null) {
                        repository.updateDistributionLineItemLot(lot);
                    } else {
                        repository.saveDistributionLineItemLot(lot);
                    }
                }
            }
        }
        return distribution.getId();
    }

    private String generateVoucherNumber(Long facilityId, Long programId) {
        VoucherNumberCode voucherNumberCode=new VoucherNumberCode();
        voucherNumberCode =repository.getFacilityVoucherNumberCode(facilityId);

        String lastVoucherNumber=repository.getLastVoucherNumber();
        String newVoucherNumber=null;
        Long newSerial=null;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);

        if(lastVoucherNumber !=null) {
            String serialString = lastVoucherNumber.substring(lastVoucherNumber.lastIndexOf('/') + 1);
            Long serial = Long.parseLong(serialString);
            newSerial = serial + 1;
        }
        else{
            newSerial=1L;
        }
        String[] nationalCodes=voucherNumberCode.getNational().split("\\s+");
        String nationalCode=(nationalCodes.length >1)?(nationalCodes[0].substring(0,2).toUpperCase()+nationalCodes[1].substring(0, 1).toUpperCase()): (nationalCodes[0].substring(0, 3).toUpperCase());

        String regionCode="";
        if(voucherNumberCode.getRegion().equalsIgnoreCase("cvs-region-code")){
            regionCode=messageService.message(cvsRegionCode);
        }
        else {
            String[] regionCodes = voucherNumberCode.getRegion().split("\\s+");
            regionCode = (regionCodes.length > 1) ? (regionCodes[0].substring(0, 2).toUpperCase() + regionCodes[1].substring(0, 1).toUpperCase()) : (regionCodes[0].substring(0, 3).toUpperCase());
        }
        String districtCode="";
        if(voucherNumberCode.getDistrict().equalsIgnoreCase("cvs-district-code"))
        {

            districtCode=messageService.message(cvsDistrictCode);
        }
        else if(voucherNumberCode.getDistrict().equalsIgnoreCase("rvs-district-code")){
            districtCode=messageService.message(rvsDistrictCode);
        }
        else {
            String[] districtCodes = voucherNumberCode.getDistrict().split("\\s+");
            districtCode = (districtCodes.length > 1) ? (districtCodes[0].substring(0, 2).toUpperCase() + districtCodes[1].substring(0, 1).toUpperCase()) : (districtCodes[0].substring(0, 3).toUpperCase());
        }
        newVoucherNumber = nationalCode+"/"+regionCode+"/"+districtCode+"/" + year +"/" + newSerial;
        return newVoucherNumber;
    }

    public List<VaccineDistribution> getDistributedFacilitiesByPeriod(Long userId) {
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId =(programs ==null)?null: programs.get(0).getId();

        ProcessingPeriod period = getCurrentPeriod(facilityId, programId, new Date());
        if (period != null) {
            return repository.getDistributedFacilitiesByPeriod(period.getId());
        } else {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            return repository.getDistributedFacilitiesByMonth(month, year);
        }
    }

    public List<VaccineDistribution> getDistributedFacilitiesByDate(Long userId) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return repository.getDistributedFacilitiesByMonth(month, year);
    }

    public ProcessingPeriod getCurrentPeriod(Long facilityId, Long programId, Date distributionDate) {
        return repository.getCurrentPeriod(facilityId, programId, distributionDate);
    }
    public ProcessingPeriod getCurrentPeriod(Long userId) {
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId =(programs ==null)?null: programs.get(0).getId();
        return repository.getCurrentPeriod(facilityId, programId, new Date());
    }

    public VaccineDistribution getById(Long id) {
        return repository.getById(id);
    }

    public List<Lot> getLotsByProductId(Long productId) {
        return repository.getLotsByProductId(productId);
    }

    public VaccineDistribution getDistributionByVoucherNumber(Long userId,String voucherNumber){
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        return repository.getDistributionByVoucherNumber(facilityId,voucherNumber);
    }

    public VoucherNumberCode  getFacilityGeographicZone(Long userId)
    {
        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        return repository.getFacilityVoucherNumberCode(facilityId);
    }
}
