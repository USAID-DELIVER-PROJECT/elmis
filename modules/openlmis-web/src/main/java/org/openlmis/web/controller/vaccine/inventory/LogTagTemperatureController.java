package org.openlmis.web.controller.vaccine.inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.inventory.FacilityTemperatureLogTag;
import org.openlmis.vaccine.domain.inventory.VaccineInventoryProductConfiguration;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.service.inventory.FacilityTemperatureLogTagService;
import org.openlmis.vaccine.service.inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by hassan on 5/27/17.
 */

@Controller
@RequestMapping(value = "/log-tag-api/")
public class LogTagTemperatureController extends BaseController {

@Autowired
private VaccineInventoryService service;

@Autowired
private FacilityTemperatureLogTagService logTagService;

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

    @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody FacilityTemperatureLogTag logTags) {

        logTagService.insert(logTags);
        return OpenLmisResponse.response("saved", logTags.getSerialNumber());
    }

    @RequestMapping(value = "getLogTagTemps", method = GET, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getAll() {
        return OpenLmisResponse.response("logTags", logTagService.getAll());
    }

    @RequestMapping(value = "byId/{id}", method = GET, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK')")
    public ResponseEntity getById(@PathVariable Long id) {
        return OpenLmisResponse.response("logTags",logTagService.getById(id));
    }

}
