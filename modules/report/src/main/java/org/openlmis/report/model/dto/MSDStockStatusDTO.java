package org.openlmis.report.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.openlmis.core.domain.BaseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "geographiczoneId",
        "productid",
        "periodId",
        "onHandDate",
        "receivedQuantity",
        "issuedQuantity",
        "onHandQuantity",
        "mos"
})
@Data
public class MSDStockStatusDTO extends BaseModel {

    @JsonProperty("geographiczoneId")
    private Integer geographiczoneId;
    @JsonProperty("productid")
    private Integer productid;
    @JsonProperty("periodId")
    private Integer periodId;
    @JsonProperty("onHandDate")
    private String onHandDate;
    @JsonProperty("receivedQuantity")
    private Integer receivedQuantity;
    @JsonProperty("issuedQuantity")
    private Integer issuedQuantity;
    @JsonProperty("onHandQuantity")
    private Integer onHandQuantity;
    @JsonProperty("mos")
    public Integer mos;

    private String productCode;
    private String productName;
    private String msdZone;
    private String color;
    private String geoCode;


}