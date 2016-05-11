/**Electronic Logistics Management Information System(eLMIS)is a supply chain management system for health commodities in a developing country setting.
        *
 *Copyright(C)2015 Clinton Health Access Initiative(CHAI)/MoHCDGEC Tanzania.
        *
 *This program is free software:you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation,either version 3of the License,or(at your option)any later version.

 *This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY;without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU Affero General Public License for more details.
 **/

package org.openlmis.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchExpirationNotificationDTO {

    private String product;
    private Date expirationDate;
    private String lotNumber;
    private String ManufacturerName;
    private Integer soh;
    private Integer quantityOnHand;

}
