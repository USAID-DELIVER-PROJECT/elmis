package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.serializer.DateDeserializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDistribution extends BaseModel {

    Long toFacilityId;

    Long programId;

    Long fromFacilityId;

    Facility fromFacility;

    String voucherNumber;

    Long periodId;

    Long orderId;

    String status;

    String distributionType;

    String remarks;

    Long total;

    String orderDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = DateDeserializer.class)
    Date distributionDate;

    List<VaccineDistributionLineItem> lineItems;
}