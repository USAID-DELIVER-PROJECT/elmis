/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.inventory;

import lombok.NoArgsConstructor;
import org.joda.time.format.DateTimeFormat;
import org.openlmis.core.domain.Pagination;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class VaccineInventoryReportService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    @Autowired
    VaccineInventoryReportRepository repository;

    public List<Map<String, String>> getDistributionCompletenessReport(String periodStart, String periodEnd, Long districtId, Pagination pagination) {
        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();

        return repository.getDistributionCompletenessReport(startDate, endDate, districtId, pagination);
    }

    public List<Map<String,String>> getDistributedFacilities(Long periodId, Long facilityId,Pagination pagination){
        return repository.getDistributedFacilities(periodId,facilityId,pagination);
    }


    public Integer getTotalDistributionCompletenessReport(String periodStart, String periodEnd, Long districtId) {
        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();

        return repository.getTotalDistributionCompletenessReport(startDate, endDate, districtId);
    }

    public Integer getTotalDistributedFacilities(Long periodId, Long facilityId) {
        return repository.getTotalDistributedFacilities(periodId,facilityId);
    }
}
