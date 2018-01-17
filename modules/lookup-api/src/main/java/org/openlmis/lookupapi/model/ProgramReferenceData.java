package org.openlmis.lookupapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;

import org.openlmis.report.model.dto.Facility;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.dto.Program;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProgramReferenceData {
    private Facility facility;
    private Program program;
    private List<FacilityTypeApprovedProduct> facilityTypeApprovedProductList;
    private  List<ProcessingPeriod> processingPeriodList;
}
