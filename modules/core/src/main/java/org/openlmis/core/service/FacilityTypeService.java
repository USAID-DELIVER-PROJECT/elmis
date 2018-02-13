package org.openlmis.core.service;

import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.repository.FacilityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacilityTypeService {
    @Autowired
    private FacilityTypeRepository repository;

    public FacilityType getFacilityTypeByCode(String code){
        return repository.getFacilityTypeByCode(code);
    }

}
