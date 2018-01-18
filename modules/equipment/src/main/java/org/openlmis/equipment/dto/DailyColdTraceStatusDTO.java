/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.equipment.domain.DailyColdTraceStatus;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyColdTraceStatusDTO {

  private String serialNumber;

  @JsonDeserialize(using = DateDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date date;

  private Integer operationalStatusId;

  private BigDecimal minTemp;

  private BigDecimal maxTemp;

  private BigDecimal lowTempEpisode;

  private BigDecimal highTempEpisode;

  private String remarks;

  public void validate() {
    if (new Date().compareTo(date) < 0) {
      throw new DataException("Submissions for future date not allowed.");
    }
  }

  public DailyColdTraceStatus buildEntity() {
    return DailyColdTraceStatus.builder()
        .date(this.date)
        .operationalStatusId(this.operationalStatusId)
        .highTempEpisode(this.highTempEpisode)
        .lowTempEpisode(this.lowTempEpisode)
        .minTemp(this.minTemp)
        .maxTemp(this.maxTemp)
        .remarks(this.remarks)
        .serialNumber(this.serialNumber)
        .build();
  }
}
