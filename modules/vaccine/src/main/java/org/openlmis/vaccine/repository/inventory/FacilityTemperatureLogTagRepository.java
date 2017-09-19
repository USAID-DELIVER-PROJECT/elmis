package org.openlmis.vaccine.repository.inventory;

import org.openlmis.vaccine.domain.inventory.FacilityTemperatureLogTag;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.repository.mapper.inventory.FacilityLogTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hassan on 8/29/17.
 */

@Repository
public class FacilityTemperatureLogTagRepository {
     @Autowired
     private FacilityLogTagMapper mapper;

    public List<FacilityTemperatureLogTag> getAll() {
        return mapper.getAll();
    }

    public FacilityTemperatureLogTag getById(Long id) {
        return mapper.getById(id);
    }

    public void insert(FacilityTemperatureLogTag logTag) {
        mapper.insert(logTag);
    }

    public void update(FacilityTemperatureLogTag logTag) {
        mapper.update(logTag);
    }

    public void insertLogTag(LogTagDTO logTagDTO) {
        mapper.insertLogData(logTagDTO);
    }

    public void updateInserted(Long id, Date currentTimestamp,String route) {
        mapper.updateLastUploaded(id, currentTimestamp,route);
    }
}
