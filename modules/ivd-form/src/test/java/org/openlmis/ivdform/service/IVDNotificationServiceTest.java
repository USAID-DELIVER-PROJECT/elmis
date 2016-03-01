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

package org.openlmis.ivdform.service;

import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.service.EmailService;
import org.openlmis.ivdform.domain.reports.ReportStatus;
import org.openlmis.ivdform.domain.reports.VaccineReport;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class IVDNotificationServiceTest {

    @InjectMocks
    IVDNotificationService notificationService;

    @Mock
    ProgramService programService;

    @Mock
    UserService userService;

    @Mock
    EmailService emailService;

    @Mock
    ConfigurationSettingService configService;

    private Facility facility;
    private ProcessingPeriod period;
    private VaccineReport report;
    private User user;
    private List<User> userList;
    private Long userId = 1L;

    @Before
    public void setUp(){

        facility = new Facility();
        facility.setName("facility_1");

        period = new ProcessingPeriod(1L);
        period.setName("period_1");

        report = new VaccineReport();
        report.setPeriod(period);
        report.setFacility(facility);
        report.setProgramId(1L);
        report.setId(1L);
        report.setModifiedBy(1L);

        userList = new ArrayList<>();
        user = new User();
        user.setId(1L);
        user.setEmail("email@rmail");
        userList.add(user);
        user.setUserName("username");

    }

    @Test
    public void shouldSendEmailForIVDFormApprovalStatus(){

        report.setStatus(ReportStatus.APPROVED);

        ConfigurationSetting setting = new ConfigurationSetting();
        setting.setValue("config");

        when(configService.getByKey(anyString())).thenReturn(setting);
        when(programService.getById(1L)).thenReturn(new Program(1L));
        when(userService.getById(userId)).thenReturn(user);
        when(userService.filterForActiveUsers(userList)).thenReturn((ArrayList<User>) userList);
        when(userService.getById(report.getModifiedBy())).thenReturn(user);

        notificationService.sendIVDStatusChangeNotification(report, userId);

        verify(emailService).queueHtmlMessage(eq(user.getEmail()), eq(setting.getValue()), eq(setting.getValue()), anyMap());

    }

    @Test
    public void shouldNotSendEmailForIVDFormForStatusStatusDraft(){

        report.setStatus(ReportStatus.DRAFT);

        ConfigurationSetting setting = new ConfigurationSetting();
        setting.setValue("config");

        when(configService.getByKey(anyString())).thenReturn(setting);
        when(programService.getById(1L)).thenReturn(new Program(1L));
        when(userService.getById(userId)).thenReturn(user);
        when(userService.filterForActiveUsers(userList)).thenReturn((ArrayList<User>) userList);


        notificationService.sendIVDStatusChangeNotification(report, userId);

        verify(emailService, never()).queueHtmlMessage(eq(user.getEmail()), eq(setting.getValue()), eq(setting.getValue()), anyMap());
    }

}
