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

package org.openlmis.ivdform.repository.reports;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.ivdform.domain.reports.LogisticsLineItem;
import org.openlmis.ivdform.repository.mapper.reports.LogisticsLineItemMapper;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class LogisticsLineItemRepositoryTest {

  @Mock
  LogisticsLineItemMapper mapper;

  @InjectMocks
  LogisticsLineItemRepository repository;

  @Test
  public void shouldInsert() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    repository.insert(lineItem);
    verify(mapper).insert(lineItem);
  }

  @Test
  public void shouldUpdate() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    repository.update(lineItem);
    verify(mapper).update(lineItem);
  }

  @Test
  public void shouldGetPreviousPeriodLineItemsForProvidedParameters() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    when(mapper.getUpTo3PreviousPeriodLineItemsFor("Program", "Product","facilityCode", 2L)).thenReturn(asList(lineItem));
    List<LogisticsLineItem> response = repository.getUpTo3PreviousPeriodLineItemsFor("Program", "Product","facilityCode", 2L);
    verify(mapper).getUpTo3PreviousPeriodLineItemsFor("Program", "Product","facilityCode", 2L);
    assertThat(response.get(0), is(lineItem));
  }

  @Test
  public void shouldGetApprovedLineItemListForProvidedParameters() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    when(mapper.getApprovedLineItemListFor("Program", "facilityCode", 2L)).thenReturn(asList(lineItem));
    List<LogisticsLineItem> response = repository.getApprovedLineItemListFor("Program","facilityCode", 2L);
    verify(mapper).getApprovedLineItemListFor("Program","facilityCode", 2L);
    assertThat(response.get(0), is(lineItem));
  }

  @Test
  public void shouldGetApprovedLineItemForProvidedParameters() throws Exception {
    LogisticsLineItem lineItem = new LogisticsLineItem();
    when(mapper.getApprovedLineItemFor("Program", "Product","facilityCode", 2L)).thenReturn(lineItem);
    LogisticsLineItem response = repository.getApprovedLineItemFor("Program", "Product","facilityCode", 2L);
    verify(mapper).getApprovedLineItemFor("Program", "Product","facilityCode", 2L);
    assertThat(response, is(lineItem));
  }
}