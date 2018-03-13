

/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.openlmis.core.repository;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityOwner;
import org.openlmis.core.domain.Owner;
import org.openlmis.core.repository.mapper.FacilityOwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityOwnerRepository {
    @Autowired
    FacilityOwnerMapper mapper;
    public Long addNewFacilityOwner(FacilityOwner facilityOwner){
        return  mapper.insert(facilityOwner);
    }
    public Long deleteFacilityOwners(Facility facility){
        return  mapper.deleteOwners(facility);
    }

    public List<FacilityOwner> loadFacilityOwners(Facility facility) {
        return mapper.loadFacilityOwners( facility);
    }

    public List<Owner> getAllOWners() {

        return mapper.allOwners();
    }

    public FacilityOwner getFacilityOwnerByOwnerCodeAndFacilityCode(String owenerCode, String facilityCode) {
        return  mapper.getFacilityOwnerByOwnerCodeAndFacilityCode(owenerCode, facilityCode);
    }

    public Owner getOwnerByCode(String code) {
        return  this.mapper.getOwnerByCode(code);
    }

    public void updateFacilityOwner(FacilityOwner record) {
        this.mapper.update(record);
    }

    public Long addOwner(Owner owner) {
      return  this.mapper.addOwner(owner);
    }
}
