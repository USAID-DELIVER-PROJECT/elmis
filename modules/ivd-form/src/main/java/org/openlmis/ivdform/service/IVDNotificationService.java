/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.ivdform.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.core.service.UserService;
import org.openlmis.ivdform.domain.reports.ReportStatus;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openlmis.email.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.openlmis.core.domain.User;


/**
 * A service for IDV operations email notification
 */
@Service
public class IVDNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IVDNotificationService.class);

    private static final String FACILITY_NAME = "facility_name";
    private static final String NAME = "name";
    private static final String PERIOD_NAME = "period_name";

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


    public void sendIVDStatusChangeNofitication(VaccineReport report, Long userId){

        List<User> userList = new ArrayList<>();
        Program program = programService.getById(report.getProgramId());

        String emailMessageKey = "";
        String emailSubjectKey = "";

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


        ArrayList<User> activeUsersWithRight = userService.filterForActiveUsers(userList);
        sendEmailForIVDSubmittersApprovers(report, activeUsersWithRight, emailMessageKey, emailSubjectKey);

    }

    public void sendEmailForIVDSubmittersApprovers(VaccineReport report, List<User> users, String messageKey, String emailSubjectKey){

        for (User user : users) {
            if (user.isMobileUser()) {
                continue;
            }

            Map model = new HashMap();
            model.put(FACILITY_NAME, report.getFacility().getName());
            model.put(NAME, user.getFirstName() + " " + user.getLastName());
            model.put(PERIOD_NAME, report.getPeriod().getName());

            SimpleMailMessage message = new SimpleMailMessage();
            String emailMessage = configService.getByKey(messageKey).getValue();

            emailService.queueHtmlMessage(user.getEmail(),
                              configService.getByKey(emailSubjectKey).getValue(),
                              emailMessage,
                              model);

            try {
                emailService.queueMessage(message);
            } catch (Exception exp) {
                LOGGER.error("Notification was not sent due to the following exception ...", exp);
            }
        }
    }

}
