package org.openlmis.vaccine.domain.VaccineOrderRequisition;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ProductService;
import org.openlmis.stockmanagement.domain.StockCard;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.openlmis.stockmanagement.service.StockCardService;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.openlmis.vaccine.dto.StockRequirements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineOrderRequisition extends BaseModel {
    public static final SimpleDateFormat form = new SimpleDateFormat("MM-dd-YYYY");
    private Long periodId;
    private Long programId;
    private ProductCategory productCategory;
    private VaccineOrderStatus status;
    private ProcessingPeriod period;
    private Facility facility;
    private Program program;
    private Long supervisoryNodeId;
    private Long facilityId;
    private String orderDate;
    private boolean emergency;
    private String reason;
    private List<VaccineOrderRequisitionLineItem> lineItems;
    private List<VaccineOrderRequisitionStatusChange> statusChanges;
    private List<VaccineOrderRequisitionColumns> columnsList;
    private List<OrderRequisitionStockCardDTO> stockCards;


    public void initiateOrder(List<StockRequirements> requirementsList, ProductService service, StockCardMapper stockCardMapper) {
        lineItems = new ArrayList<>();


        for (StockRequirements stockRequirements : requirementsList) {


            if (stockRequirements.getIsaValue() != null) {

                VaccineOrderRequisitionLineItem lineItem = new VaccineOrderRequisitionLineItem();
                lineItem.setOrderId(id);
                lineItem.setProductId(stockRequirements.getProductId());
                lineItem.setProductName(stockRequirements.getProductName());
                lineItem.setMaxmonthsofstock(stockRequirements.getMaxMonthsOfStock());

                lineItem.setOverriddenisa(stockRequirements.getIsaValue());
                lineItem.setEop(stockRequirements.getEop());

                Product p = service.getById(stockRequirements.getProductId());
                StockCard s = stockCardMapper.getByFacilityAndProduct(stockRequirements.getFacilityId(), p.getCode());
                if (s != null){
                    lineItem.setStockOnHand(s.getTotalQuantityOnHand());
                }
                else {
                    lineItem.setStockOnHand(0L);
                }
                lineItem.setMinMonthsOfStock(stockRequirements.getMinMonthsOfStock());
                lineItem.setOrderedDate(form.format(new Date()));
                lineItem.setBufferStock(stockRequirements.getBufferStock());

                lineItem.setMaximumStock(stockRequirements.getMaximumStock());
                lineItem.setReOrderLevel(stockRequirements.getReorderLevel());
                lineItem.setCategory(stockRequirements.getProductCategory());
                lineItem.setPopulation(stockRequirements.getPopulation());

                lineItem.setIsa(stockRequirements.getIsa());


                lineItem.setMinimumStock(stockRequirements.getMinimumStock());
                lineItem.setAnnualNeed(stockRequirements.getAnnualNeed());
                lineItem.setBufferStock(stockRequirements.getBufferStock());

                lineItems.add(lineItem);
            }

        }


    }


}
