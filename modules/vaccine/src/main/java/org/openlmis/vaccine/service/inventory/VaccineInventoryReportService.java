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
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.vaccine.repository.inventory.VaccineInventoryReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@NoArgsConstructor
public class VaccineInventoryReportService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    @Autowired
    VaccineInventoryReportRepository repository;
    @Autowired
    private ProcessingPeriodService periodService;


    public String formatDate(Date requestDate) {
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date = "";
        date = outputFormat.format(requestDate);
        return date;
    }

    public List<Map<String, String>> getDistributionCompletenessReport(String year, String period, Long districtId, String type, Pagination pagination) {

        String newStartDate = " ";
        String newEndDate = " ";
        if (Long.valueOf(period) == 0L) {

            newStartDate = year + "-01-01";
            newEndDate = year + "-12-31";

        } else {

            ProcessingPeriod processingPeriod = periodService.getById(Long.valueOf(period));

            newStartDate = String.valueOf(formatDate(processingPeriod.getStartDate()));
            newEndDate = String.valueOf(formatDate(processingPeriod.getEndDate()));
        }
        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(newStartDate).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(newEndDate).toDate();

        return repository.getDistributionCompletenessReport(startDate, endDate, districtId, type, pagination);
    }

    public List<Map<String, String>> getDistributedFacilities(Long periodId, Long facilityId, String type, Pagination pagination) {
        return repository.getDistributedFacilities(periodId, facilityId, type, pagination);
    }


    public Integer getTotalDistributionCompletenessReport(String year, String period, Long districtId) {

        String newStartDate = " ";
        String newEndDate = " ";
        if (Long.valueOf(period) == 0L) {

            newStartDate = year + "-01-01";
            newEndDate = year + "-12-31";

        } else {

            ProcessingPeriod processingPeriod = periodService.getById(Long.valueOf(period));

            newStartDate = String.valueOf(formatDate(processingPeriod.getStartDate()));
            newEndDate = String.valueOf(formatDate(processingPeriod.getEndDate()));
        }
        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(newStartDate).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(newEndDate).toDate();
        return repository.getTotalDistributionCompletenessReport(startDate, endDate, districtId);
    }

    public Integer getTotalDistributedFacilities(Long periodId, Long facilityId, String type) {
        return repository.getTotalDistributedFacilities(periodId, facilityId, type);
    }
}
