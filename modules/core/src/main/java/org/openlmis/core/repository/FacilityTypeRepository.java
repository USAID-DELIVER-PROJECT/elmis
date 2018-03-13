package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.repository.mapper.FacilityTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@NoArgsConstructor
public class FacilityTypeRepository {
    @Autowired
    private FacilityTypeMapper mapper;

    public FacilityType getFacilityTypeByCode(String code){
        return mapper.getByFacilityCode(code);
    }


}
