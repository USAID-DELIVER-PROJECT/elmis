package org.openlmis.vaccine.repository.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Pagination;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class VaccineInventoryReportRepository {

    @Autowired
    VaccineInventoryReportMapper mapper;

    public List<Map<String, String>> getDistributionCompletenessReport(Date startDate, Date endDate, Long districtId, Pagination pagination) {
        return mapper.getDistributionCompletenessReport(startDate, endDate, districtId, pagination);
    }

    public Integer getTotalDistributionCompletenessReport(Date startDate, Date endDate, Long districtId) {
        return mapper.getTotalDistributionCompletenessReport(startDate, endDate, districtId);
    }

    public List<Map<String,String>> getDistributedFacilities(Long periodId, Long facilityId,Pagination pagination){
        return mapper.getDistributedFacilities(periodId,facilityId,pagination);
    }

    public Integer getTotalDistributedFacilities(Long periodId, Long facilityId) {
        return mapper.getTotalDistributedFacilities(periodId,facilityId);
    }
}
