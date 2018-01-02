package org.openlmis.lookupapi.service;

import lombok.NoArgsConstructor;
import org.apache.commons.net.util.Base64;
import org.json.JSONObject;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.lookupapi.mapper.ILInterfaceMapper;
import org.openlmis.lookupapi.model.HealthFacilityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
@NoArgsConstructor
public class InterfaceService {

    private static final String IL_USERNAME ="IL_USERNAME" ;
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

        String username=settingService.getByKey(IL_USERNAME).getValue();
        System.out.println("username");
        System.out.println(username);
        String password = settingService.getByKey(IL_PASSWORD).getValue();
        System.out.println("password");
        System.out.println(password);
        String il_url = settingService.getByKey(IL_URL).getValue();
        System.out.println("url");
        System.out.println(il_url);

        lookupService.saveHFR(d);
        System.out.println("I'm second");
          // Thread.sleep(2000);
        if(d != null){
            HealthFacilityDTO hfr = interfaceMapper.getByTransactionId(d.getIlIDNumber());

            HealthFacilityDTO dto = new HealthFacilityDTO();
            if(hfr !=null){
                dto.setIlIDNumber(hfr.getIlIDNumber());
                dto.setStatus("Success");
                if(il_url != null && username != null && password !=null)
                postData(username,password,il_url,dto.getStatus(),hfr.getIlIDNumber());

            }else {
                dto.setIlIDNumber(d.getIlIDNumber());
                dto.setStatus("Fail");
                if(il_url != null && username != null && password !=null)
                    postData(username,password,il_url,dto.getStatus(),dto.getIlIDNumber());

                System.out.println("Failure Message");
            }


        }


    }

    private void postData(String username, String password, String il_url,String status,String transId) {

        String authString = username+":"+password;
        byte[] authStringBytes = authString.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(authStringBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);

        String json = "{\"IL_TransactionIDNumber\":"+transId+",\"Status\":"+status+"}";

        JSONObject jsonData = new JSONObject(json);
        System.out.println("JSON data");
        System.out.println(jsonData);
        HttpEntity<String> request = new HttpEntity<String>(jsonData.toString(),headers);
        System.out.println("data");
        System.out.println(request);
        ResponseEntity<String> response = restTemplate.postForEntity(il_url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject userJson = new JSONObject(response.getBody());
            System.out.println(userJson);
        }else
            System.out.println("responsesss");

        System.out.println(response.getStatusCode());
        System.out.println("  responses");
        System.out.println(response.getBody());

    }


}
