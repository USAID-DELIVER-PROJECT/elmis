
/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.openlmis.report.builder;


import org.openlmis.report.model.params.ColdChainTemperatureReportParam;

import java.util.Calendar;
import java.util.Map;

public class VaccineTemperatureReportQueryBuilder {

    public static final int CC_MIN_TEMP_AGGREGAT_TERESHOLD = 2;
    public static final int CC_MAX_TEMP_AGGREGAT_TERESHOLD = 8;

    public static String getColdChainTemperatureData(Map params) {

        ColdChainTemperatureReportParam filter  = (ColdChainTemperatureReportParam)params.get("filterCriteria");

        String sql =
                        "  select  vd.zone_id, vd.region_id, vd.district_id, vd.parent, vd.zone_name,\n" +
                        "       vd.region_name,\n" +
                        "       vd.district_name,\n" +
                        "       f.target::numeric,\n" +
                                "    b.minjan,\n" +
                                "    b.minfeb,\n" +
                                "    b.minmar,\n" +
                                "    b.minapr,\n" +
                                "    b.minmay,\n" +
                                "    b.minjun,\n" +
                                "    b.minjul,\n" +
                                "    b.minaug,\n" +
                                "    b.minsep,\n" +
                                "    b.minoct,\n" +
                                "    b.minnov,\n" +
                                "    b.mindec,\n" +
                                "    c.maxjan,\n" +
                                "    c.maxfeb,\n" +
                                "    c.maxmar,\n" +
                                "    c.maxapr,\n" +
                                "    c.maxmay,\n" +
                                "    c.maxjun,\n" +
                                "    c.maxjul,\n" +
                                "    c.maxaug,\n" +
                                "    c.maxsep,\n" +
                                "    c.maxoct,\n" +
                                "    c.maxnov,\n" +
                                "    c.maxdec,\n" +
                                "    d.minep_jan,\n" +
                                "    d.minep_feb,\n" +
                                "    d.minep_mar,\n" +
                                "    d.minep_apr,\n" +
                                "    d.minep_may,\n" +
                                "    d.minep_jun,\n" +
                                "    d.minep_jul,\n" +
                                "    d.minep_aug,\n" +
                                "    d.minep_sep,\n" +
                                "    d.minep_oct,\n" +
                                "    d.minep_nov,\n" +
                                "    d.minep_dec,\n" +
                                "    e.maxep_jan,\n" +
                                "    e.maxep_feb,\n" +
                                "    e.maxep_mar,\n" +
                                "    e.maxep_apr,\n" +
                                "    e.maxep_may,\n" +
                                "    e.maxep_jun,\n" +
                                "    e.maxep_jul,\n" +
                                "    e.maxep_aug,\n" +
                                "    e.maxep_sep,\n" +
                                "    e.maxep_oct,\n" +
                                "    e.maxep_nov,\n" +
                                "    e.maxep_dec \n"  +
                        " FROM\n" +
                        "  (SELECT *\n" +
                        "   FROM crosstab( $$\n" +
                        "                 SELECT gz.id, extract(MONTH\n" +
                        "                                       FROM pp.startdate) report_month, min(0) tmp\n" +
                        "                 FROM geographic_zones gz\n" +
                        "                 CROSS JOIN processing_periods pp\n" +
                        "                 WHERE extract(YEAR\n" +
                        "                               FROM pp.startdate) = "+ getYearPredicate(filter.getYear()) +"\n" +
                        "                   AND gz.levelid =\n" +
                        "                     (SELECT max(id)\n" +
                        "                      FROM geographic_levels)\n" +
                        "                 GROUP BY 1,2\n" +
                        "                 ORDER BY 1, 2 $$, $$\n" +
                        "                 SELECT m\n" +
                        "                 FROM generate_series(1,12) m $$) AS ( gzid int, jan varchar, feb varchar, mar varchar, apr varchar, may varchar, jun varchar, jul varchar, aug varchar, sep varchar, oct varchar, nov varchar, dec varchar)) a\n" +
                        "LEFT JOIN\n" +
                        "  (SELECT *\n" +
                        "   FROM crosstab( $$\n" +
                        "                 SELECT id, report_month, min(COALESCE(mintemp,0))  mintemp\n" +
                        "                 FROM vw_cc_temperature_line_item\n" +
                        "                 WHERE report_year = "+ getYearPredicate(filter.getYear()) +"\n" +
                        "                 GROUP BY 1, 2\n" +
                        "                 ORDER BY 1, 2 $$, $$\n" +
                        "                 SELECT m\n" +
                        "                 FROM generate_series(1,12) m $$) AS (gzid integer, minjan numeric, minfeb numeric, minmar numeric, minapr numeric, minmay numeric, minjun numeric, minjul numeric, minaug numeric, minsep numeric, minoct numeric, minnov numeric, mindec numeric)) b ON b.gzid = a.gzid\n" +
                        "LEFT JOIN\n" +
                        "  (SELECT *\n" +
                        "   FROM crosstab( $$\n" +
                        "                 SELECT id, report_month, max(COALESCE(maxtemp,0)) maxtemp\n" +
                        "                 FROM vw_cc_temperature_line_item\n" +
                        "                 WHERE report_year = "+ getYearPredicate(filter.getYear()) +"\n" +
                        "                 GROUP BY 1, 2\n" +
                        "                 ORDER BY 1, 2 $$, $$\n" +
                        "                 SELECT m\n" +
                        "                 FROM generate_series(1,12) m $$) AS (gzid integer, maxjan numeric, maxfeb numeric, maxmar numeric, maxapr numeric, maxmay numeric, maxjun numeric, maxjul numeric, maxaug numeric, maxsep numeric, maxoct numeric, maxnov numeric, maxdec numeric)) c ON c.gzid = a.gzid\n" +
                        "LEFT JOIN\n" +
                        "  (SELECT *\n" +
                        "   FROM crosstab( $$\n" +
                        "                 SELECT id, report_month, min(COALESCE(minepisodetemp,0)) minepisodtemp\n" +
                        "                 FROM vw_cc_temperature_line_item\n" +
                        "                 WHERE report_year = "+ getYearPredicate(filter.getYear()) +"\n" +
                        "                 GROUP BY 1, 2\n" +
                        "                 ORDER BY 1, 2 $$, $$\n" +
                        "                 SELECT m\n" +
                        "                 FROM generate_series(1,12) m $$) AS ( gzid integer, minep_jan numeric, minep_feb numeric, minep_mar numeric, minep_apr numeric, minep_may numeric, minep_jun numeric, minep_jul numeric, minep_aug numeric, minep_sep numeric, minep_oct numeric, minep_nov numeric, minep_dec numeric)) d ON d.gzid = a.gzid\n" +
                        "LEFT JOIN\n" +
                        "  (SELECT *\n" +
                        "   FROM crosstab( $$\n" +
                        "                 SELECT id, report_month, max(COALESCE(maxepisodetemp,0)) maxepisodetemp\n" +
                        "                 FROM vw_cc_temperature_line_item\n" +
                        "                 WHERE report_year = "+ getYearPredicate(filter.getYear()) +"\n" +
                        "                 GROUP BY 1, 2\n" +
                        "                 ORDER BY 1, 2 $$, $$\n" +
                        "                 SELECT m\n" +
                        "                 FROM generate_series(1,12) m $$) AS (gzid integer, maxep_jan numeric, maxep_feb numeric, maxep_mar numeric, maxep_apr numeric, maxep_may numeric, maxep_jun numeric, maxep_jul numeric, maxep_aug numeric, maxep_sep numeric, maxep_oct numeric, maxep_nov numeric, maxep_dec numeric)) e ON e.gzid = a.gzid\n" +
                        "JOIN vw_districts vd ON vd.district_id = a.gzid\n" +
                        "LEFT JOIN\n" +
                        "  (SELECT geographic_zone_id gzid,\n" +
                        "          sum(target_value_monthly) target\n" +
                        "   FROM vw_vaccine_target_population tp\n" +
                        "   WHERE category_id = 1\n" +
                        "     AND YEAR = "+ getYearPredicate(filter.getYear()) +
                        "   GROUP BY 1) f ON a.gzid = f.gzid\n" +
                                getZonePredicate(filter.getZoneId())
                      + " ORDER BY 1,\n" +
                        "         2,\n" +
                        "         3";
        return sql;
    }

