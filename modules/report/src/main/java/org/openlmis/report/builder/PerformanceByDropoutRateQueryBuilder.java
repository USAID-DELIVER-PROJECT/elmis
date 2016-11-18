/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.report.builder;


import org.openlmis.report.model.params.PerformanceByDropoutRateParam;

import java.util.Map;

public class PerformanceByDropoutRateQueryBuilder {

    public static final String FACILITY_CRITERIA = "filterCriteria";


    public String getByFacilityQuery(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get(FACILITY_CRITERIA);
        String query =
                "-- dropout temp table \n" +
                " with dropout as ( SELECT d.region_name, \n" +
                "                d.district_name, \n" +
                "                i.facility_name, \n" +
                "                i.facility_id, \n" +
                "                i.period_id ,                             \n" +
                "                i.period_start_date period_start_date,\n" +
                "                to_date(to_char(i.period_start_date, 'Mon YYYY'), 'Mon YYYY')    period_name, \n" +
                "                sum(i.bcg_1) bcg_vaccinated, \n" +
                "                sum(i.dtp_1) dtp1_vaccinated, \n" +
                "                sum(i.mr_1) mr_vaccinated,  \n" +
                "                sum(i.dtp_3) dtp3_vaccinated,  \n" +
                "                case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) - sum(i.mr_1)) / sum(i.bcg_1)::numeric) * 100 else 0 end bcg_mr_dropout,  \n" +
                "                case when sum(i.dtp_1) > 0 then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1)::numeric) * 100 else 0 end dtp1_dtp3_dropout  \n" +
                "                FROM vw_vaccine_dropout i  \n" +
                "                JOIN vw_districts d ON i.geographic_zone_id = d.district_id  \n" +
                "                JOIN vaccine_reports vr ON i.report_id = vr.ID  \n" +
                "                JOIN program_products pp  ON pp.programid = vr.programid   AND pp.productid = i.product_id  \n" +
                "                JOIN  product_categories pg  ON pp.productcategoryid = pg.ID \n" +
                writePredicates(filter) +
                "                group by 1,2,3,4,5,i.period_start_date \n" +
                "                )" +
                ",\n" +
                " -- period temp table \n" +
                "                period AS ( \n" +
                "                select pp.id period_id, pp.startdate period_start_date, pp.name period_name \n" +
                "                from processing_periods pp \n" +
                "                where  \n" +
                "                pp.startdate::date >= ' " + filter.getPeriod_start_date() + "'::date   \n" +
                "                and pp.enddate::date <= ' " + filter.getPeriod_end_date() + "'::date    \n" +
                "                and pp.numberofmonths = 1  \n" +
                "                order by  startdate \n" +
                "                        )  " +
                "               , " +
                "--cumulative temp \n" +
                "cumulative as ( select  i.facility_id,   \n" +
                "                p.period_id ,    " +
                "p.period_start_date period_start_date,                         \n" +
                "                 sum(i.bcg_vaccinated) cum_bcg_vaccinated, \n" +
                "                   sum(i.dtp1_vaccinated) cum_dtp1_vaccinated, \n" +
                "                   sum(i.mr_vaccinated) cum_mr_vaccinated,  \n" +
                "                  sum(i.dtp3_vaccinated) cum_dtp3_vaccinated,  \n" +
                "                 case when sum(i.bcg_vaccinated) > 0 then((sum(i.bcg_vaccinated) - sum(i.mr_vaccinated)) / sum(i.bcg_vaccinated)::numeric) * 100 else 0 end cum_bcg_mr_dropout,  \n" +
                "                   case when sum(i.dtp1_vaccinated) > 0 then((sum(i.dtp1_vaccinated) - sum(i.dtp3_vaccinated)) / sum(i.dtp1_vaccinated)::numeric) * 100 else 0 end cum_dtp1_dtp3_dropout \n" +
                "                 from dropout i, period p where i.period_start_date<=p.period_start_date\n" +
                "                 group by 1,2,3\n" +
                "                 )              \n" +
                "                select d.region_name, d.district_name,  d.facility_name, " +
                "     d.facility_id, d.period_id,d.period_start_date, d.period_name,d.bcg_vaccinated,d.dtp1_vaccinated,d.mr_vaccinated,d.mr_vaccinated,d.dtp3_vaccinated," +
                "     d.bcg_mr_dropout,d.dtp1_dtp3_dropout," +
                "     c.cum_bcg_vaccinated, c.cum_mr_vaccinated,cum_dtp1_vaccinated, c.cum_dtp3_vaccinated,c.cum_bcg_mr_dropout, c.cum_dtp1_dtp3_dropout  " +
                "     from dropout    d   ,cumulative c    " +
                "     where d.facility_id= c.facility_id  and d.period_start_date=c.period_start_date\n" +
                "                \n" +
                "                 order by 1,2,4,d.period_start_date";
        return query;
    }

