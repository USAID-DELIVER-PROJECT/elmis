/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.openlmis.report.builder.LabEquipmentStatusByLocationQueryBuilder;
import org.openlmis.report.model.*;
import org.openlmis.report.model.dto.FlatGeographicZone;
import org.openlmis.report.model.dto.GeoZoneTree;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.model.dto.GeographicZoneJsonDto;
import org.openlmis.report.model.geo.GeoFacilityIndicator;
import org.openlmis.report.model.geo.GeoStockStatusFacility;
import org.openlmis.report.model.geo.GeoStockStatusProduct;
import org.openlmis.report.model.geo.GeoStockStatusProductConsumption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneReportMapper {

    @Select("SELECT g.* , p.id as parentId" +
            "   FROM " +
            "       geographic_zones g left join geographic_zones p on g.parentId = p.id order by p.name, g.name")
    List<GeographicZone> getAll();

    @Select("SELECT * FROM geographic_zones gz INNER JOIN geographic_levels gl ON gz.levelId = gl.id " +
            "  where levelId = #{geographicLevelId} ORDER BY gz.id,gl.id")
    List<GeographicZone> getGeographicZoneByLevel(Long id);

    @Select("select gz2.name ADM1,gz1.name ADM2, gz.name ADM3, gz.* from geographic_zones gz " +
            "left join geographic_zones gz1  " +
            "   on gz.parentId = gz1.id " +
            " left join geographic_zones gz2 " +
            "   on gz1.parentId = gz2.id" +
            " order by ADM1, ADM2, ADM3")
    List<FlatGeographicZone> getFlatGeographicZoneList();

  @Select("select gzz.id, gzz.name, gjson.geometry,COALESCE(expected.count) expected, COALESCE(total.count) total, COALESCE(ever.count,0) as ever, COALESCE(period.count,0) as period  " +
    " from  " +
    " geographic_zones gzz " +
    " left join  " +
    " geographic_zone_geojson gjson on  " +
    " gzz.id = gjson.zoneId " +
    " left join " +
    " (select geographicZoneId, count(*) from facilities  " +
    " join programs_supported ps on ps.facilityId = facilities.id " +
    " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
    " join requisition_group_members rgm on rgm.facilityId = facilities.id " +
    " join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  " +
    " join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = #{processingPeriodId}  " +
    " where gz.levelId = (select max(id) from geographic_levels) and ps.programId = #{programId} " +
    " group by geographicZoneId" +
    " ) expected " +
    " on gzz.id = expected.geographicZoneId " +
    " left join " +
    " (select geographicZoneId, count(*) from facilities  " +
    " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
    " where gz.levelId = (select max(id) from geographic_levels)  " +
    " group by geographicZoneId" +
    " ) total " +
    " on gzz.id = total.geographicZoneId " +
    " left join  " +
    " (select geographicZoneId, count(*) from facilities  " +
    " join programs_supported ps on ps.facilityId = facilities.id " +
    " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
    " where ps.programId = #{programId} and facilities.id in  " +
    "(select facilityId from requisitions where programId = #{programId} ) " +
    "group by geographicZoneId" +
    " ) ever " +
    " on gzz.id = ever.geographicZoneId " +
    " left join " +
    " (select geographicZoneId, count(*) from facilities  " +
    " join programs_supported ps on ps.facilityId = facilities.id " +
    " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
    " where  ps.programId = #{programId} and facilities.id in  " +
    " (select facilityId from requisitions where periodId = #{processingPeriodId} and programId = #{programId} and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and emergency = false ) " +
    " group by geographicZoneId" +
    " ) period" +
    " on gzz.id = period.geographicZoneId order by gzz.name" )
    List<GeoZoneReportingRate> getGeoReportingRate(@Param("userId") Long userId, @Param("programId") Long programId,@Param("schedule") Long schedule, @Param("processingPeriodId") Long processingPeriodId);



    @Select("select   f.id, f.name, f.mainPhone, f.longitude, f.latitude,false reported ,\n" +
            "   (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts,  \n" +
            "                         ( SELECT count(*) >0   \n" +
            "                          FROM role_assignments  \n" +
            "                          JOIN supervisory_nodes on supervisory_nodes.id = role_assignments.supervisorynodeid  \n" +
            "                          JOIN users on users.id = role_assignments.userid AND users.active = true  \n" +
            "                          WHERE supervisory_nodes.facilityid = f.id  \n" +
            "                         ) as hasSupervisors  \n" +
            "                         from facilities f \n" +
            " INNER JOIN requisition_group_members rgm on rgm.facilityid = f.id\n" +
            " INNER JOIN vw_districts gz on gz.district_id = f.geographiczoneid\n" +
            " INNER JOIN facility_types ft on ft.id = f.typeid\n" +
            " INNER JOIN programs_supported ps on ps.facilityid = f.id\n" +
            " INNER JOIN requisition_group_program_schedules rgps on rgps.requisitiongroupid =\n" +
            " rgm.requisitiongroupid and ps.programid = rgps.programid\n" +
             " WHERE (f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{programId}) \n" +
            " AND f.id not in (\n" +
            " select r.facilityid from requisitions r where r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')  and r.periodid =  #{periodId} and r.programid =#{programId} )" +
            " AND  (gz.district_id =  #{geographicZoneId} or gz.zone_id = #{geographicZoneId} or gz.region_id = #{geographicZoneId}\n" +
            " or gz.parent = #{geographicZoneId} ) AND ps.programId = #{programId} AND rgps.scheduleId = #{schedule})\n" +
            " ORDER BY name")
    List<GeoFacilityIndicator> getNonReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId,
                                                         @Param("periodId") Long processingPeriodId,
                                                         @Param("schedule") Long schedule,
                                                         @Param("userId") Long userId);

    @Select("select distinct rq.id rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true reported, (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ( SELECT count(*) >0 " +
            "  FROM role_assignments" +
            "  JOIN supervisory_nodes on supervisory_nodes.id = role_assignments.supervisorynodeid" +
            "  JOIN users on users.id = role_assignments.userid AND users.active = true" +
            "  WHERE supervisory_nodes.facilityid = f.id" +
            "  ) as hasSupervisors" +
            " from facilities f " +
            " join (select facilityId, r.id from requisitions r where r.programId = #{programId} and r.periodId = #{periodId} and emergency = false and status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')) rq on rq.facilityId = f.id " +
            " join programs_supported ps on ps.facilityId = f.id " +
            " join geographic_zones gz on gz.id = f.geographicZoneId " +
            " join requisition_group_members rgm on rgm.facilityId = f.id " +
            " JOIN requisition_groups rg ON rg.id = rgm.requisitiongroupid" +
            " JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid" +
            " JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid" +
            " where  f.enabled = true" +
            " and f.geographicZoneId = #{geographicZoneId}" +
            " and ra.userid = #{userId}" +
            " and ra.programid = #{programId}" +
            " order by f.name")
    List<GeoFacilityIndicator> getReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId,
                                                      @Param("userId") Long userId);


    @Select("select gz.id, gz.name || ' - ' || l.name as name, gz.* from geographic_zones gz join geographic_levels l on l.id = gz.levelid where parentId is null")
    GeoZoneTree getParentZoneTree();

    @Select("select distinct gz.id, gz.name || ' - ' || gl.name as name, gz.* from geographic_zones gz join geographic_levels gl on gl.id = gz.levelId " +
            " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.program_id = #{programId} and vud.user_id = #{userId}) sq" +
            " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
    List<GeoZoneTree> getGeoZonesForUserByProgram(@Param("userId") Long userId, @Param("programId") Long programId);


    @Select("select distinct gz.id, gz.name || ' - ' || gl.name as name, gz.* from geographic_zones gz join geographic_levels gl on gl.id = gz.levelId  " +
                " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.program_id = #{programId} and vud.user_id = #{userId} ) sq" +
                " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
    List<GeoZoneTree> getGeoZones(@Param("programId")Long ProgramId,@Param("userId") Long userId);

        @Select("select distinct gz.id, gz.name || ' - ' || gl.name as name, gz.* from geographic_zones gz join geographic_levels gl on gl.id = gz.levelId  " +
                " join (select vd.* from vw_districts vd join vw_user_districts vud on vud.district_id = vd.district_id where vud.user_id = #{userId}) sq" +
                " on sq.district_id = gz.id or sq.zone_id = gz.id or gz.id = sq.region_id or gz.id = sq.parent")
        List<GeoZoneTree> getGeoZonesForUser(@Param("userId") Long userId);

    @Select("WITH  recursive  userGeographicZonesRec AS \n" +
            "(SELECT *\n" +
            "FROM geographic_zones \n" +
            "WHERE id in  (Select geographiczoneid from vw_user_geographic_zones where userid = #{userId}  " +
            "and case when COALESCE(#{programId},0) > 0 THEN programId = #{programId} ELSE programId = programId END ) \n" +
            "UNION \n" +
            "SELECT sn.* \n" +
            "FROM geographic_zones sn \n" +
            "JOIN userGeographicZonesRec \n" +
            "ON sn.id = userGeographicZonesRec.parentId )\n" +
            "SELECT * from geographic_zones gz\n" +
            "INNER JOIN userGeographicZonesRec gzRec on gz.id = gzRec.id\n" +
            "WHERE gz.parentId = #{parentId} order by gz.name\n")
    List<GeoZoneTree> getUserGeographicZoneChildren(@Param("programId") Long programId, @Param("parentId") int parentId, @Param("userId") Long userId);

    @Select(" WITH filterd_geozones as (\n" +
            "select * from vw_districts where (district_id = #{zone} or region_id =#{zone} or zone_id = #{zone}  or parent = #{zone} or #{zone}=0)\n" +
            ")\n" +
            "  SELECT  " +
            "   	gzz. ID,  " +
            "   	gzz. NAME,  " +
            "       fn_get_parent_geographiczone(gzz.ID,1) georegion, " +
            "       fn_get_parent_geographiczone(gzz.ID,2) geozone, " +
            "   	gjson.geometry,  " +
            "   	COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0) period,  " +
            "   	COALESCE (total. COUNT) total,  " +
            "   	COALESCE (expected. COUNT, 0) AS expected,  " +
            "   	COALESCE (ever. COUNT, 0) AS ever,  " +
            "   	COALESCE (stockedout. COUNT, 0) AS stockedout,  " +
            "   	COALESCE (understocked. COUNT, 0) AS understocked,  " +
            "   	COALESCE (overstocked. COUNT, 0) AS overstocked,  " +
            "   	COALESCE (adequatelystocked. COUNT, 0) AS adequatelystocked,  " +
            "   	COALESCE (stockedoutprev. COUNT, 0) AS stockedoutprev,  " +
            "   	COALESCE (understockedprev. COUNT, 0) AS understockedprev,  " +
            "   	COALESCE (overstockedprev. COUNT, 0) AS overstockedprev,  " +
            "   	COALESCE (adequatelystockedprev. COUNT,	0) AS adequatelystockedprev  " +
            "   FROM  " +
            "   	geographic_zones gzz  " +
            "   LEFT JOIN vw_districts gz on (gz.district_id = gzz.id) " +
            "   LEFT JOIN geographic_zone_geojson gjson ON gzz. ID = gjson.zoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		facilities  " +
            "   	JOIN programs_supported ps ON ps.facilityId = facilities. ID  " +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  " +
            "   	JOIN requisition_group_members rgm ON rgm.facilityId = facilities. ID  " +
            "   	JOIN requisition_group_program_schedules rgps ON rgps.requisitionGroupId = rgm.requisitionGroupId  " +
            "   	AND rgps.programId = ps.programId  " +
            "   	JOIN processing_periods pp ON pp.scheduleId = rgps.scheduleId  " +
            "   	AND pp. ID = #{processingPeriodId}  " +
            "   	WHERE  " +
            "   		gz.levelId = (  " +
            "   			SELECT  " +
            "   				MAX (ID)  " +
            "   			FROM  " +
            "   				geographic_levels  " +
            "   		)  " +
            "   	AND ps.programid = #{programId}  " +
            "   	GROUP BY  " +
            "   		geographicZoneId  " +
            "   ) expected ON gzz. ID = expected.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		facilities  " +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  " +
            "   	WHERE  " +
            "   		gz.levelId = (  " +
            "   			SELECT  " +
            "   				MAX (ID)  " +
            "   			FROM  " +
            "   				geographic_levels  " +
            "   		)  " +
            "   	GROUP BY  " +
            "   		geographicZoneId  " +
            "   ) total ON gzz. ID = total.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		facilities  " +
            "   	JOIN programs_supported ps ON ps.facilityId = facilities. ID  " +
            "   	JOIN geographic_zones gz ON gz. ID = facilities.geographicZoneId  " +
            "       JOIN filterd_geozones z ON z.district_id = facilities.geographicZoneId " +
            "   	WHERE  " +
            "   		ps.programid = #{programId}  " +
            "   	AND facilities. ID IN (  " +
            "   		SELECT  " +
            "   			facilityId  " +
            "   		FROM  " +
            "   			requisitions  " +
            "   		WHERE status <> 'INITIATED' " +
            "   		AND periodId = #{processingPeriodId}  " +
            "   		AND programid = #{programId}  " +
            "   	)  " +
            "   	GROUP BY  " +
            "   		geographicZoneId  " +
            "   ) period ON gzz. ID = period.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "           JOIN facilities f on f.id =  vw_stock_status_2.facility_id\n" +
            "           JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = #{processingPeriodId}  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'SO'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) stockedout ON gzz. ID = stockedout.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'SO'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) ever ON gzz. ID = ever.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = #{processingPeriodId}  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'US'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) understocked ON gzz. ID = understocked.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = #{processingPeriodId}  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'OS'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) overstocked ON gzz. ID = overstocked.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = #{processingPeriodId}  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'SP'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) adequatelystocked ON gzz. ID = adequatelystocked.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = (  " +
            "   			SELECT  " +
            "   				COALESCE (MAX(periodid), 0)  " +
            "   			FROM  " +
            "   				requisitions  " +
            "   			WHERE  " +
            "   				programid = #{programId}  " +
            "   			AND periodId < #{processingPeriodId}  " +
            "   		)  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'SO'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) stockedoutprev ON gzz. ID = stockedoutprev.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = (  " +
            "   			SELECT  " +
            "   				COALESCE (MAX(periodid), 0)  " +
            "   			FROM  " +
            "   				requisitions  " +
            "   			WHERE  " +
            "   				programid = #{programId}  " +
            "   			AND periodId < #{processingPeriodId}  " +
            "   		)  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'US'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) understockedprev ON gzz. ID = understockedprev.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = (  " +
            "   			SELECT  " +
            "   				COALESCE (MAX(periodid), 0)  " +
            "   			FROM  " +
            "   				requisitions  " +
            "   			WHERE  " +
            "   				programid = #{programId}  " +
            "   			AND periodId < #{processingPeriodId}  " +
            "   		)  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	AND status = 'OS'  " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) overstockedprev ON gzz. ID = overstockedprev.geographicZoneId  " +
            "   LEFT JOIN (  " +
            "   	SELECT  " +
            "   		gz_id geographicZoneId,  " +
            "   		COUNT (*)  " +
            "   	FROM  " +
            "   		vw_stock_status_2  " +
            "          JOIN facilities f on f.id =  vw_stock_status_2.facility_id" +
            "          JOIN filterd_geozones z ON z.district_id = f.geographicZoneId" +
            "   	WHERE  " +
            "   		periodId = (  " +
            "   			SELECT  " +
            "   				COALESCE (MAX(periodid), 0)  " +
            "   			FROM  " +
            "   				requisitions  " +
            "   			WHERE  " +
            "   				programid = #{programId}  " +
            "   			AND periodId < #{processingPeriodId}  " +
            "   		)  " +
            "   	AND programid = #{programId}  " +
            "   	AND productId = #{productId}  " +
            "   	AND status = 'SP'  " +
            "       AND req_status <> 'INITIATED' AND reported_figures > 0 " +
            "   	GROUP BY  " +
            "   		gz_id  " +
            "   ) adequatelystockedprev ON gzz. ID = adequatelystockedprev.geographicZoneId  " +
            "   ORDER BY  " +
            "   	gzz. NAME  ")
    List<GeoStockStatusFacilitySummary> getGeoStockStatusFacilitySummary(@Param("programId") Long programId, @Param("processingPeriodId") Long processingPeriodId, @Param("productId") Long productId, @Param("zone") Long zone);



    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true stockedout, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'SO' order by f.name")
    List<GeoStockStatusFacility> getStockedOutFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true understocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'US' order by f.name")
    List<GeoStockStatusFacility> getUnderStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true overstocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'OS' order by f.name")
    List<GeoStockStatusFacility> getOverStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, true adequatelystocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND ss.gz_id = #{geographicZoneId} and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'SP' order by f.name")
    List<GeoStockStatusFacility> getAdequatelyStockedFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);


    @Select("   SELECT p.id, p.code,p.primaryname,  COALESCE(stockedout.count,0) AS stockedout,COALESCE(understocked.count,0) AS understocked,  " +
            "   COALESCE(overstocked.count,0) AS overstocked, COALESCE(adequatelystocked.count,0) AS adequatelystocked,  " +
            "   COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0) reported  " +
            "   FROM public.products AS p  " +
            "    LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and req_status <> 'INITIATED' and reported_figures > 0 and reported_figures > 0  group by productid " +
            "    ) AS reported ON p.id = reported.productid " +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and  req_status <> 'INITIATED' and reported_figures > 0 and status = 'SO' group by productid  " +
            "   ) AS stockedout ON p.id = stockedout.productid  " +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and  req_status <> 'INITIATED' and reported_figures > 0 and status = 'US' group by productid  " +
            "   ) AS understocked ON p.id = understocked.productid  " +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and  req_status <> 'INITIATED' and reported_figures > 0 and status = 'OS' group by productid  " +
            "   ) AS overstocked ON p.id = overstocked.productid  " +
            "   LEFT JOIN ( select productid, count(*) from vw_stock_status_2 where periodId = #{periodId} and programId = #{programId} AND (gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and  req_status <> 'INITIATED' and reported_figures > 0 and status = 'SP' group by productid  " +
            "   ) AS adequatelystocked ON p.id = adequatelystocked.productid  " +
            "   INNER JOIN public.program_products ON p.id = public.program_products.productid  " +
            "   where programid = #{programId} and p.active  = true  " +
            "   and COALESCE (stockedout. COUNT, 0) + COALESCE (understocked. COUNT, 0) + COALESCE (overstocked. COUNT, 0) + COALESCE (adequatelystocked. COUNT, 0)  > 0 ")
    List<GeoStockStatusProductSummary> getStockStatusProductSummary(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);


    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true stockedout, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'SO' order by f.name")
    List<GeoStockStatusProduct> getStockedOutProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true understocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'US' order by f.name")
    List<GeoStockStatusProduct> getUnderStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true overstocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'OS' order by f.name")
    List<GeoStockStatusProduct> getOverStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    @Select("SELECT ss.rnrid, f.id, f.name, f.mainPhone, f.longitude, f.latitude, location geographiczonename, true adequatelystocked, " +
            " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, " +
            " ss.product, ss.amc, ss.stockinhand, ss.mos " +
            " FROM vw_stock_status_2 ss " +
            " INNER JOIN facilities f ON f.id = ss.facility_id " +
            " WHERE ss.programid = #{programId} AND ss.periodid = #{periodId} AND ss.productid = #{productId} AND (ss.gz_id = #{geographicZoneId} OR #{geographicZoneId} = 0) and ss.req_status <> 'INITIATED' and ss.reported_figures > 0 AND ss.status = 'SP' order by f.name")
    List<GeoStockStatusProduct> getAdequatelyStockedProducts(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId, @Param("productId") Long ProductId);

    //@Select("select facility_id, facility_code, facility_name, serial_number, equipment_name, equipment_status, longitude, latitude from vw_lab_equipment_status")
    @SelectProvider(type = LabEquipmentStatusByLocationQueryBuilder.class, method = "getFacilitiesEquipmentsData")
    List<GeoZoneEquipmentStatus> getFacilitiesEquipments(@Param("program") Long program,
                                                         @Param("zone") Long zone,
                                                         @Param("facilityType") Long facilityType,
                                                         @Param("facility") Long facility,
                                                         @Param("equipmentType") Long equipmentType,
                                                         @Param("userId") Long userId,
                                                         @Param("equipment") Long equipment);


    @SelectProvider(type = LabEquipmentStatusByLocationQueryBuilder.class, method = "getFacilityEquipmentStatusGeoData")
    List<GeoZoneEquipmentStatus> getFacilityEquipmentStatusGeo(@Param("program") Long program,
                                                               @Param("zone") Long zone,
                                                               @Param("facilityType") Long facilityType,
                                                               @Param("facility") Long facility,
                                                               @Param("equipmentType") Long equipmentType,
                                                               @Param("userId") Long userId,
                                                               @Param("equipment") Long equipment

    );

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Not Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithNonOperationalEquipments();

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Fully Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithFullyOperationalEquipments();

    @Select("select facility_code, facility_name, facility_type, disrict, facility_id from vw_lab_equipment_status where equipment_status = 'Partially Operational'")
    List<GeoZoneEquipmentStatus> getFacilitiesWithPartiallyOperationalEquipments();


    @SelectProvider(type = LabEquipmentStatusByLocationQueryBuilder.class, method = "getFacilitiesByEquipmentStatus")
    List<GeoZoneEquipmentStatus> getFacilitiesByEquipmentOperationalStatus(@Param("program") Long program,
                                                                           @Param("zone") Long zone,
                                                                           @Param("facilityType") Long facilityType,
                                                                           @Param("facility") Long facility,
                                                                           @Param("equipmentType") Long equipmentType,
                                                                           @Param("userId") Long userId,
                                                                           @Param("status") String status,
                                                                           @Param("equipment") Long equipment);


    @SelectProvider(type = LabEquipmentStatusByLocationQueryBuilder.class, method = "getFacilityEquipmentStatusGeoSummaryData")
    List<GeoZoneEquipmentStatusSummary> getFacilitiesEquipmentStatusSummary(@Param("program") Long program,
                                                                            @Param("zone") Long zone,
                                                                            @Param("facilityType") Long facilityType,
                                                                            @Param("facility") Long facility,
                                                                            @Param("equipmentType") Long equipmentType,
                                                                            @Param("userId") Long userId,
                                                                            @Param("equipment") Long equipment);

    @Select("   select productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc from fn_get_elmis_stock_status_data(#{programId}::int,#{geographicZoneId}::int,#{periodId}::int,#{productId}); ")
    List<GeoStockStatusProductConsumption> getStockStatusProductConsumption(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("geographicZoneId") Long geographicZoneId, @Param("productId") String ProductIds);

    @Select("select * from geographic_zone_geojson")
    List<GeographicZoneJsonDto> getGeoZoneGeometryJson();

