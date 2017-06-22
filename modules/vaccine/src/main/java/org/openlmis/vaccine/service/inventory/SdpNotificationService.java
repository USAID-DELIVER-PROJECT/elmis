package org.openlmis.vaccine.service.inventory;

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

/**
 * Created by hassan on 5/25/17.
 */

@Service
@Async
public class SdpNotificationService {

    @Autowired
    private VaccineInventoryDistributionMapper mapper;

    public void updateNotification(Long distributionId){
        if(distributionId != null) {
            VaccineDistribution d = mapper.getDistributionById(distributionId);
            sendNotification(d);
        }

    }

    private void sendNotification(VaccineDistribution d) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(d.toString(), headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity("https://www.broker.tz.vodafone.com:28443/broker/receive", request, String.class);
           if(response.getStatusCode().toString().equals("OK")){
               //update sent notification

           }
        }catch (Exception e){

            System.out.println(e.getMessage());
        }
        System.out.println(d);

    }


}
