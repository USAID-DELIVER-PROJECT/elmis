package org.openlmis.vaccine.service.inventory;

import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by hassan on 5/25/17.
 */

@Service
@Async
public class SdpNotificationService {

    public  static final String TIIS_URL= "VIMS_TIMR_INTERGRATION";
    public  static final String TIIS_USERNAME= "VIMS_TIMR_INTERGRATION";
    public  static final String TIIS_PASSWORD= "VIMS_TIMR_INTERGRATION";

    @Autowired
    private VaccineInventoryDistributionMapper mapper;

    @Autowired
    private VaccineInventoryDistributionService distributionService;

    @Autowired
    ConfigurationSettingService configurationSettingService;

    public void updateNotification(Long distributionId) {
        if (distributionId != null) {
            VaccineDistribution d = mapper.getDistributionById(distributionId);
            sendHttps(d);
            //sendNotification(d);
        }

    }

    public void sendHttps(VaccineDistribution d) {

        String url = configurationSettingService.getByKey(TIIS_URL).getValue();
        String username = configurationSettingService.getByKey(TIIS_USERNAME).getValue();
        String password = configurationSettingService.getByKey(TIIS_PASSWORD).getValue();

        VaccineDistribution distribution = distributionService.getDistributionByToFacility(d.getToFacilityId());
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(url);
        try {
            String jsonInString = mapper.writeValueAsString(distribution);

            URL obj = new URL(url);

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            String userCredentials = username+":"+password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            System.out.println("all connection" + con);

            con.setDoOutput(true);

            System.out.println(jsonInString);

            OutputStream wr = con.getOutputStream();
            wr.write(jsonInString.getBytes("UTF-8"));

            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
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
        } catch (Exception e) {
            System.out.println("e" + e.getMessage());
            e.printStackTrace();
        }


    }


   /* private void sendNotification(VaccineDistribution d) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("username", "lucy");
        headers.add("password", "Kaloleni12");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(d.toString(), headers);
        String url =
                "https://ec2-54-187-21-117.us-west-2.compute.amazonaws.com/SVC/HealthFacilityManagement.svc/receiveDelivery?vimsToFacilityId=" + d.getToFacilityId();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().toString().equals("OK")) {
                //update sent notification

            }
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
        System.out.println(d);

    }*/


}
