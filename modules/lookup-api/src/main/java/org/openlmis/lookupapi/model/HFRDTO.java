package org.openlmis.lookupapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
public class HFRDTO {
    private String IL_TransactionIDNumber;
    private String status;
}
