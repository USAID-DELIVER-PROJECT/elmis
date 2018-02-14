package org.openlmis.lookupapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@JsonPropertyOrder({
        "IL_IDNumber",
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
public class MSDStockDTO extends BaseModel{

    @JsonProperty("IL_IDNumber")
    public String ilId;

    @JsonProperty("Plant")
    public String plant;

    @JsonProperty("PartNum")
    public String partNum;

    @JsonProperty("UOM")
    public String uom;

    public Long facilityId;
    public Long productId;

    @JsonProperty("PartDescription")
    public String partDescription;

    @JsonProperty("Date")
    public String date;

    public Date onHandDate;

    @JsonProperty("MonthOfStock")
    public Integer mos;

    @JsonProperty("OnHandQty")
    public Integer onHandQuantity;

}
