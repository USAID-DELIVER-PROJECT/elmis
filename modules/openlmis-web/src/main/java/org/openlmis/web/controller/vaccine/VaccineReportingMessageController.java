/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.web.controller.vaccine;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.email.service.EmailService;
import org.openlmis.report.model.dto.MessageCollection;
import org.openlmis.report.model.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/vaccine/messages")
public class VaccineReportingMessageController {
    @Autowired
    private EmailService emailService;
    private static final Logger LOGGER = Logger.getLogger(TrendOfMinMaxColdRangeReportController.class);
    @RequestMapping(value = "/send", method = RequestMethod.POST, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> send(
            @RequestBody MessageCollection messages
    ) {

        for (MessageDto dto : messages.getMessages()) {
            LOGGER.info(messages.getMessages().size());
            queueSimpleMail(dto);

        }

        return OpenLmisResponse.success("Success");
    }

    private void queueSimpleMail(MessageDto dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("teklutesfayeh@gmail.com");
        message.setSubject("Reporting rate notice");
        message.setText(dto.getMessage());
        LOGGER.info("here i am");
        emailService.send(message);
    }
}
