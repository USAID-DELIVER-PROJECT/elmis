/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ISA;
import org.openlmis.core.dto.IsaDTO;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;


@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockRequirements
{
  Long facilityId;
  String facilityCode;
  Long productId;
  String productCategory;
  String productName;

  ISA isa;

  Double minMonthsOfStock;
  Double maxMonthsOfStock;
  Double eop;

  Long population;

  Integer annualNeed;

  Integer quarterlyNeed;

  Double MinimumStock;

  Double MaximumStock;

  Double ReorderLevel;

  IsaDTO isaDTO;

  int isaValue = 0;

  Double bufferStock;

  public Integer getIsaValue()
  {
    if(isa == null || population == null)
      return null;
    return isa.calculate(population);
  }

  public Double getMinimumStock()
  {
    if(getIsaValue() == null || minMonthsOfStock == null)
      return null;
    Double value = getIsaValue() * minMonthsOfStock;
    return MinimumStock = value;
  }

  public Double getMaximumStock()
  {
    if(getIsaValue() == null || maxMonthsOfStock == null)
      return null;
    Double value = getIsaValue() * maxMonthsOfStock;
    return MaximumStock= value;
  }

  public Double getReorderLevel()
  {
    if(getIsaValue() == null || eop == null)
      return null;
    return getIsaValue() * eop;
  }

  public Integer getAnnualNeed(){
    if(getIsaValue() == null)
      return null;
    return getIsaValue() * 12;
  }

  public Integer getQuarterlyNeed(){
    if (getAnnualNeed() == null)
      return null;
    return getAnnualNeed() / 4;
  }


  public Double getBufferStock(){
      if(getQuarterlyNeed() == null && isa == null)
          return null;
      return Double.valueOf((getQuarterlyNeed() * isa.getBufferPercentage()) / 100);
  }




  public String getJSON()
  {
    /* Perhaps use a Map instead, and then let Jackson turn it into JSON.
    (The benefit is that we wouldn't have to worry about maulually escaping strings, etc, as
    Alternatively, look into "Jackson Views." They may give you good control over what is and
    isn't returned to the client. */
   StringBuilder builder = new StringBuilder();
    builder.append("{");

    builder.append("\"facilityId\": ");
    builder.append(facilityId);

    builder.append(", \"facilityCode\": ");
    builder.append("\""+facilityCode +"\"");

    builder.append(", \"productId\": ");
    builder.append(productId);

    builder.append(", \"productName\": ");
    builder.append("\""+productName +"\"");

    builder.append(", \"productCategory\": ");
    builder.append("\""+productCategory +"\"");

    builder.append(", \"population\": ");
    builder.append(population);

    builder.append(", \"minMonthsOfStock\": ");
    builder.append(minMonthsOfStock);
    builder.append(", \"maxMonthsOfStock\": ");
    builder.append(maxMonthsOfStock);
    builder.append(", \"eop\": ");
    builder.append(eop);

    builder.append(", \"isaCoefficients\": {");
    if(isa != null) {
      builder.append("\"whoRatio\": ");
      builder.append(isa.getWhoRatio());

      builder.append(", \"dosesPerYear\": ");
      builder.append(isa.getDosesPerYear());

      builder.append(", \"wastageFactor\": ");
      builder.append(isa.getWastageFactor());

      builder.append(", \"bufferPercentage\": ");
      builder.append(isa.getBufferPercentage());

      builder.append(", \"minimumValue\": ");
      builder.append(isa.getMinimumValue());

      builder.append(", \"maximumValue\": ");
      builder.append(isa.getMaximumValue());

      builder.append(", \"adjustmentValue\": ");
      builder.append(isa.getAdjustmentValue());
    }
    builder.append("}");

    builder.append(", \"isaValue\": ");
    builder.append(getIsaValue());

    builder.append(", \"minimumStock\": ");
    builder.append(getMinimumStock());

    builder.append(", \"maximumStock\": ");
    builder.append(getMaximumStock());

    builder.append(", \"reorderLevel\": ");
    builder.append(getReorderLevel());

    builder.append(", \"annualNeed\": ");
    builder.append(getAnnualNeed());

    builder.append(", \"quarterlyNeed\": ");
    builder.append(getQuarterlyNeed());


    builder.append("}");

    return builder.toString();
  }


  //If anyone likes this pattern that this method is intended to support, the method should be generalized and moved elsewhere
  public static String getJSONArray(List<StockRequirements> items)
  {
    StringBuilder builder = new StringBuilder("[");
    for(int i=0; i<items.size(); i++)
    {
      builder.append(items.get(i).getJSON());
      if(i < items.size() - 1)
        builder.append(",");
    }
    builder.append("]");

    return builder.toString();
  }


}
