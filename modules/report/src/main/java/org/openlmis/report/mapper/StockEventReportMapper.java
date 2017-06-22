package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.StockEventQueryBuilder;
import org.openlmis.report.model.params.StockEventParam;
import org.openlmis.report.model.report.StockEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 6/22/17.
 */
@Repository
public interface StockEventReportMapper {

    @SelectProvider(type = StockEventQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<StockEvent> getReport(
            @Param("filterCriteria") StockEventParam params,
            @Param("userId") Long userId,
            @Param("RowBounds") RowBounds rowBounds);
}
