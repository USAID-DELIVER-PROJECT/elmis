package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.MinMaxVaccineReportQueryBuilder;
import org.openlmis.report.model.params.MinMaxVaccineReportParam;
import org.openlmis.report.model.report.MinMaxVaccineReport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 2/2/17.
 */


@Repository
public interface MinMaxVaccineReportMapper {

    @SelectProvider(type = MinMaxVaccineReportQueryBuilder.class, method = "getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<MinMaxVaccineReport> getReport(
            @Param("filterCriteria") MinMaxVaccineReportParam params,
            @Param("userId") Long userId,
            @Param("RowBounds") RowBounds rowBounds);



}
