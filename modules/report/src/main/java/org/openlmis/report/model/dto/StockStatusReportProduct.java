package org.openlmis.report.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockStatusReportProduct {

    private String product;
    private Integer mos;
    private Double soh;
    private Date lastUpdate;
    private String facilityName;

}
