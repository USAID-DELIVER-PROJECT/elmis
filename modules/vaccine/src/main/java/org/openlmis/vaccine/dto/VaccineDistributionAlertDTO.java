package org.openlmis.vaccine.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDistributionAlertDTO {

  private String toFacilityName;
  private String fromFacilityName;
  private Long orderId;
  private String voucherNumber;
  private Date  distributionDate;
  private String modifiedBy;
  private String status;
  private String cellPhone;
  private String orderDate;


}
