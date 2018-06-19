package org.openlmis.report.service;

import org.openlmis.report.mapper.NewDashboardMapper;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.model.dto.ReportingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private NewDashboardMapper dashboardMapper;
    public List<ReportingRate>  getReportingRate(Long zoneId, Long periodId, Long programId) {
        return  dashboardMapper.getReportingRate(zoneId,periodId,programId);
    }
}
