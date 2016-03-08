package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.Lot;
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

}
