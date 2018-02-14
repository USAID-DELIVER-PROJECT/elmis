package org.openlmis.rnr.dto;

import lombok.Data;

@Data
public class MsdStatusDTO {

    public int imported;
    public int updated;
    public int ignored;
    public String status;
    public String iL_TransactionIDNumber;

}
