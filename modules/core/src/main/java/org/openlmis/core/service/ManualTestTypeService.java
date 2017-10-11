/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.service;


import org.openlmis.core.domain.ManualTestType;
import org.openlmis.core.repository.ManualTestTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManualTestTypeService {

    @Autowired
    private ManualTestTypeRepository repository;

    public List<ManualTestType> getAll(){
        return repository.getAll();
    }

    public ManualTestType getById(Long id){
        return repository.getById(id);
    }

    public void insert(ManualTestType type){
        repository.insert(type);
    }

    public void  update(ManualTestType type){
        repository.update(type);
    }

    public void save(ManualTestType testType) {
        if(testType.getId() == null)
            repository.insert(testType);
        else
            repository.update(testType);

    }

    public void remove(Long id) {
        repository.remove(id);
    }
}