    public static String getColdChainTemperatureReportAggregateTotalData(Map params){

        ColdChainTemperatureReportParam filter = (ColdChainTemperatureReportParam) params.get("filterCriteria");

        String sql  = "  select  \n" +
                "                                    sum(b.minjan) minjan, \n" +
                "                                    sum(b.minfeb) minfeb, \n" +
                "                                    sum(b.minmar) minmar, \n" +
                "                                    sum(b.minapr) minapr, \n" +
                "                                    sum(b.minmay) minmay, \n" +
                "                                    sum(b.minjun) minjun, \n" +
                "                                    sum(b.minjul) minjul, \n" +
                "                                    sum(b.minaug) minaug, \n" +
                "                                    sum(b.minsep) minsep, \n" +
                "                                    sum(b.minoct) minoct, \n" +
                "                                    sum(b.minnov) minnov, \n" +
                "                                    sum(b.mindec) mindec, \n" +
                "                                    sum(c.maxjan) maxjan, \n" +
                "                                    sum(c.maxfeb) maxfeb, \n" +
                "                                    sum(c.maxmar) maxmar, \n" +
                "                                    sum(c.maxapr) maxapr, \n" +
                "                                    sum(c.maxmay) maxmay, \n" +
                "                                    sum(c.maxjun) maxjun, \n" +
                "                                    sum(c.maxjul) maxjul, \n" +
                "                                    sum(c.maxaug) maxaug, \n" +
                "                                    sum(c.maxsep) maxsep, \n" +
                "                                    sum(c.maxoct) maxoct, \n" +
                "                                    sum(c.maxnov) maxnov, \n" +
                "                                    sum(c.maxdec) maxdec, \n" +
                "                                    sum(d.minep_jan) minep_jan, \n" +
                "                                    sum(d.minep_feb) minep_feb, \n" +
                "                                    sum(d.minep_mar) minep_mar, \n" +
                "                                    sum(d.minep_apr) minep_apr, \n" +
                "                                    sum(d.minep_may) minep_may, \n" +
                "                                    sum(d.minep_jun) minep_jun, \n" +
                "                                    sum(d.minep_jul) minep_jul, \n" +
                "                                    sum(d.minep_aug) minep_aug, \n" +
                "                                    sum(d.minep_sep) minep_sep, \n" +
                "                                    sum(d.minep_oct) minep_oct, \n" +
                "                                    sum(d.minep_nov) minep_nov, \n" +
                "                                    sum(d.minep_dec) minep_dec, \n" +
                "                                    sum(e.maxep_jan) maxep_jan, \n" +
                "                                    sum(e.maxep_feb) maxep_feb, \n" +
                "                                    sum(e.maxep_mar) maxep_mar, \n" +
                "                                    sum(e.maxep_apr) maxep_apr, \n" +
                "                                    sum(e.maxep_may) maxep_may, \n" +
                "                                    sum(e.maxep_jun) maxep_jun, \n" +
                "                                    sum(e.maxep_jul) maxep_jul, \n" +
                "                                    sum(e.maxep_aug) maxep_aug, \n" +
                "                                    sum(e.maxep_sep) maxep_sep, \n" +
                "                                    sum(e.maxep_oct) maxep_oct, \n" +
                "                                    sum(e.maxep_nov) maxep_nov, \n" +
                "                                    sum(e.maxep_dec) maxep_dec   \n" +
                "                         FROM \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT gz.id, extract(MONTH \n" +
                "                                                               FROM pp.startdate) report_month, min(0) tmp \n" +
                "                                         FROM geographic_zones gz \n" +
                "                                         CROSS JOIN processing_periods pp \n" +
                "                                         WHERE extract(YEAR \n" +
                "                                                       FROM pp.startdate) =  "+ getYearPredicate(filter.getYear()) +"  \n" +
                "                                           AND gz.levelid = \n" +
                "                                             (SELECT max(id) \n" +
                "                                              FROM geographic_levels) \n" +
                "                                         GROUP BY 1,2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS ( gzid int, jan varchar, feb varchar, mar varchar, apr varchar, may varchar, jun varchar, jul varchar, aug varchar, sep varchar, oct varchar, nov varchar, dec varchar)) a \n" +
                "                        LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, min(COALESCE(mintemp,0)) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"  \n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS (gzid integer, minjan numeric, minfeb numeric, minmar numeric, minapr numeric, minmay numeric, minjun numeric, minjul numeric, minaug numeric, minsep numeric, minoct numeric, minnov numeric, mindec numeric)) b ON b.gzid = a.gzid \n" +
                "                        LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, max(COALESCE(maxtemp,0)) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"  \n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS (gzid integer, maxjan numeric, maxfeb numeric, maxmar numeric, maxapr numeric, maxmay numeric, maxjun numeric, maxjul numeric, maxaug numeric, maxsep numeric, maxoct numeric, maxnov numeric, maxdec numeric)) c ON c.gzid = a.gzid \n" +
                "                        LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, min(COALESCE(minepisodetemp,0)) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"  \n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS ( gzid integer, minep_jan numeric, minep_feb numeric, minep_mar numeric, minep_apr numeric, minep_may numeric, minep_jun numeric, minep_jul numeric, minep_aug numeric, minep_sep numeric, minep_oct numeric, minep_nov numeric, minep_dec numeric)) d ON d.gzid = a.gzid \n" +
                "                        LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, max(COALESCE(maxepisodetemp,0)) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"  \n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS (gzid integer, maxep_jan numeric, maxep_feb numeric, maxep_mar numeric, maxep_apr numeric, maxep_may numeric, maxep_jun numeric, maxep_jul numeric, maxep_aug numeric, maxep_sep numeric, maxep_oct numeric, maxep_nov numeric, maxep_dec numeric)) e ON e.gzid = a.gzid \n" +
                "                        JOIN vw_districts vd ON vd.district_id = a.gzid \n" +
                "                        LEFT JOIN \n" +
                "                          (SELECT geographic_zone_id gzid, \n" +
                "                                  sum(target_value_monthly) target \n" +
                "                           FROM vw_vaccine_target_population tp \n" +
                "                           WHERE category_id = 1 \n" +
                "                             AND YEAR =  "+ getYearPredicate(filter.getYear()) +" \n" +
                "                           GROUP BY 1) f ON a.gzid = f.gzid \n" +
                getZonePredicate(filter.getZoneId())
               + "                        ORDER BY 1, \n" +
                "                                 2, \n" +
                "                                 3;";
        return sql;
    }
    public static String getColdChainTemperatureMinMaxAggregateData(Map params) {

        ColdChainTemperatureReportParam filter = (ColdChainTemperatureReportParam) params.get("filterCriteria");

        String sql = "  SELECT \n" +
                "    COALESCE(SUM(b.minjan),0) minjan,\n" +
                "    COALESCE(SUM(b.minfeb),0) minfeb,\n" +
                "    COALESCE(SUM(b.minmar),0) minmar,\n" +
                "    COALESCE(SUM(b.minapr),0) minapr,\n" +
                "    COALESCE(SUM(b.minmay),0) minmay,\n" +
                "    COALESCE(SUM(b.minjun),0) minjun,\n" +
                "    COALESCE(SUM(b.minjul),0) minjul,\n" +
                "    COALESCE(SUM(b.minaug),0) minaug,\n" +
                "    COALESCE(SUM(b.minsep),0) minsep,\n" +
                "    COALESCE(SUM(b.minoct),0) minoct,\n" +
                "    COALESCE(SUM(b.minnov),0) minnov,\n" +
                "    COALESCE(SUM(b.mindec),0) mindec,\n" +
                "    COALESCE(SUM(c.maxjan),0) maxjan,\n" +
                "    COALESCE(SUM(c.maxfeb),0) maxfeb,\n" +
                "    COALESCE(SUM(c.maxmar),0) maxmar,\n" +
                "    COALESCE(SUM(c.maxapr),0) maxapr,\n" +
                "    COALESCE(SUM(c.maxmay),0) maxmay,\n" +
                "    COALESCE(SUM(c.maxjun),0) maxjun,\n" +
                "    COALESCE(SUM(c.maxjul),0) maxjul,\n" +
                "    COALESCE(SUM(c.maxaug),0) maxaug,\n" +
                "    COALESCE(SUM(c.maxsep),0) maxsep,\n" +
                "    COALESCE(SUM(c.maxoct),0) maxoct,\n" +
                "    COALESCE(SUM(c.maxnov),0) maxnov,\n" +
                "    COALESCE(SUM(c.maxdec),0) maxdec\n" +
                "    FROM\n" +
                "  (SELECT *\n" +
                "   FROM crosstab( $$\n" +
                "                 SELECT gz.id, extract(MONTH\n" +
                "                                       FROM pp.startdate) report_month, min(0) tmp\n" +
                "                 FROM geographic_zones gz\n" +
                "                 CROSS JOIN processing_periods pp\n" +
                "                 WHERE extract(YEAR\n" +
                "                               FROM pp.startdate) = "+ getYearPredicate(filter.getYear()) +"\n" +
                "                   AND gz.levelid =\n" +
                "                     (SELECT max(id)\n" +
                "                      FROM geographic_levels)\n" +
                "                 GROUP BY 1,2\n" +
                "                 ORDER BY 1, 2 $$, $$\n" +
                "                 SELECT m\n" +
                "                 FROM generate_series(1,12) m $$) AS ( gzid int, jan varchar, feb varchar, mar varchar, apr varchar, may varchar, jun varchar, jul varchar, aug varchar, sep varchar, oct varchar, nov varchar, dec varchar)) a\n" +
                "LEFT JOIN\n" +
                "  (SELECT *\n" +
                "   FROM crosstab( $$\n" +
                "                 SELECT id, report_month, count(mintemp) mintemp\n" +
                "                 FROM vw_cc_temperature_line_item\n" +
                "                 WHERE  report_year = "+ getYearPredicate(filter.getYear()) +" AND mintemp < "+ CC_MIN_TEMP_AGGREGAT_TERESHOLD +"\n "+
                "                 GROUP BY 1, 2\n" +
                "                 ORDER BY 1, 2 $$, $$\n" +
                "                 SELECT m\n" +
                "                 FROM generate_series(1,12) m $$) AS (gzid integer, minjan numeric, minfeb numeric, minmar numeric, minapr numeric, minmay numeric, minjun numeric, minjul numeric, minaug numeric, minsep numeric, minoct numeric, minnov numeric, mindec numeric)) b ON b.gzid = a.gzid\n" +
                "LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, count(maxtemp) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"  AND maxtemp > "+CC_MAX_TEMP_AGGREGAT_TERESHOLD+"\n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS (gzid integer, maxjan numeric, maxfeb numeric, maxmar numeric, maxapr numeric, maxmay numeric, maxjun numeric, maxjul numeric, maxaug numeric, maxsep numeric, maxoct numeric, maxnov numeric, maxdec numeric)) c ON c.gzid = a.gzid \n" +
                "JOIN vw_districts vd ON vd.district_id = a.gzid\n" +
                "LEFT JOIN\n" +
                "  (SELECT geographic_zone_id gzid,\n" +
                "          sum(target_value_monthly) target\n" +
                "   FROM vw_vaccine_target_population tp\n" +
                "   WHERE category_id = 1\n" +
                "     AND YEAR = "+ getYearPredicate(filter.getYear()) +"   GROUP BY 1) f ON a.gzid = f.gzid\n" +
                  getZonePredicate(filter.getZoneId())
                +"ORDER BY 1,\n" +
                "         2,\n" +
                "         3";
        return sql;
    }

    public static String getColdChainTemperatureMinMaxRecorededTemprature(Map params){

        ColdChainTemperatureReportParam filter = (ColdChainTemperatureReportParam) params.get("filterCriteria");

        String sql = "  SELECT \n" +
                "    min(b.minjan) minjan,\n" +
                "    min(b.minfeb) minfeb,\n" +
                "    min(b.minmar) minmar,\n" +
                "    min(b.minapr) minapr,\n" +
                "    min(b.minmay) minmay,\n" +
                "    min(b.minjun) minjun,\n" +
                "    min(b.minjul) minjul,\n" +
                "    min(b.minaug) minaug,\n" +
                "    min(b.minsep) minsep,\n" +
                "    min(b.minoct) minoct,\n" +
                "    min(b.minnov) minnov,\n" +
                "    min(b.mindec) mindec,\n" +
                "    max(c.maxjan) maxjan,\n" +
                "    max(c.maxfeb) maxfeb,\n" +
                "    max(c.maxmar) maxmar,\n" +
                "    max(c.maxapr) maxapr,\n" +
                "    max(c.maxmay) maxmay,\n" +
                "    max(c.maxjun) maxjun,\n" +
                "    max(c.maxjul) maxjul,\n" +
                "    max(c.maxaug) maxaug,\n" +
                "    max(c.maxsep) maxsep,\n" +
                "    max(c.maxoct) maxoct,\n" +
                "    max(c.maxnov) maxnov,\n" +
                "    max(c.maxdec) maxdec\n" +
                "    FROM\n" +
                "  (SELECT *\n" +
                "   FROM crosstab( $$\n" +
                "                 SELECT gz.id, extract(MONTH\n" +
                "                                       FROM pp.startdate) report_month, min(0) tmp\n" +
                "                 FROM geographic_zones gz\n" +
                "                 CROSS JOIN processing_periods pp\n" +
                "                 WHERE extract(YEAR\n" +
                "                               FROM pp.startdate) = "+ getYearPredicate(filter.getYear()) +"\n" +
                "                   AND gz.levelid =\n" +
                "                     (SELECT max(id)\n" +
                "                      FROM geographic_levels)\n" +
                "                 GROUP BY 1,2\n" +
                "                 ORDER BY 1, 2 $$, $$\n" +
                "                 SELECT m\n" +
                "                 FROM generate_series(1,12) m $$) AS ( gzid int, jan varchar, feb varchar, mar varchar, apr varchar, may varchar, jun varchar, jul varchar, aug varchar, sep varchar, oct varchar, nov varchar, dec varchar)) a\n" +
                "LEFT JOIN\n" +
                "  (SELECT *\n" +
                "   FROM crosstab( $$\n" +
                "                 SELECT id, report_month, min(mintemp) mintemp\n" +
                "                 FROM vw_cc_temperature_line_item\n" +
                "                 WHERE  report_year = "+ getYearPredicate(filter.getYear()) +"\n" +
                "                 GROUP BY 1, 2\n" +
                "                 ORDER BY 1, 2 $$, $$\n" +
                "                 SELECT m\n" +
                "                 FROM generate_series(1,12) m $$) AS (gzid integer, minjan numeric, minfeb numeric, minmar numeric, minapr numeric, minmay numeric, minjun numeric, minjul numeric, minaug numeric, minsep numeric, minoct numeric, minnov numeric, mindec numeric)) b ON b.gzid = a.gzid\n" +
                "LEFT JOIN \n" +
                "                          (SELECT * \n" +
                "                           FROM crosstab( $$ \n" +
                "                                         SELECT id, report_month, max(maxtemp) maxtemp \n" +
                "                                         FROM vw_cc_temperature_line_item \n" +
                "                                         WHERE report_year =  "+ getYearPredicate(filter.getYear()) +"\n" +
                "                                         GROUP BY 1, 2 \n" +
                "                                         ORDER BY 1, 2 $$, $$ \n" +
                "                                         SELECT m \n" +
                "                                         FROM generate_series(1,12) m $$) AS (gzid integer, maxjan numeric, maxfeb numeric, maxmar numeric, maxapr numeric, maxmay numeric, maxjun numeric, maxjul numeric, maxaug numeric, maxsep numeric, maxoct numeric, maxnov numeric, maxdec numeric)) c ON c.gzid = a.gzid \n" +
                "JOIN vw_districts vd ON vd.district_id = a.gzid\n" +
                "LEFT JOIN\n" +
                "  (SELECT geographic_zone_id gzid,\n" +
                "          sum(target_value_monthly) target\n" +
                "   FROM vw_vaccine_target_population tp\n" +
                "   WHERE category_id = 1\n" +
                "     AND YEAR = "+ getYearPredicate(filter.getYear()) +"   GROUP BY 1) f ON a.gzid = f.gzid\n" +
                getZonePredicate(filter.getZoneId())
                +" ORDER BY 1,\n" +
                "         2,\n" +
                "         3";

        return sql;
    }
    
    public static Long getYearPredicate(Long year){
        return  year == 0 ? Calendar.getInstance().get(Calendar.YEAR) : year;
    }

    public static String getZonePredicate(Long zoneId){
        return "WHERE (vd.district_id = "+zoneId+" or vd.zone_id = "+zoneId+" or vd.region_id = "+zoneId+" or vd.parent = "+zoneId+")";
    }


    public static String getColdChainTemperatureSubReportData(){
        String sql = "select * from wv_vaccine_temperature_by_region";
        return sql;
    }

}
