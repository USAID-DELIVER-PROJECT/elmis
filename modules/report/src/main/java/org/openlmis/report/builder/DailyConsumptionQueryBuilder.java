
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
package org.openlmis.report.builder;

import org.openlmis.report.model.params.AggregateConsumptionReportParam;
import org.openlmis.report.model.params.FacilityConsumptionReportParam;
import org.openlmis.report.model.params.StockImbalanceReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.reportTypeFilteredBy;

public class DailyConsumptionQueryBuilder {

    public static String getDailyReportQuery(Map params) {

        FacilityConsumptionReportParam filter = (FacilityConsumptionReportParam) params.get("filterCriteria");
        String query = "with daily as \n" +
                "(\n" +
                "   select\n" +
                "      f.id as facilityId,\n" +
                "      f.code as facilitycode,\n" +
                "      ds.id dailyStockId,\n" +
                "      p.code productcode,\n" +
                "      p.id productid,\n" +
                "      f.name as facility,\n" +
                "      gz.district_name district," +
                "  gz.district_id as districtid," +
                "  gz.region_id as provinceid,\n" +
                "      gz.region_name province,\n" +
                "      ds.programid as program,\n" +
                "      ds.source as source,\n" +
                "      p.primaryname as product,\n" +
                "      dsl.stockonhand stockOnHand,\n" +
                "      ds.date,\n" +
                "      (\n" +
                "         select\n" +
                "            min(date) \n" +
                "         from\n" +
                "            daily_stock_status \n" +
                "         where\n" +
                "            facilityid = ds.facilityid \n" +
                "      )\n" +
                "      as fistSubmissionDate,\n" +
                "      (\n" +
                "         select\n" +
                "            max(date) \n" +
                "         from\n" +
                "            daily_stock_status \n" +
                "         where\n" +
                "            facilityid = ds.facilityid \n" +
                "      )\n" +
                "      as lastSubmissionDate \n" +
                "   FROM\n" +
                "      daily_stock_status ds \n" +
                "      inner join\n" +
                "         daily_stock_status_line_items dsl \n" +
                "         on ds.id = dsl.stockstatussubmissionid \n" +
                "      inner join\n" +
                "         facilities f \n" +
                "         on f.id = ds.facilityId \n" +
                "      JOIN\n" +
                "         facility_types ft \n" +
                "         on ft.id = f.typeid \n" +
                "      inner JOIN\n" +
                "         vw_districts gz \n" +
                "         on gz.district_id = f.geographiczoneid \n" +
                "      inner join\n" +
                "         products p \n" +
                "         on p.id = dsl.productid \n" +
                "      inner join\n" +
                "         programs pg \n" +
                "         on pg.id = ds.programid \n" +
                "      inner join\n" +
                "         program_products pgp \n" +
                "         on ds.programid = pgp.programid \n" +
                "         and p.id = pgp.productid \n" +
                "      inner JOIN\n" +
                "         facility_approved_products fap \n" +
                "         on ft.id = fap.facilitytypeid \n" +
                "         and fap.programproductid = pgp.id \n" +
                "   where\n" +
                "      date = '"+filter.getDate()+"' :: date \n" +
                getDailyPredicate(filter)+
                ") ," +
//               this is recent daily
                "    \n" +
                "recent_date as(\n" +
                "select ds.facilityid,\n" +
                "                                  productid ,                                     \n" +
                "                                      max(ds.date )  as date                  \n" +
                "                                   FROM  \n" +
                "                                      daily_stock_status ds   \n" +
                "                                      inner join  \n" +
                "                                         daily_stock_status_line_items dsl   \n" +
                "                                         on ds.id = dsl.stockstatussubmissionid   \n" +
                "                                      \n" +
                "                                      \n" +
                "                                   \n" +
                "                                       \n" +
                "                                      --getDailyPredicate(filter) \n" +
                "\n" +
                "                                      group by 1,2\n" +

                "                ) ,\n" +
                "daily_recent as                 \n" +
                "                (select\n" +
                "                f.id as facilityId, \n" +
                "                      f.code as facilitycode, \n" +
                "                      ds.id dailyStockId, \n" +
                "                      p.code productcode, \n" +
                "                      p.id productid, \n" +
                "                      f.name as facility,                       \n" +
                "                      p.primaryname as product, \n" +
                "                      dsl.stockonhand stockOnHand, \n" +
                "                      ds.programid programid,\n" +
                "                      rd.date   ,\n" +
                "                      ds.source                   \n" +
                "                   FROM \n" +
                "                      daily_stock_status ds  \n" +
                "                      inner join \n" +
                "                         daily_stock_status_line_items dsl  \n" +
                "                         on ds.id = dsl.stockstatussubmissionid  \n" +
                "                      inner join \n" +
                "                         facilities f  \n" +
                "                         on f.id = ds.facilityId  \n" +
                "                      JOIN \n" +
                "                         facility_types ft  \n" +
                "                         on ft.id = f.typeid  \n" +
                "                      inner JOIN \n" +
                "                         vw_districts gz  \n" +
                "                         on gz.district_id = f.geographiczoneid  \n" +
                "                      inner join \n" +
                "                         products p  \n" +
                "                         on p.id = dsl.productid  \n" +
                "                      inner join \n" +
                "                         programs pg  \n" +
                "                         on pg.id = ds.programid  \n" +
                "                      inner join recent_date rd  on rd.productid=p.id and rd.facilityid=f.id and rd.date=ds.date\n" +
                "                         \n" +
                "                       where pg.active=true " +
                                    getDailyPredicate(filter) +
                "                                \n" +
                "                ) , "+
                "period as\n" +
                "(\n" +
                "   select\n" +
                "      facilityid,\n" +
                "      id,\n" +
                "      name,\n" +
                "      startdate \n" +
                "   from\n" +
                "      (\n" +
                "         select\n" +
                "            d.facilityid,\n" +
                "            pp.id,\n" +
                "            pp.name,\n" +
                "            pp.startdate,\n" +
                "            min( '"+filter.getDate()+"' :: date - pp.startdate ) dif \n" +
                "         from\n" +
                "            processing_periods pp \n" +
                "            inner join\n" +
                "               requisitions ri \n" +
                "               on pp.id = ri.periodid \n" +
                "            join\n" +
                "               daily d \n" +
                "               on d.facilityid = ri.facilityid \n" +
                "         where\n" +
                "            '"+filter.getDate()+"':: date >= pp.startdate \n" +
                "         group by\n" +
                "            pp.id,\n" +
                "            d.facilityid,\n" +
                "            pp.name \n" +
                "         order by\n" +
                "            dif limit 1 \n" +
                "      )\n" +
                "      as c \n" +
                ")\n" +
                ",\n" +
                "monthStock as\n" +
                "(\n" +
                "   SELECT\n" +
                "      r.facilityid as facilityid,\n" +
                "      li.productcode,\n" +
                "      f.name as facility,\n" +
                "      li.product as product,\n" +
                "      ft.name facilitytypename,\n" +
                "      p.id as productid,\n" +
                "      pp.id as periodid,\n" +
                "      pp.name as processing_period_name,\n" +
                "      li.stockinhand,\n" +
                "      li.stockoutdays stockoutdays,\n" +
                "      fap.minmonthsofstock,\n" +
                "      fap.maxmonthsofstock,\n" +
                "      to_char(pp.startdate, 'Mon') asmonth,\n" +
                "      extract( year \n" +
                "   from\n" +
                "      pp.startdate ) as year,\n" +
                "      CASE\n" +
                "         -- stocked out when stockinhand\n" +
                "         WHEN\n" +
                "            li.stockinhand = 0 \n" +
                "         THEN\n" +
                "            'SO' :: text \n" +
                "         ELSE\n" +
                "            CASE\n" +
                "               WHEN\n" +
                "                  li.amc > 0 \n" +
                "                  AND li.stockinhand > 0 \n" +
                "               THEN\n" +
                "                  CASE\n" +
                "                     WHEN\n" +
                "                        round( (li.stockinhand :: decimal / li.amc):: numeric, 2 ) < fap.minmonthsofstock \n" +
                "                     THEN\n" +
                "                        'US' :: text \n" +
                "                     WHEN\n" +
                "                        round( (li.stockinhand :: decimal / li.amc):: numeric, 2 ) >= fap.minmonthsofstock :: numeric \n" +
                "                        AND round( (li.stockinhand :: decimal / li.amc):: numeric, 2 ) <= fap.maxmonthsofstock :: numeric \n" +
                "                     THEN\n" +
                "                        'SP' :: text \n" +
                "                     WHEN\n" +
                "                        round( (li.stockinhand :: decimal / li.amc):: numeric, 2 ) > fap.maxmonthsofstock \n" +
                "                     THEN\n" +
                "                        'OS' :: text \n" +
                "                  END\n" +
                "               ELSE\n" +
                "                  'UK' :: text \n" +
                "            END\n" +
                "      END\n" +
                "      AS status, \n" +
                "      CASE\n" +
                "         WHEN\n" +
                "            COALESCE(li.amc, 0) = 0 \n" +
                "         THEN\n" +
                "            0 :: numeric \n" +
                "         ELSE\n" +
                "            trunc( round( (li.stockinhand :: decimal / li.amc):: numeric, 2 ), 1 ):: numeric \n" +
                "      END\n" +
                "      AS mos, li.amc, COALESCE( \n" +
                "      CASE\n" +
                "         WHEN\n" +
                "            (\n" +
                "               COALESCE(li.amc, 0) * ft.nominalmaxmonth - li.stockinhand \n" +
                "            )\n" +
                "            < 0 \n" +
                "         THEN\n" +
                "            0 \n" +
                "         ELSE\n" +
                "            COALESCE(li.amc, 0) * ft.nominalmaxmonth - li.stockinhand \n" +
                "      END\n" +
                ", 0 ) AS required, li.quantityapproved AS ordered \n" +
                "   FROM\n" +
                "      period pp \n" +
                "      JOIN\n" +
                "         requisitions r \n" +
                "         ON pp.ID = r.periodid \n" +
                "      JOIN\n" +
                "         requisition_line_items li \n" +
                "         ON li.rnrid = r.ID \n" +
                "      JOIN\n" +
                "         facilities f \n" +
                "         on f.id = r.facilityId \n" +
                "      right outer JOIN\n" +
                "         facility_types ft \n" +
                "         on ft.id = f.typeid \n" +
                "      inner JOIN\n" +
                "         vw_districts gz \n" +
                "         on gz.district_id = f.geographiczoneid \n" +
                "      right outer JOIN\n" +
                "         products p \n" +
                "         on p.code = li.productcode \n" +
                "      join\n" +
                "         program_products pgp \n" +
                "         on r.programid = pgp.programid \n" +
                "         and p.id = pgp.productid \n" +
                "      JOIN\n" +
                "         facility_approved_products fap \n" +
                "         on ft.id = fap.facilitytypeid \n" +
                "         and fap.programproductid = pgp.id \n" +
                "      join\n" +
                "         daily d \n" +
                "         on d.facilityid = f.id \n" +
                "         and d.productcode = li.productcode \n" +
                " WHERE  li.skipped = false " +
                "  AND (li.beginningbalance > 0 or li.quantityreceived > 0 or li.quantitydispensed > 0 or abs(li.totallossesandadjustments) > 0 or li.amc > 0) " +
                getMainPredicate(filter) +
                ")\n" +
                "select\n" +
                "   d.facilitycode,\n" +
                "   d.facilityid facilityid,\n" +
                "   d.program,\n" +
                "   d.date,\n" +
                "   d.source,\n" +
                "   ms.status,\n" +
                "   CASE\n" +
                "      -- stocked out when stockinhand\n" +
                "      WHEN\n" +
                "         d.stockOnHand = 0 \n" +
                "      THEN\n" +
                "         'SO' :: text \n" +
                "      ELSE\n" +
                "         CASE\n" +
                "            WHEN\n" +
                "               ms.amc > 0 \n" +
                "               AND d.stockOnHand > 0 \n" +
                "            THEN\n" +
                "               CASE\n" +
                "                  WHEN\n" +
                "                     round( (d.stockOnHand:: decimal / ms.amc):: numeric, 2 ) < ms.minmonthsofstock \n" +
                "                  THEN\n" +
                "                     'US' :: text \n" +
                "                  WHEN\n" +
                "                     round( (d.stockOnHand:: decimal / ms.amc):: numeric, 2 ) >= ms.minmonthsofstock :: numeric \n" +
                "                     AND round( (d.stockOnHand:: decimal / ms.amc):: numeric, 2 ) <= ms.maxmonthsofstock :: numeric \n" +
                "                  THEN\n" +
                "                     'SP' :: text \n" +
                "                  WHEN\n" +
                "                     round( (d.stockOnHand:: decimal / ms.amc):: numeric, 2 ) > ms.maxmonthsofstock \n" +
                "                  THEN\n" +
                "                     'OS' :: text \n" +
                "               END\n" +
                "            ELSE\n" +
                "               'UK' :: text \n" +
                "         END\n" +
                "   END\n" +
                "   AS dailystatus, d.district, d. province,d.provinceid,d.districtid, d. facility, d. productid, d.product, d.productcode, d.stockonhand , " +
                " ds.date  recentdate, ds.stockonhand  recentstockonhand , " +
                " p.name  periodname, p.id  periodid, \n" +
                "   ms.amc, ms.mos, ms.stockinhand, d.fistSubmissiondate, d.lastsubmissiondate \n" +
                "from\n" +
                "   daily d \n" +
                "   inner join daily_recent ds on ds.facilityId=d.facilityid and ds.productid=d.productid" +
                "   left outer join  period p       on d.facilityid = p.facilityid \n" +
                "   left outer join    monthstock ms  on ms.productid = d.productid \n" +
                "      and ms.facilityid = d.facilityid \n" +
                "      and p.id = ms.periodid";

//        writePredicates(filter);
//        ORDER_BY("f.name, p.primaryName");
        return query;

    }

