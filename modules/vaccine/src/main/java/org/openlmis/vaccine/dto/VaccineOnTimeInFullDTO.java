package org.openlmis.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;

import java.util.Date;

/**
 * Created by hassan on 12/2/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineOnTimeInFullDTO extends BaseModel{

    private Product product;

    private Integer quantityRequested;

    private Integer quantityReceived;

    private Date receivedDate;

    private Date requestedDate;

    private Long productId;

    private String productName;

    private Integer gap;

    private String onFull;

    private ProductCategory productCategory;






}
