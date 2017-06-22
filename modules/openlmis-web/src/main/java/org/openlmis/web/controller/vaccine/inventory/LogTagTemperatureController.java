package org.openlmis.web.controller.vaccine.inventory;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hassan on 5/27/17.
 */

@Controller
@RequestMapping(value = "/log-tag-api/")
public class LogTagTemperatureController extends BaseController {

    @RequestMapping(value="insert.json",method=GET, headers = ACCEPT_JSON)
    public String save(@RequestBody String donor, HttpServletRequest request){


      // String successResponse = String.format("Donor '%s' has been successfully saved");
        System.out.println(donor);
        System.out.println("Got the Rest API");
        return donor;
    }
}
