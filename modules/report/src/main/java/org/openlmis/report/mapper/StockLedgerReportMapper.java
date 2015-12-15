package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.StockLedgerReportQueryBuilder;
import org.openlmis.report.model.params.StockLedgerReportParam;
import org.openlmis.report.model.report.StockLedgerReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLedgerReportMapper {

    @SelectProvider(type = StockLedgerReportQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<StockLedgerReport> getReport(@Param("filterCriteria") StockLedgerReportParam params,
                                             @Param("RowBounds") RowBounds rowBounds);


}
