package org.openlmis.vaccine.repository.mapper.inventory.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Created by chrispinus on 10/29/15.
 */
public class VaccineInventoryDashboardQueryBuilder {

    public static final String getNonFunctionalAlerts(Map params) {

        String facilities = (String) params.get("facilities");

        BEGIN();
        SELECT("repair.facilityid, " +
                "repair.programid, " +
                "repair.facilityname, " +
                "repair.modifieddate," +
                "users.firstname || ' '|| users.lastname modifiedby, " +
                "repair.model, " +
                "operationalstatus as status ");
        FROM("vw_cce_repair_management_not_functional repair join users on users.id =  repair.modifiedby");
        WHERE("repair.facilityid IN " + facilities);
        ORDER_BY("repair.facilityname");

        String sql = SQL();
        return sql;

    }
}
