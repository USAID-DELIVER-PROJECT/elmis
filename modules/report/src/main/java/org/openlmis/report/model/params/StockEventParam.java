package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;

/**
 * Created by hassan on 6/22/17.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockEventParam extends BaseParam implements ReportParameter{

    private Long product;
    private Long period;
    private Long district;
    private Long year;
    private Long facilityId;
    private Long monthInNumber;

}
