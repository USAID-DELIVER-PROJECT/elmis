package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.StockOnHandSummaryDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 9/10/17.
 */

@Repository
public interface StockOnHandSummaryMapper {

    @Select("     WITH Q AS (  SELECT  x.* , r.isaValue,r.bufferStock, \n" +
            "                           case when x.soh > r.maximumstock then 1 else 0 end as blue,\n" +
            "                           case when x.soh <= maximumstock AND x.soh  >= reorderlevel then 1 else 0 end as green,\n" +
            "                           case when x.soh < reorderlevel AND x.soh >= r.bufferstock then 1 else 0 end as yellow,\n" +
            "                           case when  x.soh >= r.bufferstock then 1 else 0 end as adequacy,\n" +
            "                           (          \n" +
            "                           select fn_get_vaccine_stock_color(r.maximumstock::int, reorderlevel::int, bufferstock::int, x.soh::int)\n" +
            "                          )  color           \n" +
            "                          FROM (             \n" +
            "                         SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*   \n" +
            "                          FROM  (                             \n" +
            "                       SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
            "                          p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,   \n" +
            "                         e.modifiedDate::timestamp lastUpdate   \n" +
            "                           FROM stock_cards s   \n" +
            "                          JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "                          JOIN program_products pp ON s.productId = pp.productId   \n" +
            "                         JOIN programs ON pp.programId = programs.id   \n" +
            "                          JOIN products p ON pp.productId = p.id      \n" +
            "                          JOIN facilities f ON s.facilityId = f.id  \n" +
            "                          JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
            "                          JOIN facility_types  ON f.typeId = facility_types.Id   \n" +
            "                       AND d.district_id in (select district_id from vw_user_facilities where user_id = '307'::INT and program_id = fn_get_vaccine_program_id()) \n" +
            "\n" +
            "                               ORDER BY e.modifiedDate ) t) x \n" +
            "                         JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid\n" +
            "                        WHERE  x.r <= 1 and r.year = (SELECT date_part('YEAR', current_date))        \n" +
            "                      ORDER BY facilityId,productId ) \n" +
            "                          SELECT facilityId,districtId,regionId, productId, facilityName,district,region,product,lastUpdate,soh,isaValue,bufferStock,  \n" +
            "                         CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as mos,color, adequacy,\n" +
            "                          sum(adequacy) OVER (PARTITION BY facilityId) adequacy2, count(productId) OVER (PARTITION BY facilityId) total,\n" +
            "                          CASE WHEN count(productId) OVER (PARTITION BY facilityId) > 0 THEN ROUND( sum(adequacy) OVER (PARTITION BY facilityId) / count(productId) OVER (PARTITION BY facilityId),0) ELSE 0 END AS adequacy3 \n" +
            "                           FROM Q  \n" +
            "                           ORDER BY region ASC \n" +
            "\n")
    List<StockOnHandSummaryDTO> getStockOnHandSummary(@Param("userId") Long userId,@Param("statusDate") String statusDate);

}
