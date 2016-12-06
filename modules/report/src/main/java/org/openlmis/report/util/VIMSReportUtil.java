/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.util;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;

import java.util.*;

public class VIMSReportUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static List<Map<String, Object>> getSummaryPeriodList(String startDate, String endDate) {

        DateTime periodStart = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(startDate);
        DateTime periodEnd = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(endDate);

        int monthDiff = Months.monthsBetween(periodStart.withDayOfMonth(1), periodEnd.withDayOfMonth(1)).getMonths();

        DateTime temp = periodStart.withDayOfMonth(1);

        List<Map<String, Object>> list = new ArrayList<>();

        while (monthDiff >= 0) {

            Map<String, Object> period = new HashMap<>();
            period.put("year", temp.getYear());
            period.put("month", temp.getMonthOfYear());
            period.put("monthString", temp.toString("MMM"));

            monthDiff--;

            list.add(period);
            temp = temp.plusMonths(1);

        }

        return list;
    }
}
