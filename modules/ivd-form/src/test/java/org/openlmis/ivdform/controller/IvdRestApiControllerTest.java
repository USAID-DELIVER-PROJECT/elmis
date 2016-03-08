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

package org.openlmis.ivdform.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.ivdform.dto.FacilityIvdSummary;
import org.openlmis.ivdform.dto.StockStatusSummary;
import org.openlmis.ivdform.service.IvdFormService;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class IvdRestApiControllerTest {

  @Mock
  IvdFormService service;

  @InjectMocks
  IvdRestApiController controller;

  @Test
  public void shouldGetProductStockStatus() throws Exception {
    StockStatusSummary summary = new StockStatusSummary();
    when(service.getStockStatusForProductInFacility("FC1", "PC1", "PGC1", 1L)).thenReturn(summary);

    ResponseEntity<OpenLmisResponse> response = controller.getStockStatusForProductInFacility("FC1", "PC1", "PGC1", 1L);
    verify(service).getStockStatusForProductInFacility("FC1", "PC1", "PGC1", 1L);
    assertThat(summary, is(response.getBody().getData().get("status")));
  }

  @Test
  public void shouldGetProductFacilityStatus() throws Exception {
    FacilityIvdSummary summary = new FacilityIvdSummary("FC1", "PC1", 1L);
    when(service.getStockStatusForAllProductsInFacility("FC1", "PGC1", 1L)).thenReturn(summary);

    ResponseEntity<OpenLmisResponse> response = controller.getStockStatusForAllProductInFacility("FC1", "PGC1", 1L);
    verify(service).getStockStatusForAllProductsInFacility("FC1", "PGC1", 1L);
    assertThat(summary, is(response.getBody().getData().get("status")));
  }
}