/*
        @Select("                 SELECT gzz.id, gzz.name,d.region_name region, gjson.geometry,\n" +
                "                     COALESCE(ever.count) ever,\n" +
                "                    COALESCE(expected.count) expected,\n" +
                "                   COALESCE(total.count) total, COALESCE(period.count,0) as period ,coalesce(k.soh,0) soh,coalesce(k.mos,0) mos,\n" +
                "                   coalesce(prevSOH,0) prevSOH, coalesce(prevMOS,0) prevMOS,\n" +
                "                   sessionCurrent.planned,sessionCurrent.outReachSession,prevSession.prevOutReachSession,prevSession.prevPlanned,\n" +
                "                   round(coverage.coveragePercentage::int,0) coveragePercentage, coveRage.coverageClassification\n" +
                "                      ,coveRage.monthlyestimate monthlyestimate,coverage.cumulativeMonthlyRegular Vaccinated\n" +
                "                     FROM  \n" +
                "                     geographic_zones gzz \n" +
                "                    left join  \n" +
                "                     geographic_zone_geojson gjson on  \n" +
                "                     gzz.id = gjson.zoneId \n" +
                "                     left join \n" +
                "                    (select geographicZoneId, count(*) from facilities  \n" +
                "                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                     join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                     join requisition_group_members rgm on rgm.facilityId = facilities.id \n" +
                "                     join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  \n" +
                "                    join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = #{periodId}::INT\n" +
                "                     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = 82\n" +
                "                     group by geographicZoneId\n" +
                "                     ) expected \n" +
                "                    on gzz.id = expected.geographicZoneId \n" +
                "                     left join \n" +
                "                     (select geographicZoneId, count(*) from facilities  \n" +
                "                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                     where gz.levelId = (select max(id) from geographic_levels)  \n" +
                "                     and facilities.active =true \n" +
                "                     group by geographicZoneId\n" +
                "                     ) total \n" +
                "                    on gzz.id = total.geographicZoneId\n" +
                "                  left join \n" +
                "                     (select geographicZoneId, count(*) from facilities  \n" +
                "                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                    where  ps.programId = 82 and facilities.id in \n" +
                "                      (select facilityId from vaccine_reports where periodId = #{periodId}::INT and programId = 82 and \n" +
                "                      status not in ('DRAFT') )\n" +
                "                     group by geographicZoneId\n" +
                "                     ) period\n" +
                "                    on gzz.id = period.geographicZoneId \n" +
                "                     left join  \n" +
                "                     (select geographicZoneId, count(*) from facilities \n" +
                "                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                     where ps.programId = 82 and facilities.id not in  \n" +
                "                    (select facilityId from vaccine_reports where programId = 82 AND PERIODID = #{periodId}::INT)\n" +
                "                    group by geographicZoneId\n" +
                "                     ) ever \n" +
                "                    on gzz.id = ever.geographicZoneId \n" +
                "                \n" +
                "                     left join\n" +
                "               \n" +
                "                     (\n" +
                "            \n" +
                "                SELECT X.geographiczoneid,\n" +
                "                 x.coveragePercentage,x.district_name,\n" +
                "                 CASE\n" +
                "                  WHEN x.coveragePercentage IS NULL\n" +
                "                   THEN NULL\n" +
                "                  WHEN x.coveragepercentage > x.targetcoverageGood\n" +
                "                    THEN 'good'\n" +
                "                  WHEN x.coveragepercentage > x.targetcoveragewarn\n" +
                "                    THEN 'normal'\n" +
                "                  WHEN x.coveragepercentage > x.targetcoveragebad\n" +
                "                    THEN 'warn'\n" +
                "                 ELSE 'bad' END                   AS coverageClassification,\n" +
                "                 monthlyestimate * num monthlyestimate,cumulativeMonthlyRegular\n" +
                "                FROM (\n" +
                "                SELECT\n" +
                "                   (select extract(month from startdate) from processing_periods where id = #{periodId}::INT) num,\n" +
                "                         CASE WHEN (a.monthlyestimate IS NOT NULL AND a.monthlyestimate != 0)\n" +
                "                           THEN (cumulativeMonthlyRegular * 100) / (monthlyestimate * (select extract(month from startdate) from processing_periods where id = #{periodId}::INT))\n" +
                "                 ELSE NULL END       AS coveragePercentage,\n" +
                "                         \n" +
                "                  a.*,\n" +
                "              \n" +
                "                  d.district_name,\n" +
                "                  d.district_id,\n" +
                "                  d.region_name,\n" +
                "                   pt.*  \n" +
                "                  FROM (\n" +
                "                       SELECT         \n" +
                "                         f.geographiczoneid,\n" +
                "                         d.productid,\n" +
                "                         d.year,\n" +
                "                        MAX( e.monthlyestimate) monthlyestimate,\n" +
                "                      sum(monthlyregular) monthlyregular,\n" +
                "                         sum(monthlyregular) AS cumulativeMonthlyRegular\n" +
                "                       FROM\n" +
                "                         vw_vaccine_cumulative_coverage_by_dose d\n" +
                "                        JOIN facilities f ON f.id = d.facilityid\n" +
                "                       JOIN vw_monthly_district_estimate e ON e.districtid = f.geographiczoneid AND d.year = e.year AND\n" +
                "                                                                d.denominatorestimatecategoryid = e.demographicestimateid\n" +
                "                where d.year = 2017::INT and d.month <= \n" +
                "               (select extract(month from startdate) from processing_periods where id = #{periodId}::INT) and  productId = #{productId}            \n" +
                "                       GROUP BY d.year, f.geographiczoneid, d.productid\n" +
                "                \n" +
                "                    ) a\n" +
                "                  JOIN vw_districts d ON d.district_id = a.geographiczoneid\n" +
                "                  JOIN products p ON p.id = a.productid\n" +
                "                 JOIN vaccine_product_targets pt ON pt.productid = a.productid\n" +
                "                ) x\n" +
                "                order by region_name,district_name\n" +
                "                     )coverage \n" +
                "                     on gzz.id = coverage.geographicZoneId  \n" +
                "          \n" +
                "          LEFT JOIN \n" +
                "\n" +
                "          (WITH q AS (\n" +
                "                      \n" +
                "                        SELECT facilityId , Max(SOH) SOH from(\n" +
                "                        \n" +
                "                         SELECT  facilityID, sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh\n" +
                "                         \n" +
                "                        FROM STOCK_CARDS s\n" +
                "                        JOIN stock_card_entries e ON e.stockCardId = s.id  \n" +
                "                   \n" +
                "                        where s.productId = #{productId} and e.modifiedDate::DATE <= now()::date\n" +
                "                        )x\n" +
                "                        group by facilityId\n" +
                "\n" +
                "                        )\n" +
                "                       SELECT z.id geographiczoneid,soh,\n" +
                "                       CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as mos,S.*\n" +
                "                       FROM q\n" +
                "                       JOIN facilities f ON q.facilityid = f.id\n" +
                "                       JOIN geographic_zones z ON f.geographiczoneiD = Z.ID\n" +
                "                       JOIN geographic_levels gz on z.levelid = gz.id\n" +
                "                       JOIN STOCK_REQUIREMENTS s ON f.id = s.facilityId AND YEAR = 2017 AND PRODUCTID = #{productId}\n" +
                "                       WHERE levelNumber =4 and f.typeid in (select id from facility_Types where lower(code) ='dvs')\n" +
                "                       ORDER BY Z.id\n" +
                "                    )K\n" +
                "                   on gzz.id = k.geographicZoneId  \n" +
                "                    LEFT JOIN \n" +
                "\n" +
                "                     (WITH q AS (\n" +
                "                      \n" +
                "                        SELECT facilityId , Max(SOH) soh from(\n" +
                "                        \n" +
                "                         SELECT  facilityID, sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh\n" +
                "                         \n" +
                "                        FROM STOCK_CARDS s\n" +
                "                        JOIN stock_card_entries e ON e.stockCardId = s.id  \n" +
                "                   \n" +
                "                        where s.productId = #{productId} and e.modifiedDate::DATE<= date_trunc('month', current_date - interval '1' month)::DATE\n" +
                "                        )x\n" +
                "                        group by facilityId\n" +
                "\n" +
                "                        )\n" +
                "                       SELECT z.id geographiczoneid,soh prevSOH,\n" +
                "                       CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as prevmos,S.*\n" +
                "                       FROM q\n" +
                "                       JOIN facilities f ON q.facilityid = f.id\n" +
                "                       JOIN geographic_zones z ON f.geographiczoneiD = Z.ID\n" +
                "                       JOIN geographic_levels gz on z.levelid = gz.id\n" +
                "                       JOIN STOCK_REQUIREMENTS s ON f.id = s.facilityId AND YEAR = 2017 AND PRODUCTID = #{productId}\n" +
                "                       WHERE levelNumber =4 and f.typeid in (select id from facility_Types where lower(code) ='dvs')\n" +
                "                       ORDER BY Z.id\n" +
                "                    )M on gzz.id = M.geographicZoneId  \n" +
                "                  LEFT JOIN \n" +
                "                   (       WITH Q as ( \n" +
                "                SELECT d.district_id,\n" +
                "                   SUM(vaccine_reports.outreachimmunizationsessions) AS outreachsession,\n" +
                "                    SUM(vaccine_reports.plannedoutreachimmunizationsessions) as planned\n" +
                "                   FROM vaccine_report_coverage_line_items\n" +
                "                     JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id\n" +
                "                     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id\n" +
                "                     JOIN Facilities f ON facilityid = f.id\n" +
                "                     JOIN VW_DISTRICTS d ON  f.geographiczoneid = d.district_id \n" +
                "                  WHERE  productid = #{productId} \n" +
                "                       and periodid = #{periodId}::INT\n" +
                "                 group by district_id)\n" +
                "                 select q.* from q\n" +
                "                  ) sessionCurrent on gzz.id = sessionCurrent.district_id\n" +
                "\n" +
                "\n" +
                "                LEFT JOIN \n" +
                "                   (       WITH Q as ( \n" +
                "                SELECT d.district_id,\n" +
                "                   SUM(vaccine_reports.outreachimmunizationsessions) AS prevoutreachsession,\n" +
                "                    SUM(vaccine_reports.plannedoutreachimmunizationsessions) as prevplanned\n" +
                "                   FROM vaccine_report_coverage_line_items\n" +
                "                     JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id\n" +
                "                     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id\n" +
                "                     JOIN Facilities f ON facilityid = f.id\n" +
                "                     JOIN VW_DISTRICTS d ON  f.geographiczoneid = d.district_id \n" +
                "                  WHERE  productid = #{productId} \n" +
                "                       and periodid in (select id from processing_periods where id < #{periodId}::INT order by id desc  limit 1) \n" +
                "                 group by district_id)\n" +
                "                 select q.* from q\n" +
                "                  ) prevSession on gzz.id = prevSession.district_id\n" +
                "\n" +
                "                   LEFT JOIN vw_districts d ON d.district_id = gzz.id\n" +
                "                   \n" +
                "                   order by gzz.name\n ")*/


        @Select("\n" +
                "                SELECT gzz.id, gzz.name,d.region_name region, gjson.geometry,\n" +
                "                                     COALESCE(ever.count) ever,\n" +
                "                                    COALESCE(expected.count) expected,\n" +
                "                                     COALESCE(prevExpected.count) prevExpected,\n" +
                "                                   COALESCE(total.count) total,\n" +
                "                                    COALESCE(period.count,0) as period ,\n" +
                "                                  COALESCE(prevPeriod.count,0) as prevPeriod \n" +
                "                                   ,coalesce(k.soh,0) soh,coalesce(k.mos,0) mos,\n" +
                "                                   coalesce(prevSOH,0) prevSOH, coalesce(prevMOS,0) prevMOS,\n" +
                "                                   COALESCE(sessionCurrent.planned,0) planned,\n" +
                "                                   COALESCE(sessionCurrent.outreachsession,0) outreachsession,\n" +
                "                                   COALESCE(prevSession.prevoutreachsession,0) prevoutreachsession,\n" +
                "                                   COALESCE(prevSession.prevplanned,0) prevplanned,\n" +
                "                                      round(coverage.coveragePercentage::int,0) coveragePercentage,\n" +
                "                                       coveRage.coverageClassification\n" +
                "                                      ,coveRage.monthlyestimate monthlyestimate,coverage.cumulativeMonthlyRegular Vaccinated\n" +
                "                                     FROM  \n" +
                "                                    geographic_zones gzz \n" +
                "                                    left join  \n" +
                "                                     geographic_zone_geojson gjson on  \n" +
                "                                     gzz.id = gjson.zoneId \n" +
                "                                     left join \n" +
                "                                    (select geographicZoneId, count(*) from facilities  \n" +
                "                                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                                     join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                     join requisition_group_members rgm on rgm.facilityId = facilities.id \n" +
                "                                  join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  \n" +
                "                                    join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = #{periodId}::INT\n" +
                "                                     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = (select * from  fn_get_vaccine_program_id())\n" +
                "                                     group by geographicZoneId\n" +
                "                                    ) expected \n" +
                "                                    on gzz.id = expected.geographicZoneId \n" +
                "                \n" +
                "                                      left join \n" +
                "                                    (select geographicZoneId, count(*) from facilities  \n" +
                "                                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                                     join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                     join requisition_group_members rgm on rgm.facilityId = facilities.id \n" +
                "                                    join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  \n" +
                "                                   join processing_periods pp on pp.scheduleId = rgps.scheduleId and\n" +
                "               \n" +
                "                                   pp.id in (select id from processing_periods where id < #{periodId} order by id desc  limit 1) \n" +
                "                                     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = (select * from  fn_get_vaccine_program_id())\n" +
                "                                     group by geographicZoneId\n" +
                "                                     ) prevExpected \n" +
                "                                    on gzz.id = prevExpected.geographicZoneId\n" +
                "                                 left join \n" +
                "                                     (select geographicZoneId, count(*) from facilities  \n" +
                "                                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                     where gz.levelId = (select max(id) from geographic_levels)  \n" +
                "                                    and facilities.active =true \n" +
                "                                     group by geographicZoneId\n" +
                "                                     ) total \n" +
                "                                    on gzz.id = total.geographicZoneId\n" +
                "                                  left join \n" +
                "                                     (select geographicZoneId, count(*) from facilities  \n" +
                "                                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                    where  ps.programId = 82 and facilities.id in\n" +
                "                                      (select facilityId from vaccine_reports where periodId = #{periodId}::INT and programId = \n" +
                "                                      (select * from  fn_get_vaccine_program_id()) and \n" +
                "                                      status not in ('DRAFT') )\n" +
                "                                     group by geographicZoneId\n" +
                "                                     ) period\n" +
                "                                    on gzz.id = period.geographicZoneId \n" +
                "                                     left join \n" +
                "                                   (select geographicZoneId, count(*) from facilities  \n" +
                "                                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                    where  ps.programId = (select * from  fn_get_vaccine_program_id()) and facilities.id in \n" +
                "                                      (select facilityId from vaccine_reports where programId = (select * from  fn_get_vaccine_program_id())  \n" +
                "                                      and periodid in (select id from processing_periods where id < #{periodId} order by id desc  limit 1) \n" +
                "                                      and status not in ('DRAFT') )\n" +
                "                                     group by geographicZoneId\n" +
                "                \n" +
                "                \n" +
                "                                    ) prevPeriod\n" +
                "                                    on gzz.id = prevPeriod.geographicZoneId\n" +
                "                                     left join  \n" +
                "                                     (select geographicZoneId, count(*) from facilities \n" +
                "                                     join programs_supported ps on ps.facilityId = facilities.id \n" +
                "                                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
                "                                     where ps.programId = (select * from  fn_get_vaccine_program_id()) and facilities.id not in  \n" +
                "                                    (select facilityId from vaccine_reports where programId = (select * from  fn_get_vaccine_program_id()) AND periodId = #{periodId}::INT)\n" +
                "                                    group by geographicZoneId\n" +
                "                                     ) ever \n" +
                "                                    on gzz.id = ever.geographicZoneId \n" +
                "                \n" +
                "                                     left join\n" +
                "                \n" +
                "                                     (\n" +
                "                \n" +
                "                                SELECT X.geographiczoneid,\n" +
                "                                 x.coveragePercentage,x.district_name,\n" +
                "                                 CASE\n" +
                "                                  WHEN x.coveragePercentage IS NULL\n" +
                "                                   THEN NULL\n" +
                "                                  WHEN x.coveragepercentage > x.targetcoverageGood\n" +
                "                                    THEN 'good'\n" +
                "                                  WHEN x.coveragepercentage > x.targetcoveragewarn\n" +
                "                                    THEN 'normal'\n" +
                "                                  WHEN x.coveragepercentage > x.targetcoveragebad\n" +
                "                                    THEN 'warn'\n" +
                "                                 ELSE 'bad' END                   AS coverageClassification,\n" +
                "                                 monthlyestimate * num monthlyestimate,cumulativeMonthlyRegular\n" +
                "                               FROM (\n" +
                "                                SELECT\n" +
                "                                   (select extract(month from startdate) from processing_periods where id = #{periodId}::INT) num,\n" +
                "                                         CASE WHEN (a.monthlyestimate IS NOT NULL AND a.monthlyestimate != 0)\n" +
                "                                           THEN (cumulativeMonthlyRegular * 100) / (monthlyestimate * (select extract(month from startdate) from processing_periods where id = #{periodId}::INT))\n" +
                "                                 ELSE NULL END       AS coveragePercentage,\n" +
                "                                  a.*,\n" +
                "                \n" +
                "                               d.district_name,\n" +
                "                                  d.district_id,\n" +
                "                                  d.region_name,\n" +
                "                                   pt.*  \n" +
                "                                  FROM (\n" +
                "                                       SELECT         \n" +
                "                                         f.geographiczoneid,\n" +
                "                                         d.productid,\n" +
                "                                         d.year,\n" +
                "                                        MAX( e.monthlyestimate) monthlyestimate,\n" +
                "                                   sum(monthlyregular) monthlyregular,\n" +
                "                                         sum(monthlyregular) AS cumulativeMonthlyRegular\n" +
                "                                       FROM\n" +
                "                                         vw_vaccine_cumulative_coverage_by_dose d\n" +
                "                                        JOIN facilities f ON f.id = d.facilityid\n" +
                "                                       JOIN vw_monthly_district_estimate e ON e.districtid = f.geographiczoneid AND d.year = e.year AND\n" +
                "                                                                                d.denominatorestimatecategoryid = e.demographicestimateid\n" +
                "                                where d.year = #{year}::INT and d.month <= \n" +
                "                               (select extract(month from startdate) from processing_periods where id = #{periodId}::INT) " +
                "                                  And doseId=#{doseId}::int and  productId = #{productId}         \n" +
                "                                       GROUP BY d.year, f.geographiczoneid, d.productid\n" +
                "                                    ) a\n" +
                "                                 JOIN vw_districts d ON d.district_id = a.geographiczoneid\n" +
                "                                  JOIN products p ON p.id = a.productid\n" +
                "                                 JOIN vaccine_product_targets pt ON pt.productid = a.productid\n" +
                "                                ) x\n" +
                "                            order by region_name,district_name\n" +
                "                                     )coverage \n" +
                "                                     on gzz.id = coverage.geographicZoneId  \n" +
                "                        LEFT JOIN \n" +
                "                          (WITH q AS (\n" +
                "                                        SELECT facilityId , Max(SOH) SOH from(\n" +
                "                                         SELECT  facilityID, sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh\n" +
                "                                       FROM STOCK_CARDS s\n" +
                "                                        JOIN stock_card_entries e ON e.stockCardId = s.id  \n" +
                "                                        where s.productId = #{productId} and e.modifiedDate::DATE <= now()::date\n" +
                "                                        )x\n" +
                "                                        group by facilityId\n" +
                "                                      )\n" +
                "                                       SELECT z.id geographiczoneid,soh,\n" +
                "                                       CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as mos,S.*\n" +
                "                                       FROM q\n" +
                "                                       JOIN facilities f ON q.facilityid = f.id\n" +
                "                                       JOIN geographic_zones z ON f.geographiczoneiD = Z.ID\n" +
                "                                       JOIN geographic_levels gz on z.levelid = gz.id\n" +
                "                                       JOIN STOCK_REQUIREMENTS s ON f.id = s.facilityId AND YEAR = #{year} AND PRODUCTID = #{productId}\n" +
                "                                       WHERE levelNumber =4 and f.typeid in (select id from facility_Types where lower(code) ='dvs')\n" +
                "                                       ORDER BY Z.id\n" +
                "                                    )K\n" +
                "                                   on gzz.id = k.geographicZoneId \n" +
                "                                    LEFT JOIN \n" +
                "                                 (WITH q AS (\n" +
                "                                       SELECT facilityId , Max(SOH) soh from(\n" +
                "                                      SELECT  facilityID, sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh\n" +
                "                                        FROM STOCK_CARDS s\n" +
                "                                        JOIN stock_card_entries e ON e.stockCardId = s.id  \n" +
                "                                        where s.productId = #{productId} and e.modifiedDate::DATE<= date_trunc('month', current_date - interval '1' month)::DATE\n" +
                "                                       )x\n" +
                "                                        group by facilityId\n" +
                "                                      )\n" +
                "                                       SELECT z.id geographiczoneid,soh prevSOH,\n" +
                "                                       CASE WHEN isaValue > 0 THEN  ROUND((soh::numeric(10,2) / isaValue::numeric(10,2)),2) else 0 end as prevmos,S.*\n" +
                "                                       FROM q\n" +
                "                                       JOIN facilities f ON q.facilityid = f.id\n" +
                "                                       JOIN geographic_zones z ON f.geographiczoneiD = Z.ID\n" +
                "                                       JOIN geographic_levels gz on z.levelid = gz.id\n" +
                "                                       JOIN STOCK_REQUIREMENTS s ON f.id = s.facilityId AND YEAR = #{year} AND PRODUCTID = #{productId}\n" +
                "                                       WHERE levelNumber =4 and f.typeid in (select id from facility_Types where lower(code) ='dvs')\n" +
                "                                       ORDER BY Z.id\n" +
                "                                    )M\n" +
                "                                   on gzz.id = M.geographicZoneId  \n" +
                "                                  LEFT JOIN \n" +
                "                                   (       WITH Q as ( \n" +
                "                                SELECT d.district_id,\n" +
                "                                   SUM(vaccine_reports.outreachimmunizationsessions) AS outreachsession,\n" +
                "                                    SUM(vaccine_reports.plannedoutreachimmunizationsessions) as planned\n" +
                "                                   FROM vaccine_report_coverage_line_items\n" +
                "                                     JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id\n" +
                "                                   JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id\n" +
                "                                    JOIN Facilities f ON facilityid = f.id\n" +
                "                                     JOIN VW_DISTRICTS d ON  f.geographiczoneid = d.district_id \n" +
                "                                  WHERE  productid = #{productId}::INT AND doseId = #{doseId}::INT\n" +
                "                                       and periodid = #{periodId}::INT\n" +
                "                                 group by district_id)\n" +
                "                                select q.* from q\n" +
                "                                 ) sessionCurrent on gzz.id = sessionCurrent.district_id\n" +
                "                \n" +
                "                               LEFT JOIN \n" +
                "                                   (       WITH Q as ( \n" +
                "                               SELECT d.district_id,\n" +
                "                                   SUM(vaccine_reports.outreachimmunizationsessions) AS prevoutreachsession,\n" +
                "                                    SUM(vaccine_reports.plannedoutreachimmunizationsessions) as prevplanned\n" +
                "                                   FROM vaccine_report_coverage_line_items\n" +
                "                                     JOIN vaccine_reports ON vaccine_report_coverage_line_items.reportid = vaccine_reports.id\n" +
                "                                     JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id\n" +
                "                                     JOIN Facilities f ON facilityid = f.id\n" +
                "                                     JOIN VW_DISTRICTS d ON  f.geographiczoneid = d.district_id \n" +
                "                                  WHERE  productid = #{productId} and doseId = #{doseId}::int\n" +
                "                                       and periodid in (select id from processing_periods where id < #{periodId} order by id desc  limit 1) \n" +
                "                                 group by district_id)\n" +
                "                                 select q.* from q\n" +
                "                                  ) prevSession on gzz.id = prevSession.district_id\n" +
                "                                   LEFT JOIN vw_districts d ON d.district_id = gzz.id\n" +
                "                                   order by gzz.name\n")

        List<GeoZoneVaccineCoverage>getGeoZoneVaccineCoverage(@Param("userId") Long userId, @Param("productId") Long product,@Param("year") Long year, @Param("periodId") Long processingPeriodId,@Param("doseId")Long doseId);




}
