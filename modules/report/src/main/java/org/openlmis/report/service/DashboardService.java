package org.openlmis.report.service;

import org.openlmis.report.mapper.NewDashboardMapper;
import org.openlmis.report.mapper.lookup.DashboardMapper;
import org.openlmis.report.model.dto.DashOrderFillRate;
import org.openlmis.report.model.dto.ReportingRate;
import org.openlmis.report.model.dto.StockStatus;
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
    public List<StockStatus>  getStockStaus(Long zoneId, Long periodId, Long programId) {
        return  dashboardMapper.getStockStaus(zoneId,periodId,programId);
    }
    public List<DashOrderFillRate>  getItemFillRate(Long zoneId, Long periodId, Long programId) {
        return  dashboardMapper.getItemFillRate(zoneId,periodId,programId);
    }
}
