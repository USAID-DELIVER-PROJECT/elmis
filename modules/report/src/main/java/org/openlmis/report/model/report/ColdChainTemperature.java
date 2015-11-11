/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow; Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT; Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation; either version 3 of the License; or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful; but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSInteger   See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not; see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.report.model.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColdChainTemperature implements ReportData {

    String zone_name;
    String region_name;
    String district_name;
    Integer target;
    Integer minjan;
    Integer minfeb;
    Integer minmar;
    Integer minapr;
    Integer minmay;
    Integer minjun;
    Integer minjul;
    Integer minaug;
    Integer minsep;
    Integer minoct;
    Integer minnov;
    Integer mindec;
    Integer maxjan;
    Integer maxfeb;
    Integer maxmar;
    Integer maxapr;
    Integer maxmay;
    Integer maxjun;
    Integer maxjul;
    Integer maxaug;
    Integer maxsep;
    Integer maxoct;
    Integer maxnov;
    Integer maxdec;
    Integer minep_jan;
    Integer minep_feb;
    Integer minep_mar;
    Integer minep_apr;
    Integer minep_may;
    Integer minep_jun;
    Integer minep_jul;
    Integer minep_aug;
    Integer minep_sep;
    Integer minep_oct;
    Integer minep_nov;
    Integer minep_dec;
    Integer maxep_jan;
    Integer maxep_feb;
    Integer maxep_mar;
    Integer maxep_apr;
    Integer maxep_may;
    Integer maxep_jun;
    Integer maxep_jul;
    Integer maxep_aug;
    Integer maxep_sep;
    Integer maxep_oct;
    Integer maxep_nov;
    Integer maxep_dec;
}
