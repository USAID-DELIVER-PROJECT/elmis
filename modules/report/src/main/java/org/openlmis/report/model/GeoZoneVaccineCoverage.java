package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by hassan on 11/6/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoZoneVaccineCoverage {


    private long id;

    private String name;

    private String geometry;

    private float expected;

    private float total;

    private float ever;

    private float period;

    private String coverageClassification;

    private Integer coveragePercentage;

    private Integer vaccinated;

    private Integer monthlyEstimate;

    private float mos;
    private String region;

    private Integer planned;

    private Integer outReachSession;

    private Integer prevOutReachSession;

    private Integer prevPlanned;

    private Integer prevSOH;

    private Integer soh;

    private float prevMOS;

    private Integer prevExpected;

    private Integer prevPeriod;
}
