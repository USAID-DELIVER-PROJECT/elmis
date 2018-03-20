package org.openlmis.report.builder;

import org.openlmis.report.model.params.RejectedRnRReportParam;

import java.util.Map;

public class RejectedRnRReportQueryBuilder {

    public static String getQuery(Map params){

        RejectedRnRReportParam filter =(RejectedRnRReportParam)params.get("filterCriteria");

        return " select districtId,district_name districtName,region_name as regionName,zone_name as zoneName, count(*) rejectedCount from ( \n" +
                "                  select count(*), rnrid, zone_name,d.district_id districtId,d.district_name,d.region_name from requisition_status_changes c \n" +
                "                    join requisitions r on r.id = c.rnrid \n" +
                "                    join facilities f on f.id = r.facilityid \n" +
                "                    join vw_districts d on d.district_id = f.geographiczoneid \n" +
                "                    where c.status = '"+filter.getStatus()+"' and r.programid ="+filter.getProgram()+" and r.periodId = "+filter.getPeriod()+" group by rnrid, d.zone_name,d.district_id ,d.district_name,d.region_name having count(*) > 1\n" +
                "                ) a\n" +
                "                group by districtId, a.district_name,a.region_name, a.zone_name\n" +
                "               \n" +
                "                order by a.region_name, a.district_name";
    }
}


