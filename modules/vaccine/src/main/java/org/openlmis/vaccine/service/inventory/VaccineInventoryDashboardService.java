/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.service.inventory;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.vaccine.domain.inventory.EquipmentAlert;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;


/**
 * Exposes the services for handling stock cards.
 */

@Service
@NoArgsConstructor
public class VaccineInventoryDashboardService {

    @Autowired
    VaccineInventoryDashboardMapper mapper;

    @Autowired
    FacilityService facilityService;

    @Autowired
    ProgramService programService;

    @Autowired
    VaccineInventoryDistributionService distributionService;

    public List<EquipmentAlert> getNonFunctionalAlerts(Long userId) {

        Facility homeFacility = facilityService.getHomeFacility(userId);
        Long facilityId = homeFacility.getId();
        List<Program> programs = programService.getAllIvdPrograms();
        Long programId = programs.get(0).getId();

        List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
        StringBuilder str = new StringBuilder();
        str.append("(");
        for (Facility f : facilities) {
            str.append(f.getId());
            str.append(",");
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }
        str.append(")");

        return mapper.getNonFunctionalAlerts(str.toString());
    }
}