    private static String writePredicates(PerformanceByDropoutRateParam param) {
        String predicate = "";
        predicate = " WHERE i.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )  " +
                "  and i.period_start_date >= to_date('" + param.getPeriod_start_date() + "'::text, 'YYYY-MM-DD') " +
                "and  i.period_end_date< (to_date('" + param.getPeriod_end_date() + "'::text,'YYYY-MM-DD')) +  interval '1' day" +
                " and i.product_id= " + param.getProduct_id() +
                "and (d.parent = " + param.getGeographic_zone_id() + "::INT or  d.region_id = " + param.getGeographic_zone_id() + "::INT " +
                "or  d.district_id = " + param.getGeographic_zone_id() + "::INT " +
                "  or  0 = " + param.getGeographic_zone_id() + "::INT)";

        if (param.getFacility_id() != null && param.getFacility_id() != 0L) {
            predicate = predicate + "and i.facility_id=" + param.getFacility_id();
        }

        return predicate;
    }

    public String getByDistrictQuery(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get(FACILITY_CRITERIA);
        String query = "  with  \n" +
                "                -- dropout temp table \n" +
                "                dropout as ( SELECT d.region_name,  \n" +
                "                                d.district_name,  \n" +
                "                                   d.district_id,  \n" +
                "                                i.period_id ,                              \n" +
                "                                   i.period_start_date period_start_date, \n" +
                "                                to_date(to_char(i.period_start_date, 'Mon YYYY'), 'Mon YYYY')    period_name,  \n" +
                "                                 sum(i.bcg_1) bcg_vaccinated,  \n" +
                "                                   sum(i.dtp_1) dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_1) mr_vaccinated,   \n" +
                "                                  sum(i.dtp_3) dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) - sum(i.mr_1)) / sum(i.bcg_1)::numeric) * 100 else 0 end bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp_1) > 0 then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1)::numeric) * 100 else 0 end dtp1_dtp3_dropout   \n" +
                "                                    FROM vw_vaccine_dropout i   \n" +
                "                                 JOIN vw_districts d ON i.geographic_zone_id = d.district_id   \n" +
                "                                  JOIN vaccine_reports vr ON i.report_id = vr.ID   \n" +
                "                                 JOIN program_products pp  ON pp.programid = vr.programid   AND pp.productid = i.product_id   \n" +
                "                                     JOIN  product_categories pg  ON pp.productcategoryid = pg.ID  \n" +
                writePredicatesForDistrict(filter) +
                "                                group by 1,2,3,4,i.period_start_date  \n" +
                "                                ) \n" +
                "                , \n" +
                "                 -- period temp table  \n" +
                "                                         period AS (  \n" +
                "                                            select pp.id period_id, pp.startdate period_start_date, pp.name period_name  \n" +
                "                                               from processing_periods pp  \n" +
                "                                                  where   \n" +
                "                                                pp.startdate::date >= '" + filter.getPeriod_start_date() + "'::date    \n" +
                "                                                and pp.enddate::date <= '" + filter.getPeriod_end_date() + "'::date     \n" +
                "                                                and pp.numberofmonths = 1   \n" +
                "                                                order by  startdate  \n" +
                "                                        )   \n" +
                "                               ,  \n" +
                "                --cumulative temp \n" +
                "                cumulative as ( select  i.district_id,    \n" +
                "                                p.period_id ,     \n" +
                "                p.period_start_date period_start_date,                          \n" +
                "                                 sum(i.bcg_vaccinated) cum_bcg_vaccinated,  \n" +
                "                                   sum(i.dtp1_vaccinated) cum_dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_vaccinated) cum_mr_vaccinated,   \n" +
                "                                  sum(i.dtp3_vaccinated) cum_dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_vaccinated) > 0 then((sum(i.bcg_vaccinated) - sum(i.mr_vaccinated)) / sum(i.bcg_vaccinated)::numeric) * 100 else 0 end cum_bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp1_vaccinated) > 0 then((sum(i.dtp1_vaccinated) - sum(i.dtp3_vaccinated)) / sum(i.dtp1_vaccinated)::numeric) * 100 else 0 end cum_dtp1_dtp3_dropout  \n" +
                "                                 from dropout i, period p where i.period_start_date<=p.period_start_date \n" +
                "                                 group by 1,2,3 \n" +
                "                                 )  \n" +
                "\n" +
                "                                 -----select statement main query             \n" +
                "                                select d.region_name, d.district_name ,\n" +
                "                     d.district_id, d.period_id,d.period_start_date, d.period_name,d.bcg_vaccinated,d.dtp1_vaccinated,d.mr_vaccinated,d.mr_vaccinated,d.dtp3_vaccinated, \n" +
                "                     d.bcg_mr_dropout,d.dtp1_dtp3_dropout, \n" +
                "                     c.cum_bcg_vaccinated, c.cum_mr_vaccinated,cum_dtp1_vaccinated, c.cum_dtp3_vaccinated,c.cum_bcg_mr_dropout, c.cum_dtp1_dtp3_dropout   \n" +
                "                     from dropout    d   ,cumulative c     \n" +
                "                     where d.district_id= c.district_id  and d.period_start_date=c.period_start_date \n" +
                "                                 \n" +
                "                                 order by 1,2,4,d.period_start_date";


        return query;
    }

