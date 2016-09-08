package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.vaccine.repository.mapper.inventory.builder.VaccineInventoryReportQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface VaccineInventoryReportMapper {


    @SelectProvider(type = VaccineInventoryReportQueryBuilder.class, method = "getDistributionCompleteness")
    List<Map<String, String>> getDistributionCompletenessReport(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("districtId") Long districtId,
            RowBounds rowBounds
    );

    @SelectProvider(type = VaccineInventoryReportQueryBuilder.class, method = "getTotalDistributionCompleteness")
    Integer getTotalDistributionCompletenessReport(@Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate,
                                                   @Param("districtId") Long districtId);

    @Select("select max(f.id) facilityId,max(p.id) productid,max(d.periodid) period, f.name toFacility, p.primaryname product, sum(dl.quantity) quantity from vaccine_distribution_line_items dl\n" +
            "join vaccine_distributions d on d.id=dl.distributionid\n" +
            "join facilities f on f.id=d.tofacilityid\n" +
            "join products p on p.id=dl.productid\n" +
            "where d.periodid=#{periodId} and d.fromfacilityid=#{facilityId}\n" +
            "group by f.name, p.primaryname\n" +
            "order by f.name, productid")
    List<Map<String,String>> getDistributedFacilities(@Param("periodId")Long periodId, @Param("facilityId")Long facilityId,
                                                      RowBounds rowBounds);

    @Select("select count(Distinct d.tofacilityid) total from vaccine_distributions d\n" +
            "where d.periodid=#{periodId} and d.fromfacilityid=#{facilityId}")
    Integer getTotalDistributedFacilities(@Param("periodId")Long periodId, @Param("facilityId")Long facilityId);
}
