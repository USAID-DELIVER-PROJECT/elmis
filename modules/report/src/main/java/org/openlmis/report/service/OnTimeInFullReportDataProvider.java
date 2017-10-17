package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.joda.time.Days;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.report.mapper.OnTimeInFullReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.params.OnTimeInFullReportParam;
import org.openlmis.report.model.report.OnTimeInFullReport;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

/**
 * Created by hassan on 1/29/17.
 */
@Component
@NoArgsConstructor
public class OnTimeInFullReportDataProvider extends ReportDataProvider {

    @Autowired
    private OnTimeInFullReportMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;
    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ConfigurationSettingService configurationSettingService;

    @Autowired
    private ProcessingPeriodService periodService;

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        OnTimeInFullReportParam params = getReportFilterData(filterCriteria);

        params.setUserId(this.getUserId());
        List<OnTimeInFullReport> onTimeInFullReports = reportMapper.getReport(getReportFilterData(filterCriteria), this.getUserId(), rowBounds);

        List<OnTimeInFullReport> arr = new ArrayList<>();
        OnTimeInFullReport onTimeInFullReport;

        for (OnTimeInFullReport fullReport : onTimeInFullReports) {

            onTimeInFullReport = new OnTimeInFullReport();
            onTimeInFullReport.setReceivedDate(fullReport.getReceivedDate());
            onTimeInFullReport.setRequestedDate(fullReport.getRequestedDate());
            onTimeInFullReport.setQuantityRequested(fullReport.getQuantityRequested());
            onTimeInFullReport.setQuantityReceived(fullReport.getQuantityReceived());
            onTimeInFullReport.setStoreName(fullReport.getStoreName());
            onTimeInFullReport.setRegion(fullReport.getRegion());
            onTimeInFullReport.setProduct(fullReport.getProduct());
            onTimeInFullReport.setOnTime(getOnTimeData(fullReport.getReceivedDate(), fullReport.getRequestedDate()));
            if (fullReport.getQuantityRequested() != null) {

                Double less = fullReport.getQuantityRequested() - getTenPercentLess(fullReport.getQuantityRequested());
                Double greater = fullReport.getQuantityRequested() + getTenPercentLess(fullReport.getQuantityRequested());

                onTimeInFullReport.setInFullLessRange(less);
                onTimeInFullReport.setInFullGreatRange(greater);

                String reportInFull = getInFull(less, greater, fullReport.getQuantityReceived());
                onTimeInFullReport.setOnFull(reportInFull);
                if ((Objects.equals("Yes", onTimeInFullReport.getOnTime())) && (Objects.equals(onTimeInFullReport.getOnFull(), "Yes")))
                    onTimeInFullReport.setOnTimeAndOnFull("Yes");
                else
                    onTimeInFullReport.setOnTimeAndOnFull("No");
                arr.add(onTimeInFullReport);
            }

        }
        return arr;

    }

    private String getOnTimeData(Date receivedDate, Date requestedDate) {

        if (receivedDate != null && requestedDate != null) {

            int valueToCompare = configurationSettingService.getConfigurationIntValue("DELIVERED_ON_TIME_CONFIG_NUMBER");

            long diff = -1;

            try {

                DateFormat readFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");

                Date dateStart = readFormat.parse(String.valueOf(requestedDate));
                Date dateEnd = readFormat.parse(String.valueOf(receivedDate));

               // System.out.println(dateStart);
               // System.out.println(dateEnd);
                diff = Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000);
               // System.out.println(diff);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (valueToCompare > diff)
                return "Yes";
            else
                return "No";
        } else
            return "Not sure";
    }


    private String getInFull(Double less, Double greater, Integer received) {

        if (less <= received && received <= greater) {
            return "Yes";
        } else {
            return "No";
        }
    }

    private double getTenPercentLess(Integer quantityRequested) {
        return (configurationSettingService.getConfigurationIntValue("ON_TIME_IN_FULL_CONF_NUMBER") * quantityRequested) / 100L;
    }

    public Integer getMonth(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.MONTH)+1;
    }

    public OnTimeInFullReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        OnTimeInFullReportParam param = new OnTimeInFullReportParam();


        Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
        param.setProgram(programId);
        Long period = StringHelper.isBlank(filterCriteria, "period") ? 0L : Long.parseLong(filterCriteria.get("period")[0]);
       // ProcessingPeriod p = periodService.getById(period);
       // System.out.println(getMonth(p.getEndDate()));
        //param.setPeriods(getMonth(p.getEndDate()));

        // param.setPeriod(period);

        Long year = StringHelper.isBlank(filterCriteria, "year") ? 0L : Long.parseLong(filterCriteria.get("year")[0]);
        param.setYear(year);

      /*  Long product = StringHelper.isBlank(filterCriteria, "product") ? 0L : Long.parseLong(filterCriteria.get("product")[0]);
        param.setProduct(product);*/

        String productCategory = StringHelper.isBlank(filterCriteria, "productCategory") ? null : ((String[]) filterCriteria.get("productCategory"))[0];
        param.setProductCategory(productCategory);

      /*  String product = StringHelper.isBlank(filterCriteria, "product") ? null : ((String[]) filterCriteria.get("product"))[0];
        param.setProduct(product);*/

        String products = StringHelper.isBlank(filterCriteria, "products") ? null : ((String[]) filterCriteria.get("products"))[0];
        param.setProducts(products);

        // param.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);
      /*  String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        param.setFacilityLevel(facilityLevel);*/

        String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        param.setFacilityLevel(facilityLevel);

        String startDate = StringHelper.isBlank(filterCriteria, "periodStart") ? null : ((String[]) filterCriteria.get("periodStart"))[0];
        param.setStartDate(startDate);
        System.out.println(param.getStartDate());

        String endDate = StringHelper.isBlank(filterCriteria, "periodEnd") ? null : ((String[]) filterCriteria.get("periodEnd"))[0];
        param.setEndDate(endDate);

       /* String products = StringHelper.isBlank(filterCriteria, "products") ? null : ((String[]) filterCriteria.get("products"))[0];
        param.setProducts(products);
*/

        List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), programId, MANAGE_EQUIPMENT_INVENTORY);
        facilities.add(facilityService.getHomeFacility(this.getUserId()));

        StringBuilder str = new StringBuilder();
        str.append("{");
        for (Facility f : facilities) {
            str.append(f.getId());
            str.append(",");
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append("}");
        param.setFacilityIds(str.toString());

        return param;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }


}
