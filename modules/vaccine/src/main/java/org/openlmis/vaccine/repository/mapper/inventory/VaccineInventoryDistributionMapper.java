package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VoucherNumberCode;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VaccineInventoryDistributionMapper {

    @Select("SELECT f.id ,f.code,f.name FROM requisition_group_members rgm\n" +
            "JOIN facilities f ON f.id=rgm.facilityid\n" +
            "JOIN requisition_groups rg ON rg.id=rgm.requisitiongroupid\n" +
            "JOIN supervisory_nodes sn ON sn.id= rg.supervisorynodeid\n" +
            "WHERE sn.facilityid=#{facilityId};")
    List<Facility> getOneLevelSupervisedFacilities(@Param("facilityId") Long facilityId);

    @Insert("insert into vaccine_distributions " +
            " (tofacilityid, fromfacilityid, vouchernumber, distributiondate, periodid,orderid,status, distributiontype, createdby, createddate, modifiedby,modifieddate,remarks )" +
            " values " +
            " (#{toFacilityId}, #{fromFacilityId}, #{voucherNumber}, #{distributionDate}, #{periodId}, #{orderId}, #{status},#{distributionType}, #{createdBy},NOW(),#{modifiedBy},NOW(),#{remarks}) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistribution(VaccineDistribution vaccineDistribution);

    @Update("update vaccine_distributions set " +
            " status=#{status}, modifiedby=#{modifiedBy}, modifieddate=NOW(),remarks = #{remarks} " +
            " where id=#{id}"
    )
    Integer updateDistribution(VaccineDistribution vaccineDistribution);

    @Insert("insert into vaccine_distribution_line_items " +
            " (distributionid, productid, quantity, vvmstatus, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{distributionId}, #{productId}, #{quantity}, #{vvmStatus}, #{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem);

    @Update("update vaccine_distribution_line_items set " +
            " quantity=#{quantity}, modifiedby=#{modifiedBy}, modifieddate=NOW() " +
            " where id=#{id}"
    )
    Integer updateDistributionLineItem(VaccineDistributionLineItem vaccineDistributionLineItem);

    @Insert("insert into vaccine_distribution_line_item_lots " +
            " (distributionlineitemid, lotid, quantity, vvmstatus, createdby, createddate, modifiedby,modifieddate )" +
            " values " +
            " (#{distributionLineItemId}, #{lotId}, #{quantity}, #{vvmStatus}, #{createdBy},NOW(),#{modifiedBy},NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer saveDistributionLineItemLot(VaccineDistributionLineItemLot lot);

    @Insert("update vaccine_distribution_line_item_lots set " +
            " quantity=#{quantity}, modifiedby=#{modifiedBy},modifieddate=NOW() " +
            " where id=#{id}"
    )
    Integer updateDistributionLineItemLot(VaccineDistributionLineItemLot lot);


    @Select("Select  pp.id, pp.name, pp.startdate::DATE, pp.enddate::DATE from requisition_groups rg " +
            " JOIN supervisory_nodes sn on rg.supervisorynodeId = sn.id " +
            " JOIN requisition_group_program_schedules RGS ON rg.id = rgs.requisitiongroupid " +
            " JOIN processing_schedules ps ON rgs.scheduleid = ps.id " +
            " JOIN processing_periods pp ON ps.id = pp.scheduleid " +
            " WHERE sn.facilityid = #{facilityId} and RGS.programid=#{programId} " +
            " AND   #{distributionDate}::DATE >= pp.startdate::DATE  AND #{distributionDate}::DATE <=pp.enddate::DATE LIMIT 1;")
    @Results(value = {
            @Result(property = "name", column = "name"),
            @Result(property = "startDate", column = "startdate"),
            @Result(property = "endDate", column = "enddate")
    })
    ProcessingPeriod getSupervisedCurrentPeriod(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("distributionDate") Date distributionDate);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE tofacilityid=#{facilityId} AND distributiontype='ROUTINE' AND EXTRACT(MONTH FROM distributionDate) = #{month} AND EXTRACT(YEAR FROM distributionDate) = #{year} LIMIT 1;"
    )
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems"))})
    VaccineDistribution getDistributionForFacilityByMonth(@Param("facilityId") Long facilityId, @Param("month") int month, @Param("year") int year);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE periodId=#{periodId} AND distributiontype='ROUTINE' AND" +
            " tofacilityid=#{facilityId} LIMIT 1 ")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems"))})
    VaccineDistribution getDistributionForFacilityByPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

    @Select("Select li.*,oli.quantityRequested from vaccine_distribution_line_items li " +
            " left outer JOIN vaccine_order_requisitions o ON o.id = (select orderid from vaccine_distributions where id=#{distributionId} limit 1) " +
            " left outer join Vaccine_order_requisition_line_items oli ON o.id=oli.orderId AND li.productId = oli.productId" +
            " where li.distributionid=#{distributionId}")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lots", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItemsLots")),
            @Result(property = "productId", column = "productId"),
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))})
    List<VaccineDistributionLineItem> getLineItems(@Param("distributionId") Long distributionId);

    @Select("SELECT *" +
            " FROM vaccine_distribution_line_item_lots" +
            " WHERE distributionlineitemid = #{distributionLineItemId}"
    )
    @Results({@Result(property = "lotId", column = "lotId"),
            @Result(
                    property = "lot", column = "lotId", javaType = Lot.class,
                    one = @One(select = "org.openlmis.stockmanagement.repository.mapper.LotMapper.getById"))
    })
    List<VaccineDistributionLineItemLot> getLineItemsLots(@Param("distributionLineItemId") Long distributionLineItemId);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE id=#{id}")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems"))})
    VaccineDistribution getById(@Param("id") Long id);

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE productid = #{productId} ")
    @Results({
            @Result(property = "lotCode", column = "lotnumber"),
    })
    List<Lot> getLotsByProductId(@Param("productId") Long productId);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE tofacilityid=#{facilityId} AND vouchernumber=#{voucherNumber} AND status='PENDING' LIMIT 1")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "fromFacility", column = "fromFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    VaccineDistribution getDistributionByVoucherNumber(@Param("facilityId") Long facilityId,@Param("voucherNumber") String voucherNumber);

    @Select("SELECT vouchernumber FROM vaccine_distributions WHERE vouchernumber LIKE '%/%/'||EXTRACT(YEAR FROM NOW())||'/%' ORDER BY createddate DESC LIMIT 1;")
    String getLastVoucherNumber();

    @Select("Select * from vw_vaccine_distribution_voucher_no_fields WHERE facilityid=#{facilityId}")
    VoucherNumberCode getFacilityVoucherNumberCode(@Param("facilityId") Long facilityId);

    @Select("select vd.Id, count(remarks) total, remarks, to_char(o.createdDate,'dd-MM-YYYY' ) orderDate   " +
            " from vaccine_distributions vd" +
            " JOIN vaccine_order_requisitions o on vd.orderId = O.ID  " +
            "where toFacilityId = #{facilityId} and notified = false and  " +
            " vd.status = 'PENDING' and remarks is not Null  group by remarks,vd.Id,o.createdDate  order by vd.Id  DESC limit 1 ")
    VaccineDistribution getAllDistributionsForNotification(@Param("facilityId") Long facilityId);

    @Select(" update vaccine_distributions SET notified = true WHERE id = #{Id} ")
    Long updateNotification(@Param("Id") Long Id);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE tofacilityid=#{facilityId} AND  status='PENDING' order by createddate ASC LIMIT 1")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "fromFacilityId", column = "fromFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "fromFacility", column = "fromFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    VaccineDistribution getDistributionByToFacility(@Param("facilityId") Long facilityId);

    @Select("select sn2.facilityid from supervisory_nodes sn1 " +
            " join supervisory_nodes sn2 on sn1.parentid=sn2.id " +
            " where sn1.facilityId=#{facilityId}")
    Long getSupervisorFacilityId(@Param("facilityId") Long facilityId);

}
