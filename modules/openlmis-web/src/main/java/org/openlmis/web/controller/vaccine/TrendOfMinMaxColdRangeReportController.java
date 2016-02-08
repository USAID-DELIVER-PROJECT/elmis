/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.web.controller.vaccine;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.apache.log4j.Logger;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.vaccine.domain.reports.TrendOfMinMaxColdChainTempratureReport;
import org.openlmis.vaccine.service.reports.TrendOfMinMasColdRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/vaccine/report/")
public class TrendOfMinMaxColdRangeReportController {
    public static final String TREND_MIN_MAX_COLD_RANGE_REPORT = "trendMinMaxColdRangeReport";
    private static final Logger LOGGER = Logger.getLogger(TrendOfMinMaxColdRangeReportController.class);
    @Autowired
    private TrendOfMinMasColdRangeService service;

    @RequestMapping(value = "trendOfMinMaxColdRange", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<OpenLmisResponse> getTrendColdRangeReportList(
                                                                        HttpServletRequest request) {
        TrendOfMinMaxColdChainTempratureReport trendOfMinMaxColdChainTempratureReport = null;

        try {
            trendOfMinMaxColdChainTempratureReport = service.loadTrendMinMaxColdChainTempratureReports(request.getParameterMap());
        } catch (Exception ex) {
            LOGGER.warn(" Exception is :" , ex);
        }

        return OpenLmisResponse.response(TREND_MIN_MAX_COLD_RANGE_REPORT, trendOfMinMaxColdChainTempratureReport);
    }
}
