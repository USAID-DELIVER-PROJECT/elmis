package org.openlmis.vaccine.service.reports;
import org.apache.commons.lang.StringUtils;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsCommonUtilService {
    private ReportsCommonUtilService(){

    }
    public static final int REGION_REPORT = 1;
    public static final int DISTRICT_REPORT = 2;
    public static final int FACILLITY_REPORT = 3;
    public static final String BELOW_MIN = "1_dropoutGreaterThanHigh";
    public static final String MIN = "2_dropOutBetweenMidAndMin";
    public static final String AVERAGE = "3_droOputBetweenMidAndHigh";
    public static final String HIGHER = "4_dropoutGreaterThanHigh";
    public static final String TEMP_MIN = "1_temp_min_less";
    public static final String TEMP_GREATER_MIN = "2_temp_min_greater";
    public static final String MIN_TEMP_RECORDED = "7_min_temp_recorded";
    public static final String MAX_TEMP_RECORDED = "8_max_temp_recorded";
    public static final String ALARM_EPISODES_LESS_MIN = "5_alarm_episode_less_min";
    public static final String ALARM_EPISODES_GREATER_MIN = "6_alarm_episode_greater_min";
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceByDropoutRateByDistrictService.class);
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
           LOGGER.warn("error while parsing: ", e);
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
