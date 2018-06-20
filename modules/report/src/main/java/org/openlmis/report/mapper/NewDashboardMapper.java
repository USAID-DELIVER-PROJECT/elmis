package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.DashboardOrderFillRateBuilder;
import org.openlmis.report.builder.DashboardReportingRateQueryBuilder;
import org.openlmis.report.builder.DashboardStockStatusQueryBuilder;
import org.openlmis.report.model.dto.DashOrderFillRate;
import org.openlmis.report.model.dto.ReportingRate;
import org.openlmis.report.model.dto.StockStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewDashboardMapper {
    @SelectProvider(type = DashboardReportingRateQueryBuilder.class, method = "getQuery")
    List<ReportingRate> getReportingRate(Long zoneId, Long periodId, Long programId);
    @SelectProvider(type = DashboardStockStatusQueryBuilder.class, method = "getQuery")
    List<StockStatus> getStockStaus(Long zoneId, Long periodId, Long programId);
    @SelectProvider(type = DashboardOrderFillRateBuilder.class, method = "getQuery")
    List<DashOrderFillRate> getItemFillRate(Long zoneId, Long periodId, Long programId);
}
