package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.report.mapper.StockEventReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.StockEventParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by hassan on 6/22/17.
 */
@Component
@NoArgsConstructor
public class StockEventReportDataProvider extends ReportDataProvider {

    @Autowired
     private StockEventReportMapper reportMapper;

    @Autowired
    GeographicZoneService geoService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    private ProcessingPeriodService periodService;
    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria),this.getUserId(),rowBounds);

    }

    private StockEventParam getReportFilterData(Map<String, String[]> filterCriteria) {

        StockEventParam param = new StockEventParam();

        Long district = StringHelper.isBlank(filterCriteria, "district") ? 0L : Long.parseLong(filterCriteria.get("district")[0]);
        GeographicZone zone =geoService.getById(district);
        Facility facility = facilityService.getByGeographicZoneId(district,zone.getLevel().getId());
        System.out.println("facility");
        param.setFacilityId(facility.getId());
        System.out.println(param.getFacilityId());
        Long year = StringHelper.isBlank(filterCriteria, "year") ? 0L : Long.parseLong(filterCriteria.get("year")[0]);
        param.setYear(year);

        Long period = StringHelper.isBlank(filterCriteria, "period") ? 0L : Long.parseLong(filterCriteria.get("period")[0]);
        ProcessingPeriod period1= periodService.getById(period);
        Long period2 =0L;
        Date value;
        if(period1 != null){
            value = period1.getStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            period2= Long.valueOf(cal.get(Calendar.MONTH));
            System.out.println(period2 + 1);
        }

        param.setMonthInNumber(period2 + 1);

        Long product = StringHelper.isBlank(filterCriteria, "product") ? 0L : Long.parseLong(filterCriteria.get("product")[0]);

        param.setProduct(product);


        return param;


    }

}
