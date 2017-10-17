package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.vaccine.dto.VaccineDistributionDTO;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.PanelUI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class VaccineInventoryReportRepository {

    @Autowired
    VaccineInventoryReportMapper mapper;

    public List<Map<String, String>> getDistributionCompletenessReport(Date startDate, Date endDate, Long districtId,String type, Pagination pagination) {
        return mapper.getDistributionCompletenessReport(startDate, endDate, districtId,type, pagination);
    }

    public Integer getTotalDistributionCompletenessReport(Date startDate, Date endDate, Long districtId) {
        return mapper.getTotalDistributionCompletenessReport(startDate, endDate, districtId);
    }

    public List<Map<String,String>> getDistributedFacilities(Long periodId, Long facilityId, String type, Pagination pagination){
        return mapper.getDistributedFacilities(periodId,facilityId,type);
    }

    public Integer getTotalDistributedFacilities(Long periodId, Long facilityId, String type) {
        return mapper.getTotalDistributedFacilities(periodId,facilityId,type);
    }
    public VaccineDistributionDTO getDistributionPeriod(Long facilityId, String distributionDate){
        return mapper.getDistributionPeriod(distributionDate,facilityId);
    }
}
