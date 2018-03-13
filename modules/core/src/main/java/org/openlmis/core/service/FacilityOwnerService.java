/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityOwnerRepository;
import org.openlmis.core.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class FacilityOwnerService {
    @Autowired
    FacilityService facilityService;
    @Autowired
    private FacilityOwnerRepository repository;

    public void addFacilityOnwers(Facility facility, List<FacilityOwner> facilityOwnerList) {

        if (facilityOwnerList != null && !facilityOwnerList.isEmpty()) {
            repository.deleteFacilityOwners(facility);
            for (FacilityOwner facilityOwner : facilityOwnerList) {
                if (facilityOwner.isActive()) {
                    facilityOwner.setFacility(facility.getId());
                    repository.addNewFacilityOwner(facilityOwner);
                }
            }
        }
    }

    public List<FacilityOwner> loadFailityOwnerList(Facility facility) {
        List<FacilityOwner> facilityOwnerList = new ArrayList<>();
        List<FacilityOwner> assignedOwnerList = repository.loadFacilityOwners(facility);
        List<Owner> operators = this.getAllOwners();
        facilityOwnerList.addAll(assignedOwnerList);
        for (Owner operator : operators) {
            if (!isOwnerAssigned(assignedOwnerList, operator)) {
                FacilityOwner facilityOwner = new FacilityOwner();
                facilityOwner.setOwner(operator);
                facilityOwner.setActive(false);
                facilityOwner.setFacility(facility.getId());
                facilityOwnerList.add(facilityOwner);
            }
        }
        return facilityOwnerList;

    }

    public List<Owner> getAllOwners() {
        return repository.getAllOWners();
    }

    public boolean isOwnerAssigned(List<FacilityOwner> facilityOwnerList, Owner operator) {
        if (facilityOwnerList != null && !facilityOwnerList.isEmpty()) {
            for (FacilityOwner owner : facilityOwnerList) {
                if (operator.getId() .equals( owner.getOwner().getId())) {
                    return true;
                }
            }
        }
        return false;

    }

    public List<FacilityOwner> getAllFacilityOwners() {
        List<FacilityOwner> facilityOwnerList = new ArrayList<>();
        List<Owner> operators = this.getAllOwners();
        for (Owner operator : operators) {
            FacilityOwner facilityOwner1 = new FacilityOwner();
            facilityOwner1.setOwner(operator);
            facilityOwner1.setActive(false);
            facilityOwnerList.add(facilityOwner1);
        }
        return facilityOwnerList;
    }

    public BaseModel getFacilityOwner(FacilityOwner record) {
        FacilityOwner facilityOwner=this.repository.getFacilityOwnerByOwnerCodeAndFacilityCode(record.getOwner().getCode(),record.getFacilityCode());
        return  facilityOwner;
    }

    public void uploadFacilityOwner(FacilityOwner record) {

        Facility facility = new Facility();
        facility.setCode(record.getFacilityCode());

        facility = facilityService.getByCode(facility);
        record.setFacility(facility.getId());

        Owner owner =repository.getOwnerByCode(record.getOwner().getCode());
        if(owner==null){
            owner= new Owner();
            owner.setCode(record.getOwner().getCode());
            owner.setText(record.getOwner().getCode());
            repository.addOwner(owner);
        }
        record.setOwner(owner);

        if (record.getId() == null) {
            repository.addNewFacilityOwner(record);
        } else {
            repository.updateFacilityOwner(record);
        }
    }
}
