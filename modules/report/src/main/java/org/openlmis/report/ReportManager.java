/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report;

import lombok.*;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.openlmis.report.exception.ReportException;
import org.openlmis.report.exporter.ReportExporter;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages both interactive web and jasper report integration
 */
@NoArgsConstructor
@AllArgsConstructor
public class ReportManager {

    private ReportExporter reportExporter;

    private Map<String,Report> reportsByKey;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConfigurationSettingService configurationService;

    public ReportManager(ReportExporter reportExporter, List<Report> reports) {
        this(reports);
        this.reportExporter = reportExporter;
    }

    private ReportManager(List<Report> reports){
        if(reports != null){
            reportsByKey = new HashMap<>();
            for (Report report: reports){
                reportsByKey.put(report.getReportKey(),report);
            }
        }
    }

    /**
     *
     * @param report
     * @param params
     * @param outputOption
     * @param response
     */
    public void showReport(Integer userId, Report report, Map<String, String[]> params, ReportOutputOption outputOption, HttpServletResponse response){

       if (report == null){
           throw new ReportException("invalid report");
       }

        List<? extends ResultRow> dataSource = report.getReportDataProvider().getResultSet(params);
        Map<String, Object> extraParams = getReportExtraDataSourceParams(userId, params, outputOption, dataSource, report);

       InputStream reportInputStream =  this.getClass().getClassLoader().getResourceAsStream(report.getTemplate()) ;
        reportExporter.exportReport(reportInputStream, extraParams, dataSource, outputOption, response);
    }

    public ByteArrayOutputStream exportReportBytesStream(Integer userId, Report report, Map<String, String[]> params, ReportOutputOption outputOption){

        if (report == null){
            throw new ReportException("invalid report");
        }

        List<? extends ResultRow> dataSource = report.getReportDataProvider().getResultSet(params);
        Map<String, Object> extraParams = getReportExtraDataSourceParams(userId, params, outputOption, dataSource, report);

        // Read the report template from file.
        InputStream reportInputStream =  this.getClass().getClassLoader().getResourceAsStream(report.getTemplate()) ;

        return reportExporter.exportReportBytesStream(reportInputStream, extraParams, dataSource, outputOption);

    }

    private  Map<String, Object> getReportExtraDataSourceParams(Integer userId, Map<String, String[]> params , ReportOutputOption outputOption, List<? extends ResultRow> dataSource, Report report){

        User currentUser = userService.getById(Long.parseLong(String.valueOf(userId)));
        report.getReportDataProvider().setUserId(userId.longValue());

        Map<String, Object> extraParams = getReportExtraParams(report, currentUser.getFirstName() + " " + currentUser.getLastName(), outputOption.name(), params ) ;

        //Setup message for a report when there is no data found
        if(dataSource != null && dataSource.size() == 0){

            if(extraParams != null){
                extraParams.put(Constants.REPORT_MESSAGE_WHEN_NO_DATA, configurationService.getByKey(Constants.REPORT_MESSAGE_WHEN_NO_DATA).getValue());
            }else {
                extraParams = new HashMap<String, Object>();
                extraParams.put(Constants.REPORT_MESSAGE_WHEN_NO_DATA, configurationService.getByKey(Constants.REPORT_MESSAGE_WHEN_NO_DATA).getValue());
            }
        }

        return extraParams;
    }

    /**
     *
     * @param reportKey
     * @param params
     * @param outputOption
     * @param response
     */
    public void showReport(Integer userId, String reportKey, Map<String, String[]> params, ReportOutputOption outputOption, HttpServletResponse response){
        showReport(userId, getReportByKey(reportKey), params, outputOption, response);
    }

    /**
     *
     * @param userId
     * @param reportKey
     * @param params
     * @param outputOption
     * @return ByteArrayOutputStream
     */
    public ByteArrayOutputStream exportReportBytesStream(Integer userId, String reportKey, Map<String, String[]> params, String outputOption){

        switch (outputOption.toUpperCase()) {
            case "CSV":
                return exportReportBytesStream(userId, getReportByKey(reportKey), params, ReportOutputOption.CSV);
            case "XLS":
                return exportReportBytesStream(userId, getReportByKey(reportKey), params, ReportOutputOption.XLS);
            case "HTML":
                return exportReportBytesStream(userId, getReportByKey(reportKey), params, ReportOutputOption.HTML);
            case "PDF":
            default:
                return exportReportBytesStream(userId, getReportByKey(reportKey), params, ReportOutputOption.PDF);
        }
    }


     /**
     * Used to extract extra parameters that are used by report header and footer.
     *
      *
      * @param report
      * @param outputOption
      * @param filterCriteria
      * @return
     */
    private Map<String, Object> getReportExtraParams(Report report, String generatedBy, String outputOption, Map<String, String[]> filterCriteria){

        if (report == null) {
            return null;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.REPORT_NAME, report.getName());
        params.put(Constants.REPORT_ID, report.getId());
        params.put(Constants.REPORT_TITLE, messageService.message(report.getTitle()));
        params.put(Constants.REPORT_SUB_TITLE, report.getSubTitle());
        params.put(Constants.REPORT_VERSION, report.getVersion());
        params.put(Constants.REPORT_OUTPUT_OPTION, outputOption);
        ConfigurationSetting configuration =  configurationService.getByKey(Constants.LOGO_FILE_NAME_KEY);
        params.put(Constants.LOGO,this.getClass().getClassLoader().getResourceAsStream(configuration != null ? configuration.getValue() : "logo.png"));
        params.put(Constants.GENERATED_BY, generatedBy);
        configuration =  configurationService.getByKey(Constants.OPERATOR_LOGO_FILE_NAME_KEY);

        params.put(Constants.OPERATOR_LOGO, this.getClass().getClassLoader().getResourceAsStream(configuration != null ? configuration.getValue() : "logo.png"));
        params.put(Constants.REPORT_FILTER_PARAM_VALUES, report.getReportDataProvider().getFilterSummary(filterCriteria));

        configuration =  configurationService.getByKey(Constants.OPERATOR_NAME);
        params.put(Constants.OPERATOR_NAME, configuration.getValue());

        // populate all the rest of the report parameters as overriden by the report data provider
        Map<String, String> values = report.getReportDataProvider().getExtendedHeader(filterCriteria);
        if(values != null){
            for(String key : values.keySet()){
                params.put(key, values.get(key));
            }
        }

        return params;

    }

    public Report getReportByKey(String reportKey){
        return reportsByKey.get(reportKey);
    }
}
