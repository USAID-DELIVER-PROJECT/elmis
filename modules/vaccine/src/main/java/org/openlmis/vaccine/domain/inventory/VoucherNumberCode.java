package org.openlmis.vaccine.domain.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherNumberCode {

    Long facilityId;
    String facilityName;
    String national;
    String region;
    String district;
}
