package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItem;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionLineItemLot;
import org.openlmis.vaccine.domain.inventory.VoucherNumberCode;
import org.openlmis.vaccine.dto.BatchExpirationNotificationDTO;
import org.openlmis.vaccine.dto.VaccineDistributionAlertDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface VaccineInventoryDistributionMapper {

    @Select("SELECT DISTINCT f.id ,f.code,f.name FROM requisition_group_members rgm\n" +
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
            " where li.distributionid=#{distributionId} order by li.id")
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
            "WHERE tofacilityid=#{facilityId} AND vouchernumber=#{voucherNumber} AND status='PENDING' order by distributiondate desc LIMIT 1")
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
            " WHERE tofacilityid=#{facilityId} AND  status='PENDING'  order by distributiondate desc LIMIT 1")
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

    @Select("     SELECT \n" +
            "            (select name toFacilityName from facilities where id =toFacilityId ),  \n" +
            "            (select name fromFacilityName from facilities where id =fromFacilityId ),\n" +
            "            (select cellphone from users where id=s.modifiedby),\n" +
            "            (select concat(firstname,' ',lastName) as modifiedBy from users where id=s.modifiedby),\n" +
            "            s.modifieddate,distributionDate,s.modifiedBy,voucherNumber,d.status,orderdate  \n" +
            "            FROM vaccine_distributions d  \n" +
            "            JOIN vaccine_distribution_status_changes s ON d.id = s.distributionId \n" +
            "            JOIN vaccine_order_requisitions o ON d.orderId = o.id \n" +
            "            WHERE d.status = 'PENDING' AND \n" +
            "            ((select current_date - s.modifiedDate::date) >=(select (((EXTRACT(EPOCH FROM CAST(  ( SELECT configuration_settings.value::integer FROM configuration_settings   \n" +
            "            WHERE configuration_settings.key::text = 'NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT'::text) || ' days' AS INTERVAL)  \n" +
            "            ) / 60) / 60) / 24)::integer)) AND \n" +
            "            fromFacilityId = #{facilityId} ")
    List<VaccineDistributionAlertDTO>getPendingConsignmentAlert(@Param("facilityId") Long facilityId);


    @Select("     SELECT \n" +
            "            (select name toFacilityName from facilities where id =toFacilityId ),  \n" +
            "            (select name fromFacilityName from facilities where id =fromFacilityId ),\n" +
            "            (select cellphone from users where id=s.modifiedby),\n" +
            "            (select concat(firstname,' ',lastName) as modifiedBy from users where id=s.modifiedby),\n" +
            "            s.modifieddate,distributionDate,s.modifiedBy,voucherNumber,d.status,orderdate  \n" +
            "            FROM vaccine_distributions d  \n" +
            "            JOIN vaccine_distribution_status_changes s ON d.id = s.distributionId \n" +
            "            JOIN vaccine_order_requisitions o ON d.orderId = o.id \n" +
            "            WHERE d.status = 'PENDING' AND \n" +
            "            ((select current_date - s.modifiedDate::date) >=(select (((EXTRACT(EPOCH FROM CAST(  ( SELECT configuration_settings.value::integer FROM configuration_settings   \n" +
            "            WHERE configuration_settings.key::text = 'NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT'::text) || ' days' AS INTERVAL)  \n" +
            "            ) / 60) / 60) / 24)::integer)) AND \n" +
            "            toFacilityId = #{facilityId}")
    List<VaccineDistributionAlertDTO>getPendingConsignmentToLowerLevel(@Param("facilityId") Long facilityId);

    @Select("select f.code, f.name, f.description, f.id from facilities f " +
            " join facility_types ft on f.typeid=ft.id " +
            " where ft.code =(select faty.code from facilities fa join facility_types faty on fa.typeid=faty.id where fa.id=#{facilityId}) " +
            " and f.id <> #{facilityId} and LOWER(f.name) LIKE '%' || LOWER(#{query}) || '%'")
    List<Facility> getFacilitiesSameType(@Param("facilityId") Long facilityId, @Param("query") String query);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE fromfacilityid=#{facilityId} AND  " +
            " distributiondate::DATE = #{date}::DATE AND distributionType='ROUTINE'" +
            " order by distributiondate DESC")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    List<VaccineDistribution> getDistributionsByDate(@Param("facilityId") Long facilityId, @Param("date") String date);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE fromfacilityid=#{facilityId} AND  " +
            " distributiondate::DATE >= #{date}::DATE AND distributiondate::DATE <= #{endDate}::DATE  AND distributionType= #{distributionType}  " +
            " order by distributiondate::DATE DESC")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    List<VaccineDistribution> getDistributionsByDateRange(@Param("facilityId") Long facilityId, @Param("date") String date, @Param("endDate") String endDate,@Param("distributionType") String distributionType);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE tofacilityid=#{facilityId} AND vouchernumber=#{voucherNumber} LIMIT 1")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "fromFacility", column = "fromFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    VaccineDistribution getDistributionByVoucherNumberIfExist(@Param("facilityId") Long facilityId, @Param("voucherNumber") String voucherNumber);

   @Select("  SELECT  p.primaryName product, l.lotNumber,expirationDate,manufacturerName,loh.quantityOnHand  " +
           "           FROM lots_on_hand loh  " +
           "           INNER JOIN lots l on loh.lotid = l.id  " +
           "           INNER JOIN stock_cards s on loh.stockcardId = s.id  " +
           "           LEFT JOIN products p ON s.productId = p.id and l.productId = p.Id  " +
           "           WHERE  facilityId = #{facilityId} AND p.active = true AND loh.quantityOnHand > 0 AND  " +
           "           expirationDate::date <= (SELECT current_date + (  " +
           "          (( SELECT configuration_settings.value::integer AS value  " +
           "           FROM configuration_settings  " +
           "           WHERE configuration_settings.key::text = 'NUMBER_OF_MONTH_FOR_BATCH_TO_EXPIRE'::text))::text || ' month')::interval)::date  " +
           "           order by expirationdate,lotnumber asc ")
    List<BatchExpirationNotificationDTO> getBatchExpiryNotifications(@Param("facilityId") Long facilityId);

    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE id=#{id} LIMIT 1")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "fromFacilityId", column = "fromFacilityId"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "fromFacility", column = "fromFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
    })
    VaccineDistribution getDistributionById(@Param("id") Long id);


    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            " WHERE tofacilityid=#{facilityId} AND  " +
            " distributiondate::DATE >= #{startDate}::DATE  and " +
            " distributiondate::DATE <= #{endDate}::DATE  " +
            " order by createddate DESC")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    List<VaccineDistribution> getDistributionsByDateRangeAndFacility(@Param("facilityId") Long facilityId,
                                                                     @Param("startDate") String startDate,
                                                                     @Param("endDate") String endDate);
    @Select("SELECT * from vaccine_distributionS s   " +
            "where tofacilityId = #{facilityId}  AND    " +
            " ((SELECT FT.CODE FROM FACILITIES   " +
            " JOIN FACILITY_TYPES FT ON facilities.typeId = FT.ID  " +
            " WHERE facilities.ID = toFacilityId)  =  " +
            "  (SELECT FT.CODE FROM FACILITIES  " +
            " JOIN FACILITY_TYPES FT ON facilities.typeId = FT.ID   " +
            " WHERE facilities.ID = FROMFacilityId)) AND ORDERID IS NULL  AND " +
            " distributiondate::DATE >= #{startDate}::DATE  and " +
            " distributiondate::DATE <= #{endDate}::DATE  " +
            " order by createddate DESC")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    List<VaccineDistribution> getDistributionsByDateRangeForFacility(@Param("facilityId") Long facilityId,
                                                                     @Param("startDate") String startDate,
                                                                     @Param("endDate") String endDate);


    @Select("SELECT *" +
            " FROM vaccine_distributions " +
            "WHERE fromfacilityid=#{facilityId} AND vouchernumber=#{voucherNumber}")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "fromFacility", column = "fromFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    VaccineDistribution getAllDistributionsByVoucherNumber(@Param("facilityId") Long facilityId,@Param("voucherNumber") String voucherNumber);

    @Select("SELECT * " +
            " FROM vaccine_distributions " +
            " WHERE fromfacilityid=#{facilityId} AND  " +
            " distributiondate::DATE >= #{startDate}::DATE AND distributiondate::DATE <= #{endDate}::DATE  AND distributionType= #{distributionType} " +
            " AND toFacilityId= ( " +
            " SELECT f.id FROM FACILITIES F " +
            " LEFT JOIN vaccine_distributions vd on F.id = toFacilityId " +
            " WHERE  LOWER(name) LIKE '%' || LOWER(#{searchParam}) || '%' and fromfacilityid=#{facilityId}  LIMIT 1 )"+
            " " +
            " order by createddate DESC")
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "toFacilityId", column = "toFacilityId"),
            @Result(property = "lineItems", column = "id", javaType = List.class,
                    many = @Many(select = "getLineItems")),
            @Result(property = "toFacility", column = "toFacilityId", javaType = Facility.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    List<VaccineDistribution> searchDistributionAndFacilityByDateRange(@Param("facilityId") Long facilityId,
                                                                     @Param("startDate") String startDate,
                                                                     @Param("endDate") String endDate,
                                                                       @Param("distributionType") String distributionType,
                                                                       @Param("searchParam") String searchParam);

    @Select(" select * from vaccine_distributions where status='PENDING' and toFacilityId=#{facilityId}")
    List<VaccineDistribution>getReeceiveNotiication(@Param("facilityId")Long facilityId);


    @Select("     SELECT \n" +
            "            (select name toFacilityName from facilities where id =toFacilityId ),  \n" +
            "            (select name fromFacilityName from facilities where id =fromFacilityId ),\n" +
            "            (select cellphone from users where id=s.modifiedby),\n" +
            "            (select concat(firstname,' ',lastName) as modifiedBy from users where id=s.modifiedby),\n" +
            "            s.modifieddate,distributionDate,s.modifiedBy,voucherNumber,d.status,orderdate  \n" +
            "            FROM vaccine_distributions d  \n" +
            "            JOIN vaccine_distribution_status_changes s ON d.id = s.distributionId \n" +
            "            JOIN vaccine_order_requisitions o ON d.orderId = o.id \n" +
            "            WHERE d.status = 'PENDING' AND \n" +
            "            toFacilityId = #{facilityId} ")
    List<VaccineDistributionAlertDTO>getReceiveDistributionAlert(@Param("facilityId") Long facilityId);

@Select(" WITH r AS(\n" +
        "                             select * from stock_requirements R\n" +
        "                            JOIN stock_cards s ON  r.facilityId = S.FACILITYiD and r.productId = S.PRODUCTiD \n" +
        "                            JOIN products p ON s.productId = p.id\n" +
        "                            where s.facilityId=#{facilityId} and year = extract(year from NOW()::Date)\n" +
        "                            )SELECT * FROM r\n" +
        "                            WHERE( select fn_get_vaccine_stock_color(r.maximumstock::int, r.reorderlevel::int, r.bufferstock::int, R.TOTALQUANTITYONHAND::int) \n" +
        "                           = (SELECT value FROM configuration_settings WHERE LOWER(key) = LOWER('STOCK_LESS_THAN_BUFFER_COLOR') LIMIT 1))\n")
    List<Map<String,Object>>getMinimumStockNotification(@Param("facilityId")Long facilityId);

    @Select("\n" +
            "SELECT * FROM VACCINE_DISTRIBUTIONS WHERE STATUS= #{status} AND DISTRIBUTIONDATE=#{distributionDate}::DATE\n" +
            " AND TOFACILITYID=#{toFacilityId} AND distributionType=#{distributionType} ORDER BY CREATEDDATE DESC LIMIT 1")
    List<HashMap<String,Object>> getLastDistributionForFacility(@Param("toFacilityId") Long toFacilityId,@Param("distributionType")String distributionType,@Param("distributionDate") String distributionDate,@Param("status") String status);
}
