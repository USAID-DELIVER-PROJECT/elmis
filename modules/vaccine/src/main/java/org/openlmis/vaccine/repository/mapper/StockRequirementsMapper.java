package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.vaccine.dto.StockRequirementsDTO;
import org.openlmis.vaccine.dto.StockRequirements;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequirementsMapper {

    @Select("SELECT p.primaryName as productName,d.code dosageUnit, sr.* " +
            " FROM stock_requirements sr join products p on p.id=sr.productid " +
            " Join dosage_units d on p.dosageUnitId = d.id " +
            "WHERE sr.programid=#{programId} AND sr.facilityid=#{facilityId} AND sr.year=#{year}" +
            " order by sr.productId"
    )
    List<StockRequirementsDTO> getAllByProgramAndFacility(@Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param("year") int year);

    @Select("SELECT *" +
            " FROM stock_requirements " +
            " WHERE id=#{id}")
    StockRequirements getById(Long id);

    @Select("SELECT *" +
            " FROM stock_requirements " +
            " WHERE  facilityid=#{facilityId} AND programid=#{programId} AND productid=#{productId} AND year=#{year}")
    StockRequirementsDTO getByProductId(@Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param("productId") Long productId, @Param("year") int year);



    @Update("update stock_requirements " +
            " set " +
            " annualneed = #{annualNeed}," +
            " supplyperiodneed= #{supplyPeriodNeed}, " +
            " isavalue = #{isaValue}," +
            " reorderlevel = #{reorderLevel}, " +
            " bufferstock = #{bufferStock}, " +
            " maximumstock = #{maximumStock}, " +
            " modifieddate= NOW(), " +
            " currentPrice = #{currentPrice} "+
            " WHERE id=#{id} "
    )
    Integer update(StockRequirements requirements);



    @Insert("insert into stock_requirements  " +
            " (programid, facilityid, productid,productcategory,year, annualneed, supplyperiodneed, isavalue,reorderlevel,bufferstock," +
            "  maximumstock,createdby,createddate,modifiedby,modifieddate,currentPrice) " +
            " values " +
            " (#{programId}, #{facilityId},#{productId},#{productCategory},#{year}, #{annualNeed}, #{supplyPeriodNeed},#{isaValue},#{reorderLevel},#{bufferStock},#{maximumStock}," +
            " #{createdBy},NOW(),#{modifiedBy},NOW(), #{currentPrice}) ")
    @Options(useGeneratedKeys = true)
    Integer save(StockRequirements stockRequirements);


    @Delete("Delete from stock_requirements where programid=#{programId} and facilityid=#{facilityId} and year=#{year}")
    Integer deleteFacilityStockRequirements(@Param("programId")Long programId,@Param("facilityId")Long facilityId,@Param("year")int year);

    @Update("update stock_requirements " +
            " set " +
            " annualneed = 0," +
            " supplyperiodneed= 0, " +
            " isavalue = 0," +
            " reorderlevel = 0, " +
            " bufferstock = 0, " +
            " maximumstock = 0," +
            " modifieddate= NOW()," +
            " currentPrice = 0 " +
            "where programid=#{programId} and facilityid=#{facilityId} and year=#{year}")
    Integer resetFacilityStockRequirements(@Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param("year") int year);

    @Update("DELETE FROM stock_requirements " +
            "where programid=#{programId} and facilityid=#{facilityId} and year=#{year}")
    Integer deleteFacilityByStockRequirements(@Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param("year") int year);

    @Update("update stock_requirements " +
            " set " +
            " annualneed = #{annualNeed}," +
            " supplyperiodneed= #{supplyPeriodNeed}, " +
            " isavalue = #{isaValue}," +
            " reorderlevel = #{reorderLevel}, " +
            " bufferstock = #{bufferStock}, " +
            " maximumstock = #{maximumStock}," +
            " modifieddate= NOW()," +
            " currentPrice = #{currentPrice} " +
            "WHERE facilityid=#{facilityId} AND productid=#{productId} AND year=#{year}  "
    )
    Integer updateBundling(StockRequirementsDTO requirements);
}
