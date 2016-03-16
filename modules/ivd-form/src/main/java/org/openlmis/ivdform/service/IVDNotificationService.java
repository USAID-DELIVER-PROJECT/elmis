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

import org.openlmis.core.domain.*;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.ivdform.domain.reports.ReportStatus;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openlmis.email.service.EmailService;
import org.openlmis.core.domain.User;


/**
 * A service for IDV operations email notification
 */
@Service
public class IVDNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IVDNotificationService.class);


    @Value("${app.url}")
    String baseURL;

    private static final String FACILITY_NAME = "facility_name";
    private static final String NAME = "name";
    private static final String PERIOD_NAME = "period_name";
    private static final String USER_NAME = "user_name";
    private static final String APPROVAL_URL = "approval_url";
    private static final String DATE_PROCESSED = "date_processed";

    @Autowired
    private UserService userService;

    @Autowired
    private SupervisoryNodeService supervisoryNodeService;

    @Autowired
    ProgramService programService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigurationSettingService configService;

    private String emailMessageKey;
    private String emailSubjectKey;

    public void sendIVDStatusChangeNotification(VaccineReport report, Long userId){

        List<User> userList = new ArrayList<>();
        Program program = programService.getById(report.getProgramId());

        if (report.getStatus().equals(ReportStatus.SUBMITTED)) {
            Long supervisoryNodeId = supervisoryNodeService.getFor(report.getFacility(), program).getId();
            userList = userService.getUsersWithRightInHierarchyUsingBaseNode(supervisoryNodeId, program, RightName.APPROVE_IVD);
            emailMessageKey = ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_IVD_FORM_SUBMISSION;
            emailSubjectKey = ConfigurationSettingKey.EMAIL_SUBJECT_IVD_FORM_SUBMISSION;
        }
        else if (report.getStatus().equals(ReportStatus.APPROVED)) {
            userList.add(userService.getById(userId));
            emailMessageKey = ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_IVD_FORM_APPROVED;
            emailSubjectKey = ConfigurationSettingKey.EMAIL_SUBJECT_IVD_FORM_APPROVAL;
        }
        else if(report.getStatus().equals(ReportStatus.REJECTED)) {
            userList.add(userService.getById(userId));
            emailMessageKey = ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_IVD_FORM_REJECTION;
            emailSubjectKey = ConfigurationSettingKey.EMAIL_SUBJECT_IVD_FORM_REJECTION;
        }
        else
          return;

        User modifiedByUser  = userService.getById(report.getModifiedBy());
        ArrayList<User> activeUsersWithRight = userService.filterForActiveUsers(userList);
        sendEmailForIVDSubmittersApprovers(report, activeUsersWithRight, emailMessageKey, emailSubjectKey, modifiedByUser);

    }

    public void sendEmailForIVDSubmittersApprovers(VaccineReport report, List<User> users, String messageKey, String emailSubjectKey, User modifiedByUser){

        for (User user : users) {
            if (user.isMobileUser()) {
                continue;
            }

            String approvalURL = String.format("%1$s/public/pages/ivd-form/index.html#/approve/%2$s", baseURL, report.getId());

            Map model = new HashMap();
            model.put(FACILITY_NAME, report.getFacility().getName());
            model.put(NAME, user.getFirstName() + " " + user.getLastName());
            model.put(USER_NAME, modifiedByUser.getUserName());
            model.put(PERIOD_NAME, report.getPeriod().getName());
            model.put(APPROVAL_URL, approvalURL);
            model.put(DATE_PROCESSED, DateUtil.getFormattedDate(new Date(), DateUtil.FORMAT_DATE_TIME));

            String emailMessage = configService.getByKey(messageKey).getValue();
            String emailSubject  = configService.getByKey(emailSubjectKey).getValue();

            try {

                emailService.queueHtmlMessage(user.getEmail(),
                        emailSubject,
                        emailMessage,
                        model);
            } catch (Exception exp) {
                LOGGER.error("Notification was not sent due to the following exception ...", exp);
            }
        }
    }

}
