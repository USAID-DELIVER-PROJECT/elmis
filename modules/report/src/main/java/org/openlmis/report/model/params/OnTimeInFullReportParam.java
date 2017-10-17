package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by hassan on 1/29/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnTimeInFullReportParam extends BaseParam {

    private Long program;


    private Long period;

    private String productCategory;

    private String product;

    private Long facilityType;

    private Long facility;

    private Long zone;

    private String facilityLevel;

    private String startDate;

    private String endDate;

    private String products;

    private String facilityIds;

    private Long year;

    private String periodString;

    private Integer periods;

    private String periodStart;

    private String periodEnd;


}
