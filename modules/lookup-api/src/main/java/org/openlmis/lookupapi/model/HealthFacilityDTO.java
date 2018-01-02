package org.openlmis.lookupapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Fac_IDNumber",
        "Name",
        "Comm_FacName",
        "Zone",
        "Region",
        "District",
        "Council",
        "Ward",
        "VillageMtaa",
        "FacilityTypeGroup",
        "FacilityType",
        "OwnershipGroup",
        "Ownership",
        "OperatingStatus",
        "Latitude",
        "Longitude",
        "RegistrationStatus",
        "CreatedAt",
        "UpdatedAt",
        "OSchangeOpenedtoClose",
        "OSchangeClosedtoOperational",
        "PostorUpdate",
        "IL_TransactionIDNumber"
})

@Data
@Setter
@Getter
public class HealthFacilityDTO {

    @JsonProperty("Fac_IDNumber")
    public String facIDNumber;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Comm_FacName")
    public String commFacName;
    @JsonProperty("Zone")
    public String zone;
    @JsonProperty("Region")
    public String region;
    @JsonProperty("District")
    public String district;
    @JsonProperty("Council")
    public String council;
    @JsonProperty("Ward")
    public String ward;
    @JsonProperty("VillageMtaa")
    public String villageMtaa;
    @JsonProperty("FacilityTypeGroup")
    public String facilityTypeGroup;
    @JsonProperty("FacilityType")
    public String facilityType;
    @JsonProperty("OwnershipGroup")
    public String ownershipGroup;
    @JsonProperty("Ownership")
    public String ownership;
    @JsonProperty("OperatingStatus")
    public String operatingStatus;
    @JsonProperty("Latitude")
    public String latitude;
    @JsonProperty("Longitude")
    public String longitude;
    @JsonProperty("RegistrationStatus")
    public String registrationStatus;
    @JsonProperty("CreatedAt")
    public String createdAt;
    @JsonProperty("UpdatedAt")
    public String updatedAt;
    @JsonProperty("OSchangeOpenedtoClose")
    public String oSchangeOpenedtoClose;
    @JsonProperty("OSchangeClosedtoOperational")
    public String oSchangeClosedtoOperational;
    @JsonProperty("PostorUpdate")
    public String postorUpdate;

    @JsonProperty("IL_TransactionIDNumber")
    public String IlIDNumber;

    public String status;

}