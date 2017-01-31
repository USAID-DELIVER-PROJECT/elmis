package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.OnTimeInFullReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.OnTimeInFullReportParam;
import org.openlmis.report.model.report.OnTimeInFullReport;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        List<OnTimeInFullReport> onTimeInFullReports = reportMapper.getReport(getReportFilterData(filterCriteria),rowBounds);
        System.out.println(onTimeInFullReports);
        List<OnTimeInFullReport> arr = new ArrayList<>();
        OnTimeInFullReport onTimeInFullReport ;

        for(OnTimeInFullReport fullReport : onTimeInFullReports){

            onTimeInFullReport = new OnTimeInFullReport();
            onTimeInFullReport.setReceivedDate(fullReport.getReceivedDate());
            onTimeInFullReport.setRequestedDate(fullReport.getRequestedDate());
            onTimeInFullReport.setQuantityRequested(fullReport.getQuantityRequested());
            onTimeInFullReport.setQuantityReceived(fullReport.getQuantityReceived());
            onTimeInFullReport.setStoreName(fullReport.getStoreName());
            onTimeInFullReport.setRegion(fullReport.getRegion());
            onTimeInFullReport.setProduct(fullReport.getProduct());
            if(fullReport.getQuantityRequested() != null) {

                Double less = fullReport.getQuantityRequested() - getTenPercentLess(fullReport.getQuantityRequested());
                Double greater = fullReport.getQuantityRequested() + getTenPercentLess(fullReport.getQuantityRequested());

                onTimeInFullReport.setInFullLessRange(less);
                onTimeInFullReport.setInFullGreatRange(greater);

                String reportInFull = getInFull(less, greater, fullReport.getQuantityReceived());
                onTimeInFullReport.setOnFull(reportInFull);
                arr.add(onTimeInFullReport);
            }

        }
        System.out.println(arr);
        return arr;

    }

    private String getInFull(Double less, Double greater, Integer received) {

        if(less <= received && received <= greater){
            return "Yes";
        }else {
            return  "No";
        }
    }

    private double getTenPercentLess(Integer quantityRequested) {
        return ( configurationSettingService.getConfigurationIntValue("ON_TIME_IN_FULL_CONF_NUMBER") * quantityRequested) / 100L;
    }


    public OnTimeInFullReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        OnTimeInFullReportParam param = new OnTimeInFullReportParam();

        Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
        param.setProgram(programId);

      /*  Long product = StringHelper.isBlank(filterCriteria, "product") ? 0L : Long.parseLong(filterCriteria.get("product")[0]);
        param.setProduct(product);*/

        String productCategory = StringHelper.isBlank(filterCriteria, "productCategory") ? null : ((String[]) filterCriteria.get("productCategory"))[0];
        param.setProductCategory(productCategory);

        String product = StringHelper.isBlank(filterCriteria, "product") ? null : ((String[]) filterCriteria.get("product"))[0];
        param.setProduct(product);

        // param.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);
      /*  String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        param.setFacilityLevel(facilityLevel);*/

        String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        param.setFacilityLevel(facilityLevel);

        String startDate = StringHelper.isBlank(filterCriteria, "startDate") ? null : ((String[]) filterCriteria.get("startDate"))[0];
        param.setStartDate(startDate);

        String endDate = StringHelper.isBlank(filterCriteria, "endDate") ? null : ((String[]) filterCriteria.get("endDate"))[0];
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
