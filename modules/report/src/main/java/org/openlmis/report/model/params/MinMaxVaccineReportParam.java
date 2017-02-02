package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by hassan on 2/2/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinMaxVaccineReportParam extends BaseParam{

    private Long year;

    private String productCategory;

    private String product;

    private Long facilityType;

    private Long facility;

    private Long zone;

    private String facilityLevel;

    private Long program;

    private String facilityIds;

}
