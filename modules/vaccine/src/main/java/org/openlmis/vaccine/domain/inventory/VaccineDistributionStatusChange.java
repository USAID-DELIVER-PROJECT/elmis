package org.openlmis.vaccine.domain.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineDistributionStatusChange extends BaseModel {

    private Long distributionId;

    private String status;

    public VaccineDistributionStatusChange(VaccineDistribution distribution, Long userId) {

        distributionId = distribution.getId();
        status = distribution.getStatus();
        createdBy = userId;
        modifiedBy = userId;
    }


}
