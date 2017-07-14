package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class VaccineInventoryRepository {

    @Autowired
    VaccineInventoryMapper mapper;

    public List<Lot> getLotsByProductId(Long productId) {
        return mapper.getLotsByProductId(productId);
    }


    //TODO To delete this code on production
    public Integer deleteOrderRequisitions(){
        if(mapper.deleteRequisitionLineItems() >0)
        {
            return  mapper.deleteRequisitions();
        }else return null;

    }
    public Integer deleteDistributions(){
        mapper.deleteDistributionStatus();
        if(mapper.deleteDistributionLots() >0)
        {
            if(mapper.deleteDistributionLineItems() >0)
                return  mapper.deleteDistributions();
            else
                return null;
        }else
            return null;
    }

    public Integer deleteStockCards() {
        mapper.deleteEntryKeyValue();
        mapper.deleteEntries();
        mapper.deleteLotsOnHand();
        return mapper.deleteStockCards();
    }

    public Integer deleteLots() {
        return mapper.deleteLots();
    }


    //TODO End To delete this code on production

    public void saveLogTagData(LogTagDTO dto) {
        mapper.saveLogTagData(dto);
    }

    public LogTagDTO getByDateAndSerialNumber(String logDate, String serialNumber) {
        return mapper.geByDateAndSerialNumber(logDate,serialNumber);
    }
    public void updateLogTag(LogTagDTO dto){
        mapper.updateLogTag(dto);
    }

    public List<LogTagDTO> geLogTags(String startDate, String endDate, Pagination pagination) {
        return mapper.getLogTags(startDate,endDate,pagination);
    }
}
