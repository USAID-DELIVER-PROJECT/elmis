package org.openlmis.report.builder;

public class RejectedRnRReportQueryBuilder {

    public static String getQuery(){

        return "select districtId,district_name districtName,region_name as regionName,zone_name as zoneName, to_char(createdDate, 'yyyy_mm') as Month, count(*) rejectedCount from ( \n" +
                "    select count(*), max(c.createdDate) as createdDate, rnrid, zone_name,d.district_id districtId,d.district_name,d.region_name from requisition_status_changes c \n" +
                "    join requisitions r on r.id = c.rnrid \n" +
                "    join facilities f on f.id = r.facilityid \n" +
                "    join vw_districts d on d.district_id = f.geographiczoneid \n" +
                "    where c.status = 'INITIATED'  and r.programid =1 group by rnrid, d.zone_name,d.district_id ,d.district_name,d.region_name having count(*) > 1\n" +
                "   \n" +
                ") a\n" +
                "group by districtId, a.district_name,a.region_name, a.zone_name, to_char(createdDate, 'yyyy_mm')\n" +
                "order by to_char(createdDate, 'yyyy_mm')";
    }
}
