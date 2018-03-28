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

package org.openlmis.rnr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProductService;
import org.openlmis.rnr.domain.DailyStockStatus;
import org.openlmis.rnr.dto.MSDStockStatusDTO;
import org.openlmis.rnr.dto.MsdStatusDTO;
import org.openlmis.rnr.repository.DailyStockStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;

@Component
public class DailyStockStatusSubmissionService {

    private static final String IL_USERNAME = "IL_USERNAME";
    private static final String IL_PASSWORD = "IL_PASSWORD";
    private static final String IL_URL = "IL_URL";


    @Autowired
    private FacilityService facilityService;
    @Autowired
    private ProductService productService;
    @Autowired
    private DailyStockStatusRepository repository;

    @Autowired
    private ConfigurationSettingService settingService;

    public void save(DailyStockStatus dailyStockStatus) throws SQLException {
        repository.clearStatusForFacilityProgramDate(dailyStockStatus.getFacilityId(), dailyStockStatus.getProgramId(), dailyStockStatus.getDate());
        repository.insert(dailyStockStatus);
    }

    public String convertDate(String dateStr) {

/*
      String dateStr = "Thu Jan 19 2012 01:00 PM";
*/
        DateFormat readFormat = new SimpleDateFormat("MM dd yyyy hh:mm aaa");

        DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = readFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = "";
        if (date != null) {
            formattedDate = writeFormat.format(date);
        }
        System.out.println(formattedDate);
        return formattedDate;

    }

    @Async("myExecutor")
    public void saveMSDStockStatus(String dailyStockStatus, Long aLong) throws ParseException {
        String username = settingService.getByKey(IL_USERNAME).getValue();

        String password = settingService.getByKey(IL_PASSWORD).getValue();
        String il_url = settingService.getByKey(IL_URL).getValue();


        ObjectMapper mapper = new ObjectMapper();

        try {
            MSDStockStatusDTO[] values = mapper.readValue(dailyStockStatus, MSDStockStatusDTO[].class);

            int totalReceived = 0;
            int updated = 0;
            int ignored = 0;
            int imported = 0;
            String id = "";

            for (MSDStockStatusDTO dto : values) {

                if (dto.getIlId() != null) {

                            dto.setCreatedBy(aLong);
                            repository.clearStatusForFacilityProductDate(dto.getFacilityCode(), dto.getProductCode(), dto.getOnHandDate());
                            Long trans = repository.saveMsdStockStatus(dto);

                            if (trans == null) {
                                dto.setStatus("Fail");
                            } else {
                                dto.setStatus("Success");
                            }
                            imported++;

                            id = dto.getIlId();
                }
                totalReceived++;
            }
            String status = "Success";
            MsdStatusDTO statusDTO = new MsdStatusDTO();
            statusDTO.setStatus(status);
            statusDTO.setIL_TransactionIDNumber(id);
            statusDTO.setImported(imported);
            statusDTO.setUpdated(updated);
            if ((imported + updated) < totalReceived)
                statusDTO.setIgnored(totalReceived - (imported + updated));
            else
                statusDTO.setIgnored(ignored);
            String jsonInString = mapper.writeValueAsString(statusDTO);
            if (il_url != null && username != null && password != null)
                sendConfirmationMessage(username, password, il_url, jsonInString);

        } catch (IOException e) {

            //  if (il_url != null && username != null && password != null)
            //  sendConfirmationMessage(username, password, il_url, dto.getStatus(), hfr.getIlIDNumber());

            e.printStackTrace();
        }

    }

    private void sendConfirmationMessage(String username, String password, String il_url, String jsonData) {

        URL obj = null;
        try {
            obj = new URL(il_url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            System.out.println("all connection" + con);

            con.setDoOutput(true);

            System.out.println(jsonData);

            OutputStream wr = con.getOutputStream();
            wr.write(jsonData.getBytes("UTF-8"));

            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + il_url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
