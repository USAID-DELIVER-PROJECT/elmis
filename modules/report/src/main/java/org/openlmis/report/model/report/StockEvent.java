package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

/**
 * Created by hassan on 6/22/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockEvent implements ResultRow{


   // private String region;

    private String storeName;

    private Integer maximumStock;

    private Integer minimumStock;

    private String product;

    private Integer soh;

    private String days;

    private String monthOccured;

    private String dayOccured;



}
