package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.VaccineReceivedSummaryReportQueryBuilder;
import org.openlmis.report.model.params.VaccineReceivedSummaryReportParam;
import org.openlmis.report.model.report.DistributionSummaryReportFields;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 2/19/17.
 */
@Repository
public interface VaccineReceivedSummaryReportMapper {

    @SelectProvider(type = VaccineReceivedSummaryReportQueryBuilder.class, method = "getReceivedConsignmentSummaryData")
    @Options(timeout = 0, useCache = true, flushCache = true)
    public List<DistributionSummaryReportFields> getReceivedSummaryReport(@Param("filterCriteria") VaccineReceivedSummaryReportParam filterCriteria,
                                                                          @Param("userId")Long userId);

}
