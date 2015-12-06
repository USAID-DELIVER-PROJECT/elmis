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

package org.openlmis.vaccine.service.reports;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.apache.commons.lang.StringUtils;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsCommonUtilService {
    public static final int REGION_REPORT = 1;
    public static final int DISTRICT_REPORT = 2;
    public static final int FACILLITY_REPORT = 3;
    public static final String BELOW_MIN = "1_dropoutGreaterThanHigh";
    public static final String MIN = "2_dropOutBetweenMidAndMin";
    public static final String AVERAGE = "3_droOputBetweenMidAndHigh";
    public static final String HIGHER = "4_dropoutGreaterThanHigh";
    public static final String TEMP_MIN = "1_temp_min_less";
    public static final String TEMP_GREATER_MIN = "2_temp_min_greater";
    public static final String MIN_TEMP_RECORDED = "3_min_temp_recorded";
    public static final String MAX_TEMP_RECORDED = "4_max_temp_recorded";
    public static final String ALARM_EPISODES_LESS_MIN = "5_alarm_episode_less_min";
    public static final String ALARM_EPISODES_GREATER_MIN = "6_alarm_episode_greater_min";

    public static List<Date> extractColumnValues(PerformanceByDropoutRateParam filterParam) {
        String periodStart = filterParam.getPeriod_start_date();
        String periodEnd = filterParam.getPeriod_end_date();
        Date staDate = null;
        Date enDate = null;
        List<Date> columnNames = new ArrayList<>();
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatEnd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthName = new SimpleDateFormat("MMM yyyy");
        try {
            staDate = dateFormatStart.parse(periodStart);
            enDate = dateFormatEnd.parse(periodEnd);
            while (!staDate.after(enDate)) {
                String colName = monthName.format(staDate);
                columnNames.add(monthName.parse(colName));
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(staDate);
                calendar.add(Calendar.MONTH, 1);
                staDate = calendar.getTime();

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    public static PerformanceByDropoutRateParam prepareParam(Map<String, String[]> filterCriteria) {
        PerformanceByDropoutRateParam filterParam = null;
        if (filterCriteria != null) {
            filterParam = new PerformanceByDropoutRateParam();
            filterParam.setFacility_id(filterCriteria.get("facility") == null || filterCriteria.get("facility").length <= 0 || StringUtils.isBlank(filterCriteria.get("facility")[0]) ? 0 : Long.parseLong(filterCriteria.get("facility")[0])); //defaults to 0
            filterParam.setGeographic_zone_id(filterCriteria.get("geographicZoneId") == null || StringUtils.isBlank(filterCriteria.get("geographicZoneId")[0]) ? 0 : Long.parseLong(filterCriteria.get("geographicZoneId")[0]));
            filterParam.setPeriod_end_date(StringUtils.isBlank(filterCriteria.get("periodEnd")[0]) ? null : filterCriteria.get("periodEnd")[0]);

            filterParam.setPeriod_start_date(StringUtils.isBlank(filterCriteria.get("periodStart")[0]) ? null : filterCriteria.get("periodStart")[0]);
            filterParam.setProduct_id(filterCriteria.get("productId") == null || StringUtils.isBlank(filterCriteria.get("productId")[0]) ? 0 : Long.parseLong(filterCriteria.get("productId")[0]));

        }
        return filterParam;

    }
}
