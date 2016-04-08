package org.openlmis.equipment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColdChainEquipmentTemperatureStatusDTO extends BaseModel{
    private Boolean skipped = false;

    private Long reportId;
    private Long equipmentInventoryId;

    private Float minTemp;
    private Float maxTemp;

    private Float minEpisodeTemp;
    private Float maxEpisodeTemp;

    private String remarks;

    private Long operationalStatusId;
    //DTO properties
    private String equipmentName;
    private String type;
    private String model;
    private String energySource;
    private String serial;
    private  String location_value;



}
