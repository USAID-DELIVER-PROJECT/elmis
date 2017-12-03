/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.EquipmentModel;
import org.openlmis.equipment.repository.EquipmentModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EquipmentModelService {

    @Autowired
    EquipmentModelRepository repository;

    public List<EquipmentModel> getAll() {
        return repository.getAll();
    }

    public EquipmentModel getEquipmentModelById(Long id) {
        return repository.getEquipmentModelById(id);
    }

    public void deleteEquipmentModel(Long id) {
        repository.deleteEquipmentModel(id);
    }

    public void updateEquipmentModel(EquipmentModel obj) {
        repository.updateEquipmentModel(obj);
    }

    public void insertEquipmentModel(EquipmentModel obj) {
        repository.insertEquipmentModel(obj);
    }

    public List<EquipmentModel> getByEquipmentTypeId(Long equipmentTypeId){
        return repository.getByEquipmentTypeId(equipmentTypeId);
    }
}
