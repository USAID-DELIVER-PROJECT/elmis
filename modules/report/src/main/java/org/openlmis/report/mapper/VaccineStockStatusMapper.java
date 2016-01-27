package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;

import org.openlmis.report.builder.VaccineStockStatusQueryBuilder;

import org.openlmis.report.model.params.VaccineStockStatusParam;

import org.openlmis.report.model.report.VaccineStockStatusReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineStockStatusMapper {

    @SelectProvider(type = VaccineStockStatusQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<VaccineStockStatusReport> getReport(@Param("filterCriteria") VaccineStockStatusParam params,
                                                    @Param("RowBounds") RowBounds rowBounds);


}
