package org.openlmis.vaccine.domain.VaccineOrderRequisition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;

import java.util.Date;

/**
 * Created by hassan on 11/29/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineOnTimeInFull extends BaseModel{

    private Product product;

    private Integer quantityRequested;

    private Integer quantityReceived;

    private Date receivedDate;

    private Date requestedDate;

    private Long productId;


}
