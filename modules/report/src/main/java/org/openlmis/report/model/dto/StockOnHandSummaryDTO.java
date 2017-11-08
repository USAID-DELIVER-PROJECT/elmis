package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.utils.DateUtil;

import java.util.Date;

/**
 * Created by hassan on 9/10/17.
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockOnHandSummaryDTO {

    private String product;
    private String district;
    private Integer soh;
    private Date lastUpdate;
    private String facilityName;
    private Double monthlyStock;
    private String facilityType;
    private int facilityId;
    private int productId;

    private Long isaValue;

    private Float mos;

    private String color;

    private Integer adequacy;

    private String region;

    private Integer total;

    private Integer adequacy2;

    private Integer adequacy3;
    //private JSONPObject products;
    private Integer bufferStock;

    public String getLastUpdate(){
        return DateUtil.getFormattedDate(this.lastUpdate, "dd-MM-yyyy");
    }

}
