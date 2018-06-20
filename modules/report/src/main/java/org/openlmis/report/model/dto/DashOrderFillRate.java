package org.openlmis.report.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashOrderFillRate {
    private String name;
    private float prev;
    private float current;
    private String status;
}
