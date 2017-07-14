package org.openlmis.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;

import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by hassan on 7/5/17.
 */




@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogTagDTO extends BaseModel {

    @JsonProperty(value = "logdate")
    private String logDate;

    @JsonProperty(value = "logtime")
    private String logTime;

    /*private String sampleId;*/

    private String temperature;

    private String events;

    @JsonProperty(value = "serialnumber")
    private String serialNumber;

    private Date createdDate;

    public LogTagDTO(String logDate, String logTime, String temperature, String events, String serialnumber) {

        this.logDate = logDate;
        this.logTime = logTime;
        this.temperature = temperature;
        this.events = events;
        this.serialNumber = serialnumber;

    }
/*
    public Date getLogDate() {
        try {
            DateFormat df = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");

            return df.parse(String.valueOf(this.logDate));
        }catch (Exception e){
            return new Date();
        }
    }
    DateFormat df = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
    public Date getLogTime()  {
        try {
            return df.parse(String.valueOf(this.logTime));
        }catch (Exception e){
            return new Date();
        }
    }*/

    public void validateMandatoryFields() {
        if (isBlank(this.temperature)  || isBlank(this.temperature)) {
            throw new DataException("error.mandatory.fields.missing");
        }
    }

}
