package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.DistributionSummaryQueryBuilder;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.report.DistributionSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 2/3/17.
 */
@Repository
public interface DistributionSummaryReportMapper {

    @SelectProvider(type=DistributionSummaryQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<DistributionSummaryReport> getReportData(
            @Param("filterCriteria") DistributionSummaryReportParam params
            , @Param("RowBounds") RowBounds rowBounds );

}
