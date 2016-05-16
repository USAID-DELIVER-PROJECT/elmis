/**
 *Electronic Logistics Management Information System(eLMIS)is a supply chain management system for health commodities in a developing country setting.
 *
 *Copyright(C)2015 Clinton Health Access Initiative(CHAI)/MoHCDGEC Tanzania.
 *
 *This program is free software:you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation,either version 3of the License,or(at your option)any later version.

 *This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY;without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU Affero General Public License for more details.
 **/


package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;


import org.openlmis.core.domain.*;
import org.openlmis.core.dto.FacilitySupervisor;
import org.openlmis.core.service.*;
import org.openlmis.email.service.EmailService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class VaccineNotificationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineNotificationService.class);


    @Value("${mail.base.url}")
    String baseURL;

    @Autowired
    ProgramService programService;
    SimpleMailMessage message;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ConfigurationSettingService configService;
    @Autowired
    private ProcessingPeriodService processingPeriodService;
    @Autowired
    private UserService userService;
    @Autowired
    private VaccineOrderRequisitionService requisitionService;

    public void sendOrderRequisitionStatusChangeNotification(VaccineOrderRequisition report, Long userId) {

        List<FacilitySupervisor> supervisorList = new ArrayList<>();

        supervisorList = facilityService.getFacilitySuperVisorBy(report.getProgramId(), report.getFacility().getId());

        String emailMessageKey = ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_VACCINE_ORDER_REQUISITION;
        String emailSubjectKey = ConfigurationSettingKey.EMAIL_SUBJECT_VACCINE_ORDER_REQUISITION_FORM_SUBMISSION;
        sendEmailToSuperVisor(report, supervisorList, emailMessageKey, emailSubjectKey);

    }

    private void sendEmailToSuperVisor(VaccineOrderRequisition report, List<FacilitySupervisor> supervisorList, String emailMessageKey, String emailSubjectKey) {

        for (FacilitySupervisor facilitySupervisor : supervisorList) {

            String issueURL;
            issueURL = String.format("%1$s/public/pages/vaccine/order-requisition/index.html#/view/", baseURL);

            message = new SimpleMailMessage();
            String emailMessage = configService.getByKey(emailMessageKey).getValue();

            emailMessage = emailMessage.replaceAll("\\{approver_name\\}", facilitySupervisor.getName());
            emailMessage = emailMessage.replaceAll("\\{facility_name\\}", report.getFacility().getName());
            emailMessage = emailMessage.replaceAll("\\{period\\}", report.getPeriod().getName());
            emailMessage = emailMessage.replaceAll("\\{link\\}", issueURL);
            message.setText(emailMessage);
            message.setSubject(configService.getByKey(emailSubjectKey).getValue());
            message.setTo(facilitySupervisor.getContact());

            try {
                emailService.queueMessage(message);
            } catch (Exception exp) {
                LOGGER.error("Notification was not sent due to the following exception ...", exp);
            }

        }

    }


    public void sendConsolidationNotification(VaccineDistribution distribute, Long userId) {

        List<FacilitySupervisor> userList = new ArrayList<>();

        userList = facilityService.getSuperVisedUserFacility(distribute.getProgramId(), distribute.getToFacilityId());

        constructEmailForConsolidation(distribute, userList);

    }

    private void constructEmailForConsolidation(VaccineDistribution distribute, List<FacilitySupervisor> users) {

        for (FacilitySupervisor supervisor : users) {
            message = new SimpleMailMessage();

            String emailMessage = configService.getByKey(ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_ORDER_CONSOLIDATION).getValue();

            Facility facility = facilityService.getById(distribute.getFromFacilityId());
            VaccineOrderRequisition orderRequisition = requisitionService.getById(distribute.getOrderId());
            emailMessage = emailMessage.replaceAll("\\{user_name\\}", supervisor.getName());
            emailMessage = emailMessage.replaceAll("\\{from_facility_name\\}", facility.getName());
            emailMessage = emailMessage.replaceAll("\\{date_submitted\\}", orderRequisition.getOrderDate());
            emailMessage = emailMessage.replaceAll("\\{period\\}", orderRequisition.getPeriod().getName());
            message.setText(emailMessage);
            message.setSubject(configService.getByKey(ConfigurationSettingKey.EMAIL_SUBJECT_FOR_ORDER_CONSOLIDATION).getValue());
            message.setTo(supervisor.getContact());
            try {
                emailService.queueMessage(message);
            } catch (Exception exp) {
                LOGGER.error("Notification was not sent due to the following exception ...", exp);

            }

        }


    }

    public void sendIssuingEmail(VaccineDistribution distribution) {

            List<FacilitySupervisor> userList = new ArrayList<>();
        List<Program> programs = programService.getAllIvdPrograms();
        Program program = (programs != null && programs.size() > 0) ? programs.get(0) : null;
        Long programId = (program != null) ? program.getId() : null;
        userList = facilityService.getSuperVisedUserFacility(programId, distribution.getToFacilityId());

            for (FacilitySupervisor supervisor : userList) {

                message = new SimpleMailMessage();

                String emailMessage = configService.getByKey(ConfigurationSettingKey.EMAIL_TEMPLATE_FOR_ISSUE_VOUCHER).getValue();

                Facility facility = facilityService.getById(distribution.getFromFacilityId());

                VaccineOrderRequisition orderRequisition = (distribution.getOrderId() != null) ? requisitionService.getById(distribution.getOrderId()) : null;
                emailMessage = emailMessage.replaceAll("\\{user_name\\}", supervisor.getName());
                emailMessage = emailMessage.replaceAll("\\{from_facility_name\\}", facility.getName());

                String orderDate = (orderRequisition != null) ? orderRequisition.getOrderDate() : "-";
                String period = (orderRequisition != null) ? orderRequisition.getPeriod().getName() : "-";
                emailMessage = emailMessage.replaceAll("\\{date_submitted\\}", orderDate);
                emailMessage = emailMessage.replaceAll("\\{period\\}", period);

                message.setText(emailMessage);
                message.setSubject(configService.getByKey(ConfigurationSettingKey.EMAIL_SUBJECT_FOR_ISSUE_VOUCHER).getValue());
                message.setTo(supervisor.getContact());
                try {
                    emailService.queueMessage(message);
                } catch (Exception exp) {
                    LOGGER.error("Notification was not sent due to the following exception ...", exp);

                }
            }

        }

}
