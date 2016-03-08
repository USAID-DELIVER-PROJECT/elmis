
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
package org.openlmis.report.model.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ResultRow;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColdChainTemperature implements ResultRow {

    String zone_name;
    String region_name;
    String district_name;
    Double target;
    Double minjan;
    Double minfeb;
    Double minmar;
    Double minapr;
    Double minmay;
    Double minjun;
    Double minjul;
    Double minaug;
    Double minsep;
    Double minoct;
    Double minnov;
    Double mindec;
    Double maxjan;
    Double maxfeb;
    Double maxmar;
    Double maxapr;
    Double maxmay;
    Double maxjun;
    Double maxjul;
    Double maxaug;
    Double maxsep;
    Double maxoct;
    Double maxnov;
    Double maxdec;
    Double minep_jan;
    Double minep_feb;
    Double minep_mar;
    Double minep_apr;
    Double minep_may;
    Double minep_jun;
    Double minep_jul;
    Double minep_aug;
    Double minep_sep;
    Double minep_oct;
    Double minep_nov;
    Double minep_dec;
    Double maxep_jan;
    Double maxep_feb;
    Double maxep_mar;
    Double maxep_apr;
    Double maxep_may;
    Double maxep_jun;
    Double maxep_jul;
    Double maxep_aug;
    Double maxep_sep;
    Double maxep_oct;
    Double maxep_nov;
    Double maxep_dec;
}