    public String getDistrict(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get(FACILITY_CRITERIA);
        String query = "  with  \n" +
                "                -- dropout temp table \n" +
                "                dropout as ( SELECT d.region_name,  \n" +
                "                                d.district_name,  \n" +
                "                                   d.district_id,  \n" +
                "                                i.period_id ,                              \n" +
                "                                   i.period_start_date period_start_date, \n" +
                "                                to_date(to_char(i.period_start_date, 'Mon YYYY'), 'Mon YYYY')    period_name,  \n" +
                "                                 sum(i.bcg_1) bcg_vaccinated,  \n" +
                "                                   sum(i.dtp_1) dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_1) mr_vaccinated,   \n" +
                "                                  sum(i.dtp_3) dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) - sum(i.mr_1)) / sum(i.bcg_1)::numeric) * 100 else 0 end bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp_1) > 0 then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1)::numeric) * 100 else 0 end dtp1_dtp3_dropout   \n" +
                "                                    FROM vw_vaccine_dropout i   \n" +
                "                                 JOIN vw_districts d ON i.geographic_zone_id = d.district_id   \n" +
                "                                  JOIN vaccine_reports vr ON i.report_id = vr.ID   \n" +
                "                                 JOIN program_products pp  ON pp.programid = vr.programid   AND pp.productid = i.product_id   \n" +
                "                                     JOIN  product_categories pg  ON pp.productcategoryid = pg.ID  \n" +
                writePredicatesForDistrict(filter) +
                "                                group by 1,2,3,4,i.period_start_date  \n" +
                "                                ) \n" +
                "                , \n" +
                "                 -- period temp table  \n" +
                "                                         period AS (  \n" +
                "                                            select pp.id period_id, pp.startdate period_start_date, pp.name period_name  \n" +
                "                                               from processing_periods pp  \n" +
                "                                                  where   \n" +
                "                                                pp.startdate::date >= '" + filter.getPeriod_start_date() + "'::date    \n" +
                "                                                and pp.enddate::date <= '" + filter.getPeriod_end_date() + "'::date     \n" +
                "                                                and pp.numberofmonths = 1   \n" +
                "                                                order by  startdate  \n" +
                "                                        )   \n" +
                "                               ,  \n" +
                "                --cumulative temp \n" +
                "                cumulative as ( select  i.district_id,    \n" +
                "                                p.period_id ,     \n" +
                "                p.period_start_date period_start_date,                          \n" +
                "                                 sum(i.bcg_vaccinated) cum_bcg_vaccinated,  \n" +
                "                                   sum(i.dtp1_vaccinated) cum_dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_vaccinated) cum_mr_vaccinated,   \n" +
                "                                  sum(i.dtp3_vaccinated) cum_dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_vaccinated) > 0 then((sum(i.bcg_vaccinated) - sum(i.mr_vaccinated)) / sum(i.bcg_vaccinated)::numeric) * 100 else 0 end cum_bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp1_vaccinated) > 0 then((sum(i.dtp1_vaccinated) - sum(i.dtp3_vaccinated)) / sum(i.dtp1_vaccinated)::numeric) * 100 else 0 end cum_dtp1_dtp3_dropout  \n" +
                "                                 from dropout i, period p where i.period_start_date<=p.period_start_date \n" +
                "                                 group by 1,2,3 \n" +
                "                                 )  \n" +
                "\n" +
                "                                 -----select statement main query             \n" +
                "                                select d.region_name, d.district_name ,\n" +
                "                     d.district_id, d.period_id,d.period_start_date, d.period_name,d.bcg_vaccinated,d.dtp1_vaccinated,d.mr_vaccinated,d.mr_vaccinated,d.dtp3_vaccinated, \n" +
                "                     d.bcg_mr_dropout,d.dtp1_dtp3_dropout, \n" +
                "                     c.cum_bcg_vaccinated, c.cum_mr_vaccinated,cum_dtp1_vaccinated, c.cum_dtp3_vaccinated,c.cum_bcg_mr_dropout, c.cum_dtp1_dtp3_dropout   \n" +
                "                     from dropout    d   ,cumulative c     \n" +
                "                     where d.district_id= c.district_id  and d.period_start_date=c.period_start_date \n" +
                "                                 \n" +
                "                                 order by 1,2,4,d.period_start_date";


        return query;


    }

