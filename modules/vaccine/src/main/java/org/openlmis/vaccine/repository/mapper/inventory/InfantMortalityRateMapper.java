package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.springframework.stereotype.Repository;

/**
 * Created by hassan on 1/6/16.
 */
@Repository
public interface InfantMortalityRateMapper {

    @Select("")

    InfantMortalityRateDTO getByCode(String code);

}
