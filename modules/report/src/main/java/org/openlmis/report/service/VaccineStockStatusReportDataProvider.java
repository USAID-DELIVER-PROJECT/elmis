package org.openlmis.report.service;


import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.mapper.VaccineStockStatusMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.VaccineReceivedSummaryReportParam;
import org.openlmis.report.model.params.VaccineStockStatusParam;
import org.openlmis.report.model.report.VaccineStockStatusReport;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Component
@NoArgsConstructor
public class VaccineStockStatusReportDataProvider extends ReportDataProvider {

    @Autowired
    private VaccineStockStatusMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;
    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramService programService;

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        VaccineStockStatusParam params = getReportFilterData(filterCriteria);
         params.setUserId(this.getUserId());
       return reportMapper.getReport(getReportFilterData(filterCriteria),this.getUserId(),rowBounds);

    }

    public int getTotal(List<VaccineStockStatusReport> stockStatusReport){
        int total = 0;
        for (VaccineStockStatusReport aStockStatusReport : stockStatusReport) {

            total += aStockStatusReport.getAdequacy();
        }
        return (total / stockStatusReport.size()) * 100;
    }
    public VaccineStockStatusParam getReportFilterData(Map<String, String[]> filterCriteria) {

        VaccineStockStatusParam param = new VaccineStockStatusParam();

       /* Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
        param.setProgram(programId);*/
        param.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);
        String productCategory = StringHelper.isBlank(filterCriteria, "productCategory") ? null : ((String[]) filterCriteria.get("productCategory"))[0];
        param.setProductCategory(productCategory);

        String statusDate = StringHelper.isBlank(filterCriteria, "statusDate") ? null : ((String[]) filterCriteria.get("statusDate"))[0];
        param.setStatusDate(statusDate);

        String products = StringHelper.isBlank(filterCriteria, "products") ? null : ((String[]) filterCriteria.get("products"))[0];
        param.setProducts(products);

       List<Program> program = programService.getIvdProgramForSupervisedFacilities(this.getUserId(),MANAGE_EQUIPMENT_INVENTORY);


        List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), program.get(0).getId(), MANAGE_EQUIPMENT_INVENTORY);
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



