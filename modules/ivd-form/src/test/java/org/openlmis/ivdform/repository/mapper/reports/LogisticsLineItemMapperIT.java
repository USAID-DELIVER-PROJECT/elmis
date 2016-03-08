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

package org.openlmis.ivdform.repository.mapper.reports;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.ivdform.builders.reports.LogisticsLineItemBuilder;
import org.openlmis.ivdform.builders.reports.VaccineReportBuilder;
import org.openlmis.ivdform.domain.reports.LogisticsLineItem;
import org.openlmis.ivdform.domain.reports.ReportStatus;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-applicationContext-ivdform.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class LogisticsLineItemMapperIT {

  @Autowired
  IvdFormMapper ivdFormMapper;

  @Autowired
  ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  LogisticsLineItemMapper logisticsLineItemMapper;

  @Autowired
  FacilityMapper facilityMapper;

  private VaccineReport report;

  private Product product;

  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod,
        with(scheduleId, processingSchedule.getId()),
        with(ProcessingPeriodBuilder.name, "Period1")));

    processingPeriodMapper.insert(processingPeriod);

    product = make(a(ProductBuilder.defaultProduct));
    productMapper.insert(product);

    report = make(a(VaccineReportBuilder.defaultVaccineReport));
    report.setPeriodId(processingPeriod.getId());
    report.setFacilityId(facility.getId());
    ivdFormMapper.insert(report);
  }

  @Test
  public void shouldInsert() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setReportId(report.getId());
    lineItem.setProductId(product.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    assertThat(count, is(1));
    assertThat(lineItem.getId(), is(notNullValue()));
  }

  @Test
  public void shouldUpdate() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setReportId(report.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    lineItem.setRemarks("the Remark");
    logisticsLineItemMapper.update(lineItem);

    List<LogisticsLineItem> lineItemList = logisticsLineItemMapper.getLineItems(report.getId());
    assertThat(lineItem.getRemarks(), is(lineItemList.get(0).getRemarks()));
  }

  @Test
  public void shouldGetLineItems() throws Exception {
    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setReportId(report.getId());
    Integer count = logisticsLineItemMapper.insert(lineItem);

    List<LogisticsLineItem> lineItemList = logisticsLineItemMapper.getLineItems(report.getId());
    assertThat(lineItemList, hasSize(1));
  }

  @Test
  public void shouldGetApprovedLineItemForProductWhenThereIsApprovedReport() throws Exception {



    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setClosingBalance(20L);
    lineItem.setReportId(report.getId());
    logisticsLineItemMapper.insert(lineItem);

    report.setStatus(ReportStatus.APPROVED);
    ivdFormMapper.update(report);

    LogisticsLineItem response = logisticsLineItemMapper.getApprovedLineItemFor( "VACCINES", lineItem.getProductCode(), facility.getCode(), report.getPeriodId());
    assertThat(response, is(Matchers.notNullValue()));
    assertThat(response.getClosingBalance(), is(lineItem.getClosingBalance()));
  }

  @Test
  public void shouldGetNullApprovedLineItemForProductWhenThereIsNoApprovedReportForSpecifiedPeriod() throws Exception {

    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setClosingBalance(40L);
    lineItem.setReportId(report.getId());
    logisticsLineItemMapper.insert(lineItem);

    report.setStatus(ReportStatus.DRAFT);
    ivdFormMapper.update(report);

    LogisticsLineItem response = logisticsLineItemMapper.getApprovedLineItemFor( "VACCINES", lineItem.getProductCode(), facility.getCode(), report.getPeriodId());
    assertThat(response, is(nullValue()));
  }

  @Test
  public void shouldGetApprovedLineItemListForFacility() throws Exception {

    LogisticsLineItem lineItem = make(a(LogisticsLineItemBuilder.defaultLogisticsLineItem));
    lineItem.setProductId(product.getId());
    lineItem.setClosingBalance(40L);
    lineItem.setReportId(report.getId());
    logisticsLineItemMapper.insert(lineItem);

    report.setStatus(ReportStatus.APPROVED);
    ivdFormMapper.update(report);

    List<LogisticsLineItem> lineItems = logisticsLineItemMapper.getApprovedLineItemListFor("VACCINES", facility.getCode(), report.getPeriodId());
    assertThat(lineItems.get(0).getClosingBalance(), is(lineItem.getClosingBalance()));
  }
  
}