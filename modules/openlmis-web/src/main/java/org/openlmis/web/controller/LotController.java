package org.openlmis.web.controller;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.service.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by hassan on 8/22/17.
 */


@Controller
public class LotController extends BaseController {

    @Autowired
    private LotService service;


    @RequestMapping(value = "lot/save", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> save(@RequestBody Lot lot,HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        try {
            Long userId= loggedInUserId(request);
            lot.setModifiedBy(userId);
            lot.setCreatedBy(userId);
            service.insertLot(lot);
        }catch(DuplicateKeyException exp){
            return OpenLmisResponse.error("Duplicate lot Name Exists in DB.", HttpStatus.BAD_REQUEST);
        }

        successResponse = success(String.format("Lot '%s' has been successfully saved", lot.getLotCode()));
        successResponse.getBody().addData("lots", lot);
        return  successResponse;
    }


    @RequestMapping(method = GET, value = "lot/lots")
    public ResponseEntity<OpenLmisResponse> getLots( ){
        return OpenLmisResponse.response("lots",service.getAll());
    }

    @RequestMapping(value="lot/{id}",method = GET,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getById(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response("lotsById",service.getById(id));
    }


}
