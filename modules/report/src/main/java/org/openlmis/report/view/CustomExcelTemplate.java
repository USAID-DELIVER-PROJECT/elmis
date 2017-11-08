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

package org.openlmis.report.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.report.model.custom.ColumnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
//TODO: rewrite this Object Oriented
public class CustomExcelTemplate extends AbstractView {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomExcelTemplate.class);
  private static final String COLUMN_OPTIONS = "columnoptions";
  private static final String NUMBER = "number";
  private static final String PERCENT = "percent";
  private static final String REPORT_NAME = "Report Name";
  private static final String NAME = "name";
  private static final String GENERATED_ON = "Generated On";
  private static final String MONTH = "month";

  private static final String[] allMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  private Boolean isPivot = false;

  private int pivotStartColumn = 0;
  private List<String> distinctPivotColumns;
  private HashMap<String, Integer> rowValues = new HashMap<>();
  private int rowNumber = 0;


  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map queryModel = (Map) model.get("queryModel");
    List reportContent = (List) model.get("report");
    response.setHeader("Content-Disposition", "attachment; filename=" + queryModel.get("name") + ".xlsx");

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Sheet 1");

    try {
      writeHeader(sheet, workbook, queryModel, reportContent);
      writeReportData(sheet, queryModel, reportContent);
      workbook.write(response.getOutputStream());
      workbook.close();
    } catch (IOException e) {
      LOGGER.error("Could not write Excel to stream. Please see errors for details.", e);
      throw new DataException(e.getMessage());
    }
  }

  private void writeHeader(XSSFSheet sheet, XSSFWorkbook workbook, Map queryModel, List reportContent) throws IOException {
    List<ColumnModel> columns = getColumnDefinitions(queryModel);

    rowNumber = 0;


    CellStyle headerCellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    headerCellStyle.setFont(font);


    XSSFRow row = sheet.createRow(rowNumber++);
    Cell cell = row.createCell(0);
    cell.setCellValue(REPORT_NAME);
    cell.setCellStyle(headerCellStyle);
    cell = row.createCell(1);
    cell.setCellValue(queryModel.get(NAME).toString());

    row = sheet.createRow(rowNumber++);
    cell = row.createCell(0);
    cell.setCellValue(GENERATED_ON);
    cell.setCellStyle(headerCellStyle);
    cell = row.createCell(1);
    cell.setCellValue(new Date().toString());


    int columnIndex = 0;
    this.isPivot = false;

    row = sheet.createRow(rowNumber++);

    for (ColumnModel col : columns) {
      // if this is not a isPivot column an is not supposed to be an invisible column, then render it.
      if (col.getVisible() && !col.getPivotValue() && !col.getPivotColumn()) {
        cell = row.createCell(columnIndex++);
        cell.setCellValue(col.getDisplayName());
        cell.setCellStyle(headerCellStyle);
      }

    }

    //check if the pivot settings are all correct. otherwise - do not try to pivot this.
    if (
        columns.stream().filter(ColumnModel::getPivotColumn).findAny().isPresent()
            &&
            columns.stream().filter(ColumnModel::getPivotColumn).findAny().isPresent()
            &&
            columns.stream().filter(ColumnModel::getPivotRow).findAny().isPresent()
        ) {

      this.isPivot = true;

      //TODO: implement the isPivot headers here.
      List<String> pivotColumns = getPivotColumns(columns, reportContent);
      this.pivotStartColumn = columnIndex;
      for (String head : pivotColumns) {
        cell = row.createCell(columnIndex++);
        cell.setCellValue(head);
        cell.setCellStyle(headerCellStyle);
      }
      rowValues.clear();
    }
  }

  private List<String> getPivotColumns(List<ColumnModel> columns, List reportContent) {

    ColumnModel pivotColumnModel = (ColumnModel) columns.stream()
        .filter(ColumnModel::getPivotColumn)
        .findFirst()
        .get();
    String pivotColumnName = pivotColumnModel.getName();
    distinctPivotColumns = (List<String>) reportContent
        .stream()
        .map(f -> ((Map) f).get(pivotColumnName))
        .collect(Collectors.toList());

    distinctPivotColumns = distinctPivotColumns
        .stream()
        .distinct()
        .collect(Collectors.toList());

    if (pivotColumnModel.getPivotType().equals(MONTH)) {
      List<String> monthlyColumns = new ArrayList<>();
      for (String month : allMonths) {
        Optional<String> foundMonth = distinctPivotColumns.stream().filter(f -> f.startsWith(month)).findFirst();
        if (foundMonth.isPresent()) {
          monthlyColumns.add(foundMonth.get());
        } else {
          monthlyColumns.add(month);
        }
      }
      distinctPivotColumns = monthlyColumns;
    }

    return distinctPivotColumns;
  }

  private List<ColumnModel> getColumnDefinitions(Map queryModel) {
    String columnModel = queryModel.get(COLUMN_OPTIONS).toString();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    List<ColumnModel> actualObj = new ArrayList<>();
    try {
      actualObj = objectMapper.readValue(columnModel, new TypeReference<List<ColumnModel>>() {
      });
    } catch (Exception exp) {
      LOGGER.error("Column Definition was not populated correctly due to .... ", exp);
    }
    return actualObj;
  }

  private void writeReportData(XSSFSheet sheet, Map queryModel, List reportContent) throws IOException {
    List<ColumnModel> columns = getColumnDefinitions(queryModel);

    String pivotRowFieldName = "";
    String pivotValueFieldName = "";
    String pivotColumnFieldName = "";


    ColumnModel pivotValueColumn = null;

    if (this.isPivot) {
      pivotRowFieldName = columns.stream()
          .filter(ColumnModel::getPivotRow)
          .findFirst()
          .get().getName();


      if (this.isPivot) {
        pivotValueColumn = columns.stream()
            .filter(ColumnModel::getPivotValue)
            .findFirst()
            .get();
        pivotValueFieldName = columns.stream()
            .filter(ColumnModel::getPivotValue)
            .findFirst()
            .get().getName();

        pivotColumnFieldName = columns.stream()
            .filter(ColumnModel::getPivotColumn)
            .findFirst()
            .get()
            .getName();
      }
    }
    for (Object o : reportContent) {
      Map m = (Map) o;
      if (this.isPivot) {
        //check if this is something that we need to write as basic
        if (!rowValues.containsKey(m.get(pivotRowFieldName).toString())) {
          rowValues.put(m.get(pivotRowFieldName).toString(), rowNumber);
          rowNumber = writeBasicRowValues(sheet, columns, rowNumber, m);
        }
        int rn = rowValues.get(m.get(pivotRowFieldName).toString());
        String column = m.get(pivotColumnFieldName).toString();
        int newColumnIndex = distinctPivotColumns.indexOf(column) + pivotStartColumn;
        Cell cell = sheet.getRow(rn).createCell(newColumnIndex);
        Object value = m.get(pivotValueFieldName);
        if (NUMBER.equals(pivotValueColumn.getFormatting()) || PERCENT.equals(pivotValueColumn.getFormatting())) {
          cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        }

        if (value != null) {
          cell.setCellValue(value.toString());
        }


        if (pivotValueColumn.getClassification() != null && m.get(pivotValueColumn.getClassification()) != null) {
          String classifciation = m.get(pivotValueColumn.getClassification()).toString();
          setConditionalCellFormat(sheet, cell, classifciation);
        }

      } else {
        rowNumber = writeBasicRowValues(sheet, columns, rowNumber, m);
      }

    }
  }

  private void setConditionalCellFormat(XSSFSheet sheet, Cell cell, String classifciation) {
    XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    switch (classifciation) {
      case "good":
        style.setFillForegroundColor(new XSSFColor(new Color(69, 183, 63)));
        break;
      case "bad":
        style.setFillForegroundColor(new XSSFColor(new Color(244, 0, 0)));
        break;
      case "warn":
        style.setFillForegroundColor(new XSSFColor(new Color(252, 241, 91)));
        break;
      case "normal":
        style.setFillForegroundColor(new XSSFColor(new Color(201, 201, 201)));
        break;
      default:
        break;
    }
    cell.setCellStyle(style);
  }

  private static int writeBasicRowValues(XSSFSheet sheet, List<ColumnModel> columns, int rowNumber, Map m) {
    int index = 0;

    XSSFRow row = sheet.createRow(rowNumber++);
    for (ColumnModel col : columns) {
      if (col.getVisible() && !col.getPivotValue() && !col.getPivotColumn()) {
        Cell cell = row.createCell(index);
        if(m.get(col.getName()) != null) {
          cell.setCellValue(m.get(col.getName()).toString());
          if (NUMBER.equals(col.getFormatting())) {
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
          }
        }
        index++;
      }
    }
    return rowNumber;
  }
}
