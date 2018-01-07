package org.openlmis.lookupapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.commons.net.util.Base64;
import org.json.JSONObject;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.lookupapi.mapper.ILInterfaceMapper;
import org.openlmis.lookupapi.model.HFRDTO;
import org.openlmis.lookupapi.model.HealthFacilityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


@Service
@NoArgsConstructor
public class InterfaceService {

    private static final String IL_USERNAME = "IL_USERNAME";
    private static final String IL_PASSWORD = "IL_PASSWORD";
    private static final String IL_URL = "IL_URL";

    private RestTemplate restTemplate;

    @Autowired
    private ILInterfaceMapper interfaceMapper;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ConfigurationSettingService settingService;

    @Async("myExecutor")
    public void sendResponse(HealthFacilityDTO d) throws InterruptedException {

        String username = settingService.getByKey(IL_USERNAME).getValue();
        System.out.println(username);
        String password = settingService.getByKey(IL_PASSWORD).getValue();
        System.out.println(password);
        String il_url = settingService.getByKey(IL_URL).getValue();
        System.out.println(il_url);
        lookupService.saveHFR(d);

        // Thread.sleep(2000);
        if (d != null) {
            HealthFacilityDTO hfr = interfaceMapper.getByTransactionId(d.getIlIDNumber());

            HealthFacilityDTO dto = new HealthFacilityDTO();
            if (hfr != null) {
                dto.setIlIDNumber(hfr.getIlIDNumber());
                dto.setStatus("Success");
                if (il_url != null && username != null && password != null)
                    sendConfirmationMessage(username, password, il_url, dto.getStatus(), hfr.getIlIDNumber());

            } else {
                dto.setIlIDNumber(d.getIlIDNumber());
                dto.setStatus("Fail");
                if (il_url != null && username != null && password != null)
                    sendConfirmationMessage(username, password, il_url, dto.getStatus(), dto.getIlIDNumber());

                System.out.println("Failure Message");
            }


        }


    }

    private void sendConfirmationMessage(String username, String password, String il_url, String status, String transId) {
        ObjectMapper mapper = new ObjectMapper();
        URL obj = null;
        try {
            obj = new URL(il_url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            HFRDTO hfrdto = new HFRDTO();
            hfrdto.setStatus(status);
            hfrdto.setIL_TransactionIDNumber(transId);

            String jsonInString = mapper.writeValueAsString(hfrdto);

            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            System.out.println("all connection" + con);

            con.setDoOutput(true);

            System.out.println(jsonInString);

            OutputStream wr = con.getOutputStream();
            wr.write(jsonInString.getBytes("UTF-8"));

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
