package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccineOrderRequisitionLineItem extends BaseModel {

    private Long orderId;
    private Long productId;
    private String productName;
    private Product product;
    private ProductCategory productCategory;
    private Integer displayOrder;
    private Integer maximumStock;
    private Integer minimumStock;
    private Integer reOrderLevel;
    private Integer bufferStock;
    private Long stockOnHand;
    private Long quantityRequested;
    private String orderedDate;
    private String category;
    private ISA isa;

    Integer annualNeed;
    Integer quarterlyNeed;

    //will be used for calculation purpose on font end

    private Integer overriddenisa;
    private Double maxmonthsofstock;
    private Double minMonthsOfStock;
    private Double eop;
    Long population;


}
