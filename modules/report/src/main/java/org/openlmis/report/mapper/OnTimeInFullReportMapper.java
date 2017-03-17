package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.OnTimeInFullQueryBuilder;
import org.openlmis.report.model.params.OnTimeInFullReportParam;
import org.openlmis.report.model.report.OnTimeInFullReport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 1/29/17.
 */
@Repository
public interface OnTimeInFullReportMapper {

    @SelectProvider(type = OnTimeInFullQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<OnTimeInFullReport> getReport(@Param("filterCriteria") OnTimeInFullReportParam params,
                                              @Param("userId") Long userId,
                                              @Param("RowBounds") RowBounds rowBounds);





}
