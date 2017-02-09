package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

/**
 * Created by hassan on 2/2/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MinMaxVaccineReport implements ResultRow {

    private String region;

    private String storeName;

    private Integer maximumStock;

    private Integer minimumStock;

    private String product;

    private Integer soh;

}
