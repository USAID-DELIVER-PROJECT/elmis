package org.openlmis.rnr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class MSDStockStatusCollectionDTO{

    @Getter
    @Setter
    private List<MSDStockStatusDTO> values;

    @Getter
    @Setter
    private String iLIDNumber;
}
