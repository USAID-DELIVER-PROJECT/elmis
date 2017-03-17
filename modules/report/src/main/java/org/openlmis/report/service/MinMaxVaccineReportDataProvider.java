package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.mapper.MinMaxVaccineReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.MinMaxVaccineReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

/**
 * Created by hassan on 2/2/17.
 */

@Component
@NoArgsConstructor
public class MinMaxVaccineReportDataProvider  extends ReportDataProvider  {

    @Autowired
    private MinMaxVaccineReportMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;
    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramService programService;

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria),this.getUserId(),rowBounds);

    }

    private MinMaxVaccineReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
        MinMaxVaccineReportParam param = new MinMaxVaccineReportParam();
/*
        Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
        param.setProgram(programId);*/

        Long year = StringHelper.isBlank(filterCriteria, "year") ? 0L : Long.parseLong(filterCriteria.get("year")[0]);
        param.setYear(year);


        String productCategory = StringHelper.isBlank(filterCriteria, "productCategory") ? null : ((String[]) filterCriteria.get("productCategory"))[0];
        param.setProductCategory(productCategory);

        String product = StringHelper.isBlank(filterCriteria, "product") ? null : ((String[]) filterCriteria.get("product"))[0];
        param.setProduct(product);


        String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        param.setFacilityLevel(facilityLevel);

        List<Program> programs = programService.getIvdProgramForSupervisedFacilities(this.getUserId(),MANAGE_EQUIPMENT_INVENTORY);


        List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), programs.get(0).getId(), MANAGE_EQUIPMENT_INVENTORY);
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


}
