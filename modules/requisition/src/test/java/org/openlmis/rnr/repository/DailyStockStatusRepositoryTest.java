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

package org.openlmis.rnr.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.domain.DailyStockStatusLineItem;
import org.openlmis.rnr.repository.mapper.DailyStockStatusMapper;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DailyStockStatusRepositoryTest {

  @Mock
  DailyStockStatusMapper mapper;

  @InjectMocks
  DailyStockStatusRepository repository;

  @Test
  public void insert() throws Exception {
    DailyStockStatus status = createDailyStockStatus();
    doNothing().when(mapper).insert(status);
    doNothing().when(mapper).insertLineItem(any(DailyStockStatusLineItem.class));

    repository.insert(status);

    verify(mapper).insert(any(DailyStockStatus.class));
    verify(mapper, times(2)).insertLineItem(any(DailyStockStatusLineItem.class));
  }

  private DailyStockStatus createDailyStockStatus() {
    DailyStockStatus status = new DailyStockStatus();
    status.setFacilityId(1L);
    status.setProgramId(2L);
    status.setDate(new Date());
    status.getLineItems().add(new DailyStockStatusLineItem());
    status.getLineItems().add(new DailyStockStatusLineItem());
    return status;
  }

  @Test
  public void clearStatusForFacilityProgramDate() throws Exception {
    DailyStockStatus status = createDailyStockStatus();
    doNothing().when(mapper).clearStatusForFacilityDate(anyLong(), anyLong(), any(Date.class));

    repository.clearStatusForFacilityProgramDate(status.getFacilityId(), status.getProgramId(), status.getDate());

    verify(mapper).clearStatusForFacilityDate(status.getFacilityId(), status.getProgramId(), status.getDate());
  }

}
