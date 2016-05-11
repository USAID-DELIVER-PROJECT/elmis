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

package org.openlmis.equipment.service;

import org.apache.log4j.Logger;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.email.service.EmailService;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@EnableScheduling
public class EquipmentInventoryNotificationService {

    @Autowired
    EquipmentInventoryRepository repository;

    @Autowired
    ProgramService programService;
    Logger logger = Logger.getLogger(EquipmentInventoryNotificationService.class);
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConfigurationSettingService configService;
    @Autowired
    private UserService userService;
    @Autowired
    private SupervisoryNodeService supervisoryNodeService;
    private String emailMessageKey;
    private String emailSubjectKey;

    @Scheduled(cron = "${batch.job.equipment.non.functional.schedule}")
    public void sendEmailNotificationsForNonFunctional() {

        List<Program> programs = programService.getAllIvdPrograms();
        Program program = (programs != null) ? programs.get(0) : null;

        List<User> userList = new ArrayList<>();
        List<Facility> facilities = repository.getFacilitiesWithNonFunctionalEquipments();
        for (Facility facility : facilities) {
            List<User> usersToAdd = new ArrayList<>();
            Long supervisoryNodeId = supervisoryNodeService.getFor(facility, program).getId();
            usersToAdd = userService.getUsersWithRightInHierarchyUsingBaseNode(supervisoryNodeId, program, RightName.MANAGE_EQUIPMENT_INVENTORY);
            userList.addAll(usersToAdd);
        }
        Set<User> uniqueUsers = new HashSet<>(userList);
        List<User> uniqueUserList = new ArrayList<User>(uniqueUsers);
        emailMessageKey = ConfigurationSettingKey.NON_FUNCTIONAL_EQUIPMENTS_EMAIL_MESSAGE_TEMPLATE;
        emailSubjectKey = ConfigurationSettingKey.NON_FUNCTIONAL_EQUIPMENTS_EMAIL_SUBJECT;

        sendEmailNotifications(uniqueUserList, emailMessageKey, emailSubjectKey);

    }

    public void sendEmailNotifications(List<User> users, String messageKey, String emailSubjectKey) {
        for (User user : users) {
            if (user.isMobileUser()) {
                continue;
            }


            Map model = new HashMap();
//      model.put(FACILITY_NAME, '');

            String emailMessage = configService.getByKey(messageKey).getValue();
            String emailSubject = configService.getByKey(emailSubjectKey).getValue();
            if (user.getEmail() != null) {
                try {

                    emailService.queueHtmlMessage(user.getEmail(),
                            emailSubject,
                            emailMessage,
                            model);

                } catch (Exception exp) {
                    logger.error("Notification was not sent due to the following exception ...", exp);
                }
            }
        }
    }


}
