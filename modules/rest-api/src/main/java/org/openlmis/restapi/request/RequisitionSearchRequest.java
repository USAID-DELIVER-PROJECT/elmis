package org.openlmis.restapi.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionSearchRequest{
    List<Long> periodIds;
    Long facilityId;
    Long programId;
    Boolean emergency;
    String sourceApplication;
}