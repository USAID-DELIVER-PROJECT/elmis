/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository;

import org.openlmis.core.domain.ManualTestType;
import org.openlmis.core.repository.mapper.ManualTestTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManualTestTypeRepository {

    @Autowired
    private ManualTestTypeMapper mapper;

    public List<ManualTestType> getAll(){
        return mapper.getAll();
    }

    public ManualTestType getById(Long id){
        return mapper.getById(id);
    }

    public void insert(ManualTestType type){
        mapper.insert(type);
    }

    public void  update(ManualTestType type){
        mapper.update(type);
    }

    public void remove(Long id) {
        mapper.remove(id);
    }
}
