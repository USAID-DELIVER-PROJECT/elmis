/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.reporting.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.view.jasperreports.*;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.io.File.createTempFile;
import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

/**
 * Exposes the services for generating jasper report multi format view using a data source to fetch data.
 */

@Service
public class JasperReportsViewFactory {

  @Autowired
  DataSource replicationDataSource;

  protected static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
  private static final String PDF = "PDF";
  private static final String XLS = "XLS";
  private static final String HTML = "HTML";
  private static final String CSV = "CSV";


  public JasperReportsMultiFormatView getJasperReportsView(Template template)
    throws IOException, ClassNotFoundException, JRException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();

    setExportParams(jasperView);

    setDataSourceAndURLAndApplicationContext(template, jasperView);

    return jasperView;
  }

  private void setExportParams(JasperReportsMultiFormatView jasperView) {
    Map<JRExporterParameter, Object> reportFormatMap = new HashMap<>();
    reportFormatMap.put(IS_USING_IMAGES_TO_ALIGN, false);
    jasperView.setExporterParameters(reportFormatMap);
  }

  private void setDataSourceAndURLAndApplicationContext(Template template,
                                                        JasperReportsMultiFormatView jasperView)
    throws IOException, ClassNotFoundException, JRException {
    WebApplicationContext ctx = getCurrentWebApplicationContext();

    jasperView.setJdbcDataSource(replicationDataSource);
    jasperView.setUrl(getReportURLForReportData(template));

    if (ctx != null)
      jasperView.setApplicationContext(ctx);
  }

  public String getReportURLForReportData(Template template)
    throws IOException, ClassNotFoundException, JRException {

    File tmpFile = createTempFile(template.getName() + "_temp", ".jasper");
    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(template.getData()));
    JasperReport jasperReport = (JasperReport) inputStream.readObject();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject(jasperReport);
    writeByteArrayToFile(tmpFile, bos.toByteArray());
    return tmpFile.toURI().toURL().toString();
  }

  public AbstractJasperReportsSingleFormatView getJasperReportsView(HttpServletRequest httpServletRequest,
                                                                    DataSource dataSource, Template url, String format, String fileName)

          throws IOException, ClassNotFoundException, JRException {

    String viewFormat = format==null?"pdf":format;

    // set possible content headers
    Properties availableHeaders = new Properties();
    availableHeaders.put("html", "inline; filename="+fileName+".html");
    availableHeaders.put("csv", "inline; filename="+fileName+".csv");
    availableHeaders.put("pdf", "inline; filename="+fileName+".pdf");
    availableHeaders.put("xls", "inline; filename="+fileName+".xls");

    // get jasperView class based on the format supplied
    // defaults to pdf
    AbstractJasperReportsSingleFormatView jasperView = null;

    switch (viewFormat.toUpperCase())
    {
      case CSV:
      jasperView = new JasperReportsCsvView();
        break;
      case XLS:
        jasperView = new JasperReportsXlsView();
        break;
      case PDF:
        jasperView = new JasperReportsPdfView();
        break;
      case HTML:
        jasperView = new JasperReportsHtmlView();
        break;
      default:

    }
    // get appContext. required by the view
    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(
            httpServletRequest.getSession().getServletContext());

    // set the appropriate content disposition header.
    Properties headers = new Properties();
    headers.put(HEADER_CONTENT_DISPOSITION, availableHeaders.get(viewFormat));

    // set the relevant jasperView properties
    if (jasperView != null) {
      jasperView.setJdbcDataSource(replicationDataSource);
      jasperView.setUrl(getReportURLForReportData(url));
      jasperView.setApplicationContext(ctx);
      jasperView.setHeaders(headers);
    }
    // return view
    return jasperView;
  }


}