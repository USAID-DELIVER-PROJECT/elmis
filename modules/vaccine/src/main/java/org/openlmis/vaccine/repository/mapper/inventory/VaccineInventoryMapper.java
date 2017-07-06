package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineInventoryMapper {

    @Select("SELECT *" +
            " FROM lots" +
            " WHERE productid = #{productId} and expirationdate > NOW() ")
    @Results({
            @Result(property = "lotCode", column = "lotnumber"),
    })
    List<Lot> getLotsByProductId(@Param("productId") Long productId);

    //TODO To delete this code on production

    @Delete("delete from vaccine_order_requisition_line_items")
    Integer deleteRequisitionLineItems();

    @Delete("delete from vaccine_order_requisitions")
    Integer deleteRequisitions();

    @Delete("delete from vaccine_distribution_line_item_lots")
    Integer deleteDistributionLots();

    @Delete("delete from vaccine_distribution_line_items")
    Integer deleteDistributionLineItems();

    @Delete("delete from vaccine_distributions")
    Integer deleteDistributions();

    @Delete("DELETE FROM stock_card_entry_key_values")
    Integer deleteEntryKeyValue();

    @Delete("DELETE FROM stock_card_entries")
    Integer deleteEntries();

    @Delete("DELETE FROM lots_on_hand")
    Integer deleteLotsOnHand();

    @Delete("DELETE FROM stock_cards")
    Integer deleteStockCards();

    @Delete("DELETE FROM lots")
    Integer deleteLots();

    @Delete("DELETE FROM vaccine_distribution_status_changes")
    void deleteDistributionStatus();


    //TODO End To delete this code on production

    //Log Tag Query Start

    @Insert("INSERT INTO log_tags(logDate,logTime,temperature,serialNumber,createdDate)  " +
            "VALUES(#{logDate},#{logTime},#{temperature},#{serialNumber},NOW())")
    @Options(useGeneratedKeys = true)
    void saveLogTagData(LogTagDTO dto);

    @Select("select * from log_tags WHERE logDate = #{logDate} AND serialNumber = #{serialNumber}")
    LogTagDTO geByDateAndSerialNumber(@Param("logDate")String logDate, @Param("serialNumber") String serialNumber);

    @Update("UPDATE log_tags\n" +
            "   SET  logDate=#{logDate}, logTime=#{logTime},\n" +
            "    temperature=#{temperature}, serialNumber=#{serialNumber},\n" +
            "     createdDate=NOW() where id =#{id} ")
    void updateLogTag(LogTagDTO dto);

    @Select("select * from log_tags where createdDate >=#{startDate}::DATE AND createdDate <=#{endDate}::DATE ")
    List<LogTagDTO> getLogTags(@Param("startDate") String startDate,@Param("endDate") String endDate, RowBounds rowBounds);




}
