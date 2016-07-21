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

package org.openlmis.sms.service;


import lombok.NoArgsConstructor;
import org.openlmis.sms.Repository.mapper.ConfigMapper;
import org.openlmis.sms.domain.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Future;

@Service
@NoArgsConstructor
public class SMSService {


  //TDOO: remove sms gateway URL
  // and the corresponding configuration from file.
  private String smsGatewayUrl;
  /* If this is false, we're disallowing the system from sending sms messages */
  private Boolean smsSendingFlag;

  @Autowired
  ConfigMapper configMapper;

  @Autowired
  public SMSService(@Value("${sms.gateway.url}") String smsGatewayUrl, @Value("${sms.sending.flag}") Boolean smsSendingFlag) {
    this.smsGatewayUrl = smsGatewayUrl;
    this.smsSendingFlag = smsSendingFlag;
  }

  public Future<Boolean> send(SMS sms) {
    if (!smsSendingFlag || sms.getSent()) {
      return new AsyncResult<>(true);
    }

    return new AsyncResult<>(SendSMSMessage(sms));
  }

  @Async
  public Future<Boolean> sendAsync(SMS sms) {
    if (!smsSendingFlag || sms.getSent() == Boolean.TRUE ) {
      return new AsyncResult<>(true);
    }

    return new AsyncResult<>(SendSMSMessage(sms));
  }

  public void ProcessSMS(@Payload List<SMS> smsList) {
    if(!smsSendingFlag){
      return;
    }

    for (SMS sms : smsList) {
        SendSMSMessage(sms);
    }
  }

  public Boolean SendSMSMessage(SMS sms) {
    //TDOO: Please clean this up. why the try catch?
    //TOFIX: How could this be?
    try{
      String relayUrl = configMapper.getValueByKey("KANNEL_SETTINGS");
      relayUrl = relayUrl.replaceAll("TO_PHONE", sms.getPhoneNumber());
      relayUrl = relayUrl.replaceAll("MESSAGE_CONTENT", URLEncoder.encode(sms.getMessage(), "UTF-8"));
      System.out.println(relayUrl);
      URL url = new URL(relayUrl);
      url.getContent();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}