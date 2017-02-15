package org.openlmis.vaccine.repository.mapper.OrderRequisitions;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.openlmis.vaccine.dto.VaccineOnTimeInFullDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineOrderRequisitionMapper {

    @Insert("INSERT INTO vaccine_order_requisitions (periodId,programId,status,supervisoryNodeId,facilityId,orderDate," +
            " createdBy, createdDate,modifiedBy,modifiedDate,emergency,reason )    " +
            "VALUES (#{periodId},#{programId},#{status},#{supervisoryNodeId},#{facilityId},#{orderDate}," +
            "#{createdBy}, NOW(),#{modifiedBy},NOW(),#{emergency},#{reason} )")
    @Options(useGeneratedKeys = true)
    Integer insert(VaccineOrderRequisition orderRequisition);

    @Update("Update vaccine_order_requisitions SET " +
            "periodId = #{periodId}, " +
            "programId = #{programId}, " +
            "status = #{status}, " +
            "supervisoryNodeId = #{supervisoryNodeId}, " +
            "facilityId = #{facilityId}, " +
            "orderDate = #{orderDate}," +
            "modifiedBy = #{createdBy}, " +
            "modifiedDate = #{modifiedDate}," +
            "emergency = #{emergency}, " +
            "reason  = #{reason} " +
            "WHERE id = #{id} ")
    void update(VaccineOrderRequisition orderRequisition);

    @Update("Update vaccine_order_requisitions SET   "
            +" status = 'ISSUED'  " +
            "WHERE id = #{orderId}  ")
    Long updateORStatus(@Param("orderId") Long orderId);

    @Select(" SELECT * FROM vaccine_order_requisitions WHERE periodId = #{periodId}  and  programId = #{programId} AND facilityId = #{facilityId} and emergency=false")
    VaccineOrderRequisition getByFacilityProgram(@Param("periodId") Long periodId, @Param("programId") Long programId, @Param("facilityId") Long facilityId);

    @Select("select * from vaccine_order_requisitions " +
            "   where " +
            "   facilityId = #{facilityId} and programId = #{programId} order by id desc limit 1")
    VaccineOrderRequisition getLastReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId);



    @Select(" select * from vaccine_order_requisitions where id = #{id} ")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "periodId", column = "periodId"),
            @Result(property = "facilityId", column = "facilityId"),
            @Result(property = "programId", column = "programId"),
            @Result(property = "lineItems", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineOrderRequisitionLineItemsMapper.getLineItems")),
            @Result(property = "statusChanges", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.vaccine.repository.mapper.OrderRequisitions.VaccineStatusRequisitionChangeMapper.getChangeLogByReportId")),
            @Result(property = "facility", javaType = Facility.class, column = "facilityId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "program", javaType = Program.class, column = "programId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
            @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId",
                    many = @Many(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById"))
    })
    VaccineOrderRequisition getAllOrderDetails(@Param("id") Long id);



    @Select("select max(s.scheduleId) id from requisition_group_program_schedules s " +
            " join requisition_group_members m " +
            "     on m.requisitionGroupId = s.requisitionGroupId " +
            " where " +
            "   s.programId = #{programId} " +
            "   and m.facilityId = #{facilityId} ")
    Long getScheduleFor(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("SELECT DISTINCT f.id AS facilityId,r.periodId,f.name facilityName,R.orderDate orderDate,r.createdDate,R.ID,R.STATUS,ra.programid AS programId,pp.name periodName  " +
            "   FROM facilities f  " +
            "     JOIN requisition_group_members m ON m.facilityid = f.id  " +
            "     JOIN requisition_groups rg ON rg.id = m.requisitiongroupid  " +
            "     JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid  " +
            "     JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid " +
            "     JOIN vaccine_order_requisitions r on f.id = r.facilityId and sn.id = r.supervisorynodeid " +
            "     JOIN processing_periods pp on r.periodId = pp.id " +
            "     WHERE ra.userId = #{userId} AND R.STATUS  IN('SUBMITTED') AND  isVerified = false AND r.programId = #{programId} AND sn.facilityId = #{facilityId}")
      List<OrderRequisitionDTO> getPendingRequest(@Param("userId") Long userId, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Select("select r.id, p.name as periodName, r.facilityId, r.status, r.programId " +
            " from vaccine_order_requisitions r " +
            "   join processing_periods p on p.id = r.periodId " +
            " where r.facilityId = #{facilityId} and r.programId = #{programId}" +
            " order by p.startDate desc ")
    List<OrderRequisitionDTO> getReportedPeriodsForFacility(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("Select id from vaccine_order_requisitions where facilityid = #{facilityId} and periodid = #{periodId}")
    Long getReportIdForFacilityAndPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

    @Select("select * from vaccine_order_requisitions r " +
            "JOIN vaccine_order_requisition_line_items li on r.id = li.orderId  " +
            " WHERE programId = #{programId} AND periodId = #{periodId} and facilityId = #{facilityId} and R.STATUS  IN('SUBMITTED') ")
    List<OrderRequisitionDTO> getAllBy(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("facilityId") Long facilityId);

    @Select("select r.id,p.name programName, f.name facilityName,r.status,pp.startdate periodStartDate,pp.enddate periodEndDate,emergency, orderDate,r.createdDate  from vaccine_order_requisitions r   " +
            "JOIN programs p on r.programId =p.id  " +
            "JOIN processing_periods pp on r.periodId = pp.id  " +
            "JOIN facilities f on r.facilityId= f.id    "+
            " WHERE programId = #{programId} AND r.createdDate::date >= #{dateRangeStart}::date and r.createdDate::date <= #{dateRangeEnd}::date  " +
            " and facilityId = #{facilityId} and R.STATUS  IN('SUBMITTED','ISSUED')")
    List<OrderRequisitionDTO> getSearchedDataBy(@Param("facilityId") Long facilityId,
                                                @Param("dateRangeStart") String dateRangeStart,
                                                @Param("dateRangeEnd") String dateRangeEnd
                                                ,@Param("programId") Long programId);


    @Select("SELECT ic.* FROM isa_coefficients ic \n" +
            "JOIN facility_program_products fpp ON fpp.isaCoefficientsId = ic.id \n" +
            "JOIN program_products pp on fpp.programproductId = pp.id\n" +
            "JOIN products p on p.id = pp.productId\n" +
            "WHERE facilityId = #{facilityId}")
    List<OrderRequisitionStockCardDTO>getAllByFacility(@Param("facilityId") Long facilityId,
                                                       @Param("programId") Long programId);


    @Select("SELECT *" +
            " FROM vw_stock_cards" +
            " WHERE facilityid = #{facilityId}" +
            " AND programid = #{programId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "product", column = "productId", javaType = Product.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
            @Result(property = "keyValues", column = "id", javaType = List.class,
                    one = @One(select = "org.openlmis.stockmanagement.repository.mapper.StockCardMapper.getStockCardKeyValues")),
            @Result(property = "entries", column = "id", javaType = List.class,
                    many = @Many(select = "org.openlmis.stockmanagement.repository.mapper.StockCardMapper.getEntries")),
            @Result(property = "lotsOnHand", column = "id", javaType = List.class,
                    many = @Many(select = "org.openlmis.stockmanagement.repository.mapper.StockCardMapper.getLotsOnHand"))

    })
    List<OrderRequisitionStockCardDTO> getAllByFacilityAndProgram(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

    @Select("select * from supervisory_nodes where facilityId = #{facilityId} ")
    List<OrderRequisitionDTO>getSupervisoryNodeByFacility(@Param("facilityId") Long facilityId);

   @Select(" select o.periodId, o.id orderId, facilityId, (select name from facilities where id = o.facilityId) facilityName,  " +
           "li.quantityRequested,li.productName,p.code productCode,p.id productId  " +
           " from vaccine_order_requisitions o  " +
           "JOIN vaccine_order_requisition_line_items li ON o.id = li.orderId " +
           "JOIN products p ON li.productId = p.Id " +
           "WHERE status = 'SUBMITTED' AND programId = #{program} AND facilityId = ANY(#{facilityIds}::int[])" +
           " ORDER BY p.id ")
    List<OrderRequisitionDTO> getConsolidatedList(@Param("program") Long program,@Param("facilityIds") String facilityIds);

    @Update("Update vaccine_order_requisitions SET   "
            +" isVerified = true " +
            "WHERE id = #{orderId}  ")
    Long verifyVaccineOrderRequisition(@Param("orderId") Long orderId);

    @Select("SELECT COUNT(DISTINCT f.id) as totalPending " +
            "   FROM facilities f  " +
            "     JOIN requisition_group_members m ON m.facilityid = f.id  " +
            "     JOIN requisition_groups rg ON rg.id = m.requisitiongroupid  " +
            "     JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid  " +
            "     JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid " +
            "     JOIN vaccine_order_requisitions r on f.id = r.facilityId and sn.id = r.supervisorynodeid " +
            "     JOIN processing_periods pp on r.periodId = pp.id " +
            "     WHERE ra.userId = #{userId} AND R.STATUS  IN('SUBMITTED') AND  isVerified = false AND r.programId = #{programId} AND sn.facilityId = #{facilityId}")
    Integer getTotalPendingRequest(@Param("userId") Long userId, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Select("   SELECT y.id productId, y.product productName,y.quantityRequested,x.QuantityReceived,requestedDate, distributiondate receivedDate from   " +
            "   (    "+
            "   SELECT p.id, p.primaryName product, dl.quantity QuantityReceived,distributiondate FROM vaccine_distributions d   " +
            "   JOIN vaccine_distribution_line_items dl on d.id = dl.distributionId   " +
            "   JOIN products p on p.id = dl.productId    " +
            "   JOIN program_products  pp ON pp.productId = p.id    " +
            "   JOIN product_categories pc ON pp.productCategoryId = PC.ID      " +
            "   WHERE d.status = 'RECEIVED' and toFacilityId=#{facilityId}  and d.periodId = #{periodId} and d.orderId = #{orderId}   " +
            "   order by pc.displayOrder   " +
            "   ) x LEFT JOIN (     " +
            "   SELECT p.id,p.primaryName product, quantityRequested,o.createddate requestedDate FROM vaccine_order_requisitions o\n" +
            "   JOIN vaccine_order_requisition_line_items li on li.orderid = o.id    " +
            "   JOIN products p on p.id = li.productId     " +
            "   JOIN program_products  pp ON pp.productId = p.id   " +
            "   JOIN product_categories pc ON pp.productCategoryId = PC.ID     " +
            "   WHERE o.status = 'ISSUED' and facilityId=#{facilityId}  and periodId = #{periodId} and o.id = #{orderId}    " +
            "   order by pc.displayOrder " +
            "   ) Y ON x.id = y.id   ")

    List<VaccineOnTimeInFullDTO> getOnTimeInFullData(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId,@Param("orderId") Long orderId);

    @Select("select vd.status,vd.modifiedDate receivedDate, vd.distributionType,   " +
            "pp.id periodId, r.id,p.name programName,    " +
            "f.id facilityId, f.name facilityName,pp.startdate periodStartDate,pp.enddate periodEndDate,emergency, orderDate,r.createdDate  from vaccine_order_requisitions r   " +
            "JOIN vaccine_distributions vd ON r.id = vd.orderId  " +
            "JOIN programs p on r.programId =p.id  " +
            "JOIN processing_periods pp on r.periodId = pp.id  " +
            "JOIN facilities f on r.facilityId= f.id    "+
            " WHERE programId = #{programId} AND r.createdDate::date >= #{dateRangeStart}::date and r.createdDate::date <= #{dateRangeEnd}::date  " +
            " and facilityId = #{facilityId} and vd.STATUS  IN('RECEIVED')")
    List<OrderRequisitionDTO> getSearchedDataForOnTimeReportingBy(@Param("facilityId") Long facilityId,
                                                @Param("dateRangeStart") String dateRangeStart,
                                                @Param("dateRangeEnd") String dateRangeEnd
                                                ,@Param("programId") Long programId);


}

