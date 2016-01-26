package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.stockmanagement.domain.Lot;
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
}
