package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;

import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionLineItem;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
public class VaccineOrderRequisitionLineItemService {

    @Autowired
    VaccineOrderRequisitionLineItemRepository itemRepository;

    public void saveVaccineOrderRequisitionLineItems(List<VaccineOrderRequisitionLineItem> lineItems, Long reportId) {

        for (VaccineOrderRequisitionLineItem lineItem : emptyIfNull(lineItems)) {
            if (lineItem.getId() == null) {
                lineItem.setOrderId(reportId);
                itemRepository.Insert(lineItem);
            } else {

                if(lineItem.getMaximumStock() < lineItem.getStockOnHand()) {
                    lineItem.setQuantityRequested(0L);
                    itemRepository.Update(lineItem);
                }else {
                    itemRepository.Update(lineItem);
                }
            }


        }


    }

    public void insert(VaccineOrderRequisitionLineItem item){
        itemRepository.insert(item);
    }


}
