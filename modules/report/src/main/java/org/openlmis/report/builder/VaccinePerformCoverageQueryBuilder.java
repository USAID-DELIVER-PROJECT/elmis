package org.openlmis.report.builder;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.report.model.params.PerformanceByDropoutRateParam;

public class VaccinePerformCoverageQueryBuilder {

    public static final String FACILITY_CRITERIA = "filterCriteria";

    public static String prepareSqlStatement(PerformanceByDropoutRateParam filter) {
        String query =
                "-- dropout temp table \n" +
                        " with coverage as ( select geographic_zone_name district, facility_name facility," +
                        " product_name || ' - ' ||dose_id product," +
                        " within_outside_total vaccinated, \n" +
                        "case when denominator = 0 then 0 else round((within_outside_total / denominator::float)*100) end coverage\n" +
                        "from vw_vaccine_coverage vc\n" +
                        writePredicates(filter) +
                        "                group by 1,2,3,4,5,6,7,i.period_start_date \n" +
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
                        "                 ),              \n" +
                        "          mainQuery as(      select d.region_name, d.district_name,  d.facility_name, " +
                        "     d.facility_id,d.district_id, d.region_id, d.period_id,d.period_start_date, d.period_name," +
                        "round(d.bcg_vaccinated,2) bcg_vaccinated," +
                        "round(d.dtp1_vaccinated,2) dtp1_vaccinated," +
                        "round(d.mr_vaccinated,2) mr_vaccinated," +
                        "round(d.dtp3_vaccinated,2) dtp3_vaccinated," +
                        "    round(d.bcg_mr_dropout,2) bcg_mr_dropout,round(d.dtp1_dtp3_dropout,2) dtp1_dtp3_dropout," +
                        "     round(c.cum_bcg_vaccinated,2) cum_bcg_vaccinated, " +
                        "round(c.cum_mr_vaccinated,2) cum_mr_vaccinated,round(cum_dtp1_vaccinated,2) cum_dtp1_vaccinated, round(c.cum_dtp3_vaccinated,2) cum_dtp3_vaccinated," +
                        "round(c.cum_bcg_mr_dropout,2) cum_bcg_mr_dropout, round(c.cum_dtp1_dtp3_dropout,2)  cum_dtp1_dtp3_dropout " +
                        "     from dropout    d   ,cumulative c    " +
                        "     where d.facility_id= c.facility_id  and d.period_start_date=c.period_start_date\n" +
                        "                \n" +
                        "                 )";

        return query;
    }

    private static String writePredicates(PerformanceByDropoutRateParam param) {
        String predicate = "";
        predicate = " WHERE vc.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )  " +
                "  and vc.period_start_date >= to_date('" + param.getPeriod_start_date() + "'::text, 'YYYY-MM-DD') " +
                "and  vc.period_end_date< (to_date('" + param.getPeriod_end_date() + "'::text,'YYYY-MM-DD')) +  interval '1' day" +
                " and i.product_id= " + param.getProduct_id() +
                "and (d.parent = " + param.getGeographic_zone_id() + "::INT or  d.region_id = " + param.getGeographic_zone_id() + "::INT " +
                "or  d.district_id = " + param.getGeographic_zone_id() + "::INT " +
                "  or  0 = " + param.getGeographic_zone_id() + "::INT)"+
                " and  d.district_id in(select district_id from vw_user_facilities where user_id ="+param.getUserId()+" and program_id = fn_get_vaccine_program_id()) ";

        if (param.getFacility_id() != null && param.getFacility_id() != 0L) {
            predicate = predicate + "and i.facility_id=" + param.getFacility_id();
        }

        return predicate;
    }

}
