/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.equipment.repository;

import org.openlmis.equipment.domain.EquipmentModel;
import org.openlmis.equipment.repository.mapper.EquipmentMapper;
import org.openlmis.equipment.repository.mapper.EquipmentModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentModelRepository {

    @Autowired
    EquipmentModelMapper mapper;

    public List<EquipmentModel> getAll() {
     return mapper.getAll();
    }

    public EquipmentModel getEquipmentModelById(Long id) {
        return mapper.getEquipmentModelById(id);
    }

    public void deleteEquipmentModel(Long id) {
        mapper.deleteEquipmentModel(id);
    }

    public void updateEquipmentModel(EquipmentModel model) {
        mapper.updateEquipmentModel(model);
    }

    public void insertEquipmentModel(EquipmentModel model) {
        mapper.insertEquipmentModel(model);
    }

    public List<EquipmentModel> getByEquipmentTypeId(Long equipmentTypeId) {
        return mapper.getByEquipmentTypeId(equipmentTypeId);
    }
}
