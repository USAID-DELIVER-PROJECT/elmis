package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.DashboardReportingRateQueryBuilder;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.model.dto.ReportingRate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewDashboardMapper {
    @SelectProvider(type = DashboardReportingRateQueryBuilder.class, method = "getQuery")
    List<ReportingRate> getReportingRate(Long zoneId, Long periodId, Long programId);
}
