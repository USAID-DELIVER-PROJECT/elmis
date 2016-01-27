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

package org.openlmis.vaccine.domain.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrendOfMinMaxColdChainTempratureDetail extends BaseModel {
    private Long period_id;
    private String region_name;
    private String district_name;
    private Date period_name;
    private Date period_start_date;
    private Date period_end_date;
    private Long geographic_zone_id;
    private String geographic_zone_name;
    private Long level_id;
    private Long parent_id;
    private Long facility_id;
    private Long targetpopulation;
    private String facility_code;
    private String facility_name;
    private Long report_id;
    private Long programid;
    private Date reported_date;
    private String equipment_name;
    private String model;
    private Long yearofinstallation;
    private String equipment_type_name;
    private float mintemp;
    private float maxtemp;
    private float minepisodetemp;
    private float maxepisodetemp;
    private String energy_source;
    private String status;
    List<TrendOfMinMaxColdChainTempratureDetail> children;
}
