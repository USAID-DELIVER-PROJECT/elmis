package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.LogTagQueryBuilder;
import org.openlmis.report.model.params.LogTagParam;
import org.openlmis.report.model.report.LogTagReport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 7/5/17.
 */
@Repository
public interface LogTagReportMapper {

    @SelectProvider(type = LogTagQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<LogTagReport> getReport(
            @Param("filterCriteria") LogTagParam params,
            @Param("userId") Long userId,
            @Param("RowBounds") RowBounds rowBounds);
}
