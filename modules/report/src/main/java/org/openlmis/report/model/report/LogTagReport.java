package org.openlmis.report.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

import java.util.Date;

/**
 * Created by hassan on 7/5/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class LogTagReport implements ResultRow {

    private String logDate;

    private String logTime;

    private String temperature;

    private String events;

    private String serialNumber;

    private Date createdDate;

    private String storeName;

    private String name;

    private String route;

}
