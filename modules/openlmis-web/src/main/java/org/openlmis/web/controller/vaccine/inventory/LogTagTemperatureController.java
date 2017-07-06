package org.openlmis.web.controller.vaccine.inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.service.inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Integer.parseInt;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by hassan on 5/27/17.
 */

@Controller
@RequestMapping(value = "/log-tag-api/")
public class LogTagTemperatureController extends BaseController {

@Autowired
private VaccineInventoryService service;
@Value("10")
private String limit;

    @RequestMapping(value="insert.json",method=POST, headers = ACCEPT_JSON)
    public String save(@RequestBody String donor, HttpServletRequest request){
        System.out.println("Got the Rest API");

        ObjectMapper mapper = new ObjectMapper();
        try {
            LogTagDTO[] graph = mapper.readValue(donor, LogTagDTO[].class);
            service.uploadLogTag(Arrays.asList(graph));
            // System.out.println(Arrays.asList(graph));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return donor;
    }


    @RequestMapping(value="getAllLogTags.json",method=GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getLogTags(HttpServletRequest httpRequest,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "startDate") String startDate,
                                                      @RequestParam(value = "endDate") String endDate
    ){
        Long userId=loggedInUserId(httpRequest);
        Pagination pagination = new Pagination(page, parseInt(limit));
        OpenLmisResponse openLmisResponse = new OpenLmisResponse("logs", service.getLogTags(startDate,endDate , pagination));
        // pagination.setTotalRecords(service.getTotalRows(userId));
        openLmisResponse.addData("pagination", pagination);
        return openLmisResponse.response(OK);
    }
}
