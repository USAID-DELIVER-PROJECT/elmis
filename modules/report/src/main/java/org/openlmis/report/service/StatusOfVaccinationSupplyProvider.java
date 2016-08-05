package org.openlmis.report.service;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.report.vaccine.StatusOfVaccinationSuppliesReceivedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class StatusOfVaccinationSupplyProvider extends ReportDataProvider {
    @Autowired
    private  StatusOfVaccinationSupplyService statusOfVaccinationSupplyService;
    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filter, Map<String, String[]> sorter, int page, int pageSize) {
        List<StatusOfVaccinationSuppliesReceivedReport> suppliesReceivedReportList= new ArrayList<>();
        StatusOfVaccinationSuppliesReceivedReport statusOfVaccinationSuppliesReceivedReport=this.statusOfVaccinationSupplyService.loadStatusOfVaccineSupplyReport(filter);
        suppliesReceivedReportList.add(statusOfVaccinationSuppliesReceivedReport);
        return suppliesReceivedReportList;
    }
}
