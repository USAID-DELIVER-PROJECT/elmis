package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;

/**
 * Created by hassan on 2/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColdChainEquipmentReportParam extends BaseParam
        implements ReportParameter {

    private Long program;
    private String facilityLevel;
    private String facilityIds;
}
