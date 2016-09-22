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

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.service.DailyStockStatusSubmissionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class DailyStockStatusControllerTest {

  @Mock
  DailyStockStatusSubmissionService service;

  @Mock
  HttpServletRequest request;

  @Mock
  Principal principal;

  @Mock
  BindingResult bindingResult;

  @InjectMocks
  DailyStockStatusController controller;

  @Before
  public void setup(){
    when(principal.getName()).thenReturn("2");
    when(request.getUserPrincipal()).thenReturn(principal);
    when(bindingResult.hasErrors()).thenReturn(false);
  }

  @Test
  public void submitDailyStockStatus() throws Exception {

    DailyStockStatus stockStatus = new DailyStockStatus();
    doNothing().when(service).save(stockStatus);

    ResponseEntity<OpenLmisResponse> response  = controller.submitDailyStockStatus(stockStatus, bindingResult, request);

    assertEquals("Submission Accepted!", response.getBody().getData().get("success"));
  }

  @Test(expected = DataException.class)
  public void dailyStockStatus() throws Exception {

    DailyStockStatus stockStatus = new DailyStockStatus();
    doThrow(new SQLException("the reason")).when(service).save(stockStatus);

    ResponseEntity<OpenLmisResponse> response  = controller.submitDailyStockStatus(stockStatus, bindingResult, request);

    assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
  }

}
