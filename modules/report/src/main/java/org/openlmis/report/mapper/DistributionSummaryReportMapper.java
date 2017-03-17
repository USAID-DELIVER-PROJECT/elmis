package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.DistributionSummaryQueryBuilder;
import org.openlmis.report.builder.VaccineReceivedSummaryReportQueryBuilder;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.params.VaccineReceivedSummaryReportParam;
import org.openlmis.report.model.report.DistributionSummaryReport;
import org.openlmis.report.model.report.DistributionSummaryReportFields;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 2/3/17.
 */
@Repository
public interface DistributionSummaryReportMapper {

    @SelectProvider(type = DistributionSummaryQueryBuilder.class, method = "getDistributionQueryData")
    @Options(timeout = 0, useCache = true, flushCache = true)
    public List<DistributionSummaryReportFields> getSummaryReport(@Param("filterCriteria") DistributionSummaryReportParam filterCriteria,
                                                                  @Param("userId") Long userId);

    @SelectProvider(type = DistributionSummaryQueryBuilder.class, method = "getReceivedConsignmentSummaryData")
    @Options(timeout = 0, useCache = true, flushCache = true)
    public List<DistributionSummaryReportFields> getReceivedSummaryReport(@Param("filterCriteria") DistributionSummaryReportParam filterCriteria,
                                                                          @Param("userId")Long userId);

}
