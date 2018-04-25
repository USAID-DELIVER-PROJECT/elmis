package org.openlmis.rnr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({

        "Plant",
        "PartNum",
        "UOM",
        "PartDescription",
        "OnHandQty",
        "Date",
        "MonthOfStock"
})
@Setter
@Getter
public class MSDStockStatusDTO extends BaseModel{

    @JsonProperty("Plant")
    public String facilityCode;

    @JsonProperty("PartNum")
    public String productCode;

    @JsonProperty("UOM")
    public String uom;

    @JsonProperty("PartDescription")
    public String partDescription;

    @JsonProperty("Date")
    public String onHandDate;

    //public Date onHandDate;

    @JsonProperty("MonthOfStock")
    public Integer mos;

    @JsonProperty("OnHandQty")
    public String onHandQuantity;

    public String status;

}

