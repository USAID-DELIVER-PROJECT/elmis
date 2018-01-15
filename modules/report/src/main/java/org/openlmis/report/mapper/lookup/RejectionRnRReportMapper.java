package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface RejectionRnRReportMapper {

    @Select("    select facilityName,districtId,district_name districtName,region_name as regionName,zone_name as zoneName, to_char(createdDate, 'yyyy_mm') as Month, count(*) rejectedCount from ( \n" +
            "\n" +
            "                    select f.name facilityName, count(*), max(c.createdDate) as createdDate, rnrid, zone_name,d.district_id districtId,\n" +
            "                    d.district_name,d.region_name from requisition_status_changes c \n" +
            "                    join requisitions r on r.id = c.rnrid\n" +
            "                    join facilities f on f.id = r.facilityid \n" +
            "                   join vw_districts d on d.district_id = f.geographiczoneid \n" +
            "                   JOIN users u ON u.id = c.modifiedBy\n" +
            "                   where c.status = #{status} and r.programid =#{program} and periodId=#{period}\n" +
            "                      and lower(d.zone_name) = lower(#{zone})\n" +
            "                    group by f.name,rnrid, d.zone_name,d.district_id ,d.district_name,d.region_name having count(*) > 1\n" +
            "                \n" +
            "                ) a\n" +
            "                group by facilityName,districtId, a.district_name,a.region_name, a.zone_name, to_char(createdDate, 'yyyy_mm')\n" +
            "                order by districtId, to_char(createdDate, 'yyyy_mm')")
    List<HashMap<String,Object>>getRnRRejected(@Param("status") String status,@Param("program")Long program, @Param("period")Long period,@Param("zone")String zone, RowBounds rowBounds);

}