    private static String writePredicatesForDistrict(PerformanceByDropoutRateParam param) {

        String predicate = "";
        predicate = " WHERE i.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )  " +
                "  and i.period_start_date >= to_date('" + param.getPeriod_start_date() + "'::text, 'YYYY-MM-DD') " +
                "and  i.period_end_date< (to_date('" + param.getPeriod_end_date() + "'::text,'YYYY-MM-DD')) +  interval '1' day" +
                " and i.product_id= " + param.getProduct_id() +
                "and (d.parent = " + param.getGeographic_zone_id() + "::INT or  d.region_id = " + param.getGeographic_zone_id() + "::INT " +
                "or  d.district_id = " + param.getGeographic_zone_id() + "::INT " +
                "  or  0 = " + param.getGeographic_zone_id() + "::INT)";


        return predicate;


    }

    public String getByRegionQuery(Map params) {
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get(FACILITY_CRITERIA);
        String query = "   with  \n" +
                "                -- dropout temp table \n" +
                "                dropout as ( SELECT d.region_name,  \n" +
                "                               \n" +

                "                                   d.region_id,  \n" +
                "                                i.period_id ,                              \n" +
                "                                   i.period_start_date period_start_date, \n" +
                "                                to_date(to_char(i.period_start_date, 'Mon YYYY'), 'Mon YYYY')    period_name,  \n" +
                "                                 sum(i.bcg_1) bcg_vaccinated,  \n" +
                "                                   sum(i.dtp_1) dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_1) mr_vaccinated,   \n" +
                "                                  sum(i.dtp_3) dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) - sum(i.mr_1)) / sum(i.bcg_1)::numeric) * 100 else 0 end bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp_1) > 0 then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1)::numeric) * 100 else 0 end dtp1_dtp3_dropout   \n" +
                "                                    FROM vw_vaccine_dropout i   \n" +
                "                                 JOIN vw_districts d ON i.geographic_zone_id = d.district_id   \n" +
                "                                  JOIN vaccine_reports vr ON i.report_id = vr.ID   \n" +
                "                                 JOIN program_products pp  ON pp.programid = vr.programid   AND pp.productid = i.product_id   \n" +
                "                                     JOIN  product_categories pg  ON pp.productcategoryid = pg.ID  \n" +
                writePredicatesForRegion(filter) +
                "                                group by 1,2,3,i.period_start_date  \n" +
                "                                ) \n" +
                "                , \n" +
                "                 -- period temp table  \n" +
                "                                         period AS (  \n" +
                "                                            select pp.id period_id, pp.startdate period_start_date, pp.name period_name  \n" +
                "                                               from processing_periods pp  \n" +
                "                                                  where   \n" +
                "                                                pp.startdate::date >= '" + filter.getPeriod_start_date() + "'::date    \n" +
                "                                                and pp.enddate::date <= '" + filter.getPeriod_end_date() + "'::date     \n" +
                "                                                and pp.numberofmonths = 1   \n" +
                "                                                order by  startdate  \n" +
                "                                        )   \n" +
                "                               ,  \n" +
                "                --cumulative temp \n" +
                "                cumulative as ( select  i.region_id,    \n" +
                "                                p.period_id ,     \n" +
                "                p.period_start_date period_start_date,                          \n" +
                "                                 sum(i.bcg_vaccinated) cum_bcg_vaccinated,  \n" +
                "                                   sum(i.dtp1_vaccinated) cum_dtp1_vaccinated,  \n" +
                "                                   sum(i.mr_vaccinated) cum_mr_vaccinated,   \n" +
                "                                  sum(i.dtp3_vaccinated) cum_dtp3_vaccinated,   \n" +
                "                                 case when sum(i.bcg_vaccinated) > 0 then((sum(i.bcg_vaccinated) - sum(i.mr_vaccinated)) / sum(i.bcg_vaccinated)::numeric) * 100 else 0 end cum_bcg_mr_dropout,   \n" +
                "                                   case when sum(i.dtp1_vaccinated) > 0 then((sum(i.dtp1_vaccinated) - sum(i.dtp3_vaccinated)) / sum(i.dtp1_vaccinated)::numeric) * 100 else 0 end cum_dtp1_dtp3_dropout  \n" +
                "                                 from dropout i, period p where i.period_start_date<=p.period_start_date \n" +
                "                                 group by 1,2,3 \n" +
                "                                 )  \n" +
                "\n" +
                "                                 -----select statement main query             \n" +
                "                                select d.region_name, d.region_id ,\n" +
                "                      d.period_id,d.period_start_date, d.period_name,d.bcg_vaccinated,d.dtp1_vaccinated,d.mr_vaccinated,d.mr_vaccinated,d.dtp3_vaccinated, \n" +
                "                     d.bcg_mr_dropout,d.dtp1_dtp3_dropout, \n" +
                "                     c.cum_bcg_vaccinated, c.cum_mr_vaccinated,cum_dtp1_vaccinated, c.cum_dtp3_vaccinated,c.cum_bcg_mr_dropout, c.cum_dtp1_dtp3_dropout   \n" +
                "                     from dropout    d   ,cumulative c     \n" +
                "                     where d.region_id= c.region_id  and d.period_start_date=c.period_start_date \n" +
                "                                 \n" +
                "                                 order by 1,2,4,d.period_start_date;";

        return query;
    }

    private static String writePredicatesForRegion(PerformanceByDropoutRateParam param) {

        String predicate = "";
        predicate = " WHERE i.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )  " +
                "  and i.period_start_date >= to_date('" + param.getPeriod_start_date() + "'::text, 'YYYY-MM-DD') " +
                "and  i.period_end_date< (to_date('" + param.getPeriod_end_date() + "'::text,'YYYY-MM-DD')) +  interval '1' day" +
                " and i.product_id= " + param.getProduct_id();


        return predicate;


    }
}
