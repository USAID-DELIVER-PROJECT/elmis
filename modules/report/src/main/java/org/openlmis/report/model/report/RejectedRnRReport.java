package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectedRnRReport implements ResultRow {

    private Integer rejectedCount;
    private String districtName;
    private String regionName;
    private String zoneName;
    private String month;
}
