package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.stockmanagement.domain.Lot;

/**
 * Created by chrispinus on 10/22/15.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDistributionLineItemLot extends BaseModel {

    private Long distributionLineItemId;

    private Long lotId;

    private Lot lot;

    private Long quantity;

    private Integer vvmStatus;
}
