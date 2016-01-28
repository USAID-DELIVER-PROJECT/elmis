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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.ivdform.builders.reports.VaccineReportBuilder;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.dto.ReportStatusDTO;
import org.openlmis.ivdform.dto.RoutineReportDTO;
import org.openlmis.ivdform.service.IvdFormService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({Date.class})
public class IvdFormControllerTest {

  @Mock
  IvdFormService service;

  @Mock
  ProgramService programService;

  @Mock
  FacilityService facilityService;

  @Mock
  UserService userService;

  @InjectMocks
  IvdFormController controller;

  private MockHttpSession session;
  private MockHttpServletRequest httpServletRequest;

  @Before
  public void setUp() throws Exception {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, 1L);
  }


  @Test
  public void shouldGetPeriods() throws Exception {
    Date currentDate = new Date();
    List<ReportStatusDTO> periods = new ArrayList<>();
    when(service.getPeriodsFor(1L, 1L, currentDate)).thenReturn(periods);
    whenNew(Date.class).withNoArguments().thenReturn(currentDate);

    ResponseEntity<OpenLmisResponse> response = controller.getPeriods(1L, 1L);

    assertThat(periods, is(response.getBody().getData().get("periods")));
  }

  @Test
  public void shouldInitialize() throws Exception {
    VaccineReport report = new VaccineReport();
    when(service.initialize(1L, 1L, 1L, 1L)).thenReturn(report);

    ResponseEntity<OpenLmisResponse> response = controller.initialize(1L, 1L, 1L, httpServletRequest);

    verify(service).initialize(1L, 1L, 1L, 1L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldGetReport() throws Exception {
    VaccineReport report = new VaccineReport();
    when(service.getById(23L)).thenReturn(report);

    ResponseEntity<OpenLmisResponse> response = controller.getReport(23L);

    verify(service).getById(23L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldSave() throws Exception {
    VaccineReport report = new VaccineReport();
    doNothing().when(service).save(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.save(report, httpServletRequest);

    verify(service).save(report, 1L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldSubmit() throws Exception {
    VaccineReport report = new VaccineReport();
    doNothing().when(service).submit(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.submit(report, httpServletRequest);

    verify(service).submit(report, 1L);

    // the status would have changed.
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldGetViewPeriods() throws Exception {
    Date currentDate = new Date();
    List<ReportStatusDTO> periods = new ArrayList<>();
    when(service.getReportedPeriodsFor(1L, 1L)).thenReturn(periods);
    whenNew(Date.class).withNoArguments().thenReturn(currentDate);

    ResponseEntity<OpenLmisResponse> response = controller.getViewPeriods(1L, 1L);

    assertThat(periods, is(response.getBody().getData().get("periods")));
  }

  @Test
  public void shouldGetPendingFormsForApproval() throws Exception {
    RoutineReportDTO dto = new RoutineReportDTO();
    dto.setStatus("REPORT_FOUND");
    List<RoutineReportDTO> pendingForms = asList(dto);
    when(service.getApprovalPendingForms(1L, 1L)).thenReturn( pendingForms );

    ResponseEntity<OpenLmisResponse> response = controller.getPendingFormsForApproval(1L, httpServletRequest);

    verify(service).getApprovalPendingForms(1L, 1L);
    assertThat(pendingForms, is(response.getBody().getData().get("pending_submissions")));

  }

  @Test
  public void shouldApprove() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    doNothing().when(service).approve(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.approve(report, httpServletRequest);

    assertThat(report, is(response.getBody().getData().get("report")));
    verify(service).approve(report, 1L);
  }

  @Test
  public void shouldReject() throws Exception {
    VaccineReport report = make(a(VaccineReportBuilder.defaultVaccineReport));
    doNothing().when(service).reject(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.reject(report, httpServletRequest);

    assertThat(report, is(response.getBody().getData().get("report")));
    verify(service).reject(report, 1L);
  }
}