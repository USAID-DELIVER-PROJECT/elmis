package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.MSDStockStatusDTO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface MsdStockStatusReportMapper {

    @Select("SELECT s.mos,gz.code geoCode, case when gz.code ='MSDCentral' then\n" +
            " fn_get_MSD_MOS_color(s.mos::INT,1::INT) else fn_get_MSD_MOS_color(s.mos::INT,2::INT) end as color,\n" +
            " gz.name msdzone,p.code productcode,p.primaryname productName\n" +
            "  FROM msd_stock_statuses s \n" +
            "JOIN geographic_Zones gz ON s.geographiczoneid = gz.id\n" +
            "JOIN products p On p.id =s.productId\n" +
            "where gz.levelid=2 \n" +
            "order by gz.name")
    List<MSDStockStatusDTO> getAllMSDStockStatusReport(Long programId, Long periodId, String productCode);

    @Select("SELECT p.code productCode,p.primaryName productName,\n" +
            "SUM(CASE WHEN gz.name = 'MSD Central' then mos else 0 end) as msd_central,\n" +
            "SUM(CASE WHEN gz.name = 'Dar Es Salaam Zone' then mos else 0 end) as dar,\n" +
            "SUM(CASE WHEN gz.name = 'Mwanza Zone' then mos else 0 end) as mwnz,\n" +
            "SUM(CASE WHEN gz.name = 'Muleba Zone' then mos else 0 end) as muleba,\n" +
            "SUM(CASE WHEN gz.name = 'Tanga Zone' then mos else 0 end) as tanga,\n" +
            "SUM(CASE WHEN gz.name = 'Tabora Zone' then mos else 0 end) as tabr,\n" +
            "SUM(CASE WHEN gz.name = 'Mtwara Zone' then mos else 0 end) as mtr,\n" +
            "SUM(CASE WHEN gz.name = 'Moshi Zone' then mos else 0 end) as mosh,\n" +
            "SUM(CASE WHEN gz.name = 'Mbeya Zone' then mos else 0 end) as mbeya,\n" +
            "SUM(CASE WHEN gz.name = 'Iringa Zone' then mos else 0 end) as iringa,\n" +
            "SUM(CASE WHEN gz.name = 'Dodoma Zone' then mos else 0 end) as doodoma\n" +
            "\n" +
            "  FROM msd_stock_statuses s \n" +
            "JOIN geographic_Zones gz ON s.geographiczoneid = gz.id\n" +
            "JOIN products p On p.id =s.productId\n" +
            "where gz.levelid=2 \n" +
            "group by productCode,productname")
    List<HashMap<String,Object>>getStockStatus();

    @Select("select * from  fn_get_MSD_MOS_color(#{mos}::INT,#{levelId}::INT) ")
    HashMap<String,Object>getStockColor(@Param("mos")Long mos,@Param("levelId")Long levelId);
}