    private static String getMainPredicate(FacilityConsumptionReportParam filter) {
        String predicate = "";
        predicate += " AND " + programIsFilteredBy("r.programId");
        if (filter.getFacility() != 0) {
            predicate += " AND " + facilityIsFilteredBy("f.id");
        }
        if (filter.getZone() != 0) {
            predicate += " AND " + geoZoneIsFilteredBy("gz");
        }
        String queryFilter = predicate +
                " AND li.stockinhand IS NOT NULL AND li.skipped = false";
        return queryFilter;

    }

    private static String getDailyPredicate(FacilityConsumptionReportParam filter) {
        String predicate = "";
        predicate += " AND " + programIsFilteredBy("ds.programId");
        if (filter.getFacility() != 0) {
            predicate += " AND " + facilityIsFilteredBy("f.id");
        }
        if (filter.getZone() != 0) {
            predicate += " AND " + geoZoneIsFilteredBy("gz");
        }
        if(filter.getFacility()!=null&&filter.getFacility()!=0) {
            predicate +=" AND " +facilityIsFilteredBy("f.id");
        }
        if(filter.getProduct()!=null&&!filter.getProduct().equals(0L)&&!filter.getProduct().equals(-1L)) {
            predicate +=" AND " +productFilteredBy("p.id");
        }

        return predicate;

    }

}
