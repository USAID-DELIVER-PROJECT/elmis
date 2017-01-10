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

import org.openlmis.core.domain.*;
import org.openlmis.core.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RegimenRepository is Repository class for Regimen related database operations.
 */

@Repository
public class RegimenRepository {

    @Autowired
    RegimenMapper mapper;

    @Autowired
    RegimenCategoryMapper regimenCategoryMapper;

    @Autowired
    DosageFrequencyMapper dosageFrequencyMapper;

    @Autowired
    RegimenCombinationConstituentMapper regimenCombinationConstituentMapper;

    @Autowired
    RegimenProductCombinationMapper regimenProductCombinationMapper;

    @Autowired
    RegimenConstituentDosageMapper regimenConstituentDosageMapper;
    @Autowired
    DosageUnitMapper dosageUnitMapper;


    public List<Regimen> getByProgram(Long programId) {
        return mapper.getByProgram(programId);
    }

    public Regimen getById(Long id) {
        return mapper.getById(id);
    }

    public List<RegimenCategory> getAllRegimenCategories() {
        return regimenCategoryMapper.getAll();
    }

    public void save(List<Regimen> regimens, Long userId) {
        for (Regimen regimen : regimens) {
            regimen.setModifiedBy(userId);
            if (regimen.getId() == null) {
                regimen.setCreatedBy(userId);
                mapper.insert(regimen);
            }
            mapper.update(regimen);
        }
    }

    public void saveProductCombination(List<RegimenProductCombination> productCombinations, Long userId) {
        for (RegimenProductCombination productCombination : productCombinations) {
            productCombination.setModifiedBy(userId);
            if (productCombination.getId() == null) {
                productCombination.setCreatedBy(userId);
                mapper.insertProcuctCombination(productCombination);
            }
            mapper.updateProductCombination(productCombination);
        }
    }

    public List<Regimen> getAllRegimens() {
        return mapper.getAllRegimens();
    }

    public List<DosageFrequency> getAllDosageFrequencies() {
        return dosageFrequencyMapper.getAll();
    }

    public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents() {
        return regimenCombinationConstituentMapper.getAll();
    }

    public List<RegimenCombinationConstituent> getAllRegimenCombinationConstituents(Long combinationId) {
        return regimenCombinationConstituentMapper.getAllCombinationConstituents(combinationId);
    }

    public List<RegimenConstituentDosage> getAllRegimenConstituentsDosages() {
        return regimenConstituentDosageMapper.getAll();
    }

    public List<RegimenConstituentDosage> getAllRegimenConstituentsDosages(Long constituentId) {
        return regimenConstituentDosageMapper.getConstituentDosageList(constituentId);
    }

    public List<RegimenProductCombination> getAllRegimenProductCombinations() {
        return regimenProductCombinationMapper.getAll();
    }

    public List<RegimenProductCombination> getAllRegimenProductCombinations(Long regimenId) {
        return regimenProductCombinationMapper.getAllRegimenCombinations(regimenId);
    }

    public void saveProductConstituent(RegimenCombinationConstituent combinationConstituent) {
        if (combinationConstituent.getId() == null) {
            regimenCombinationConstituentMapper.save(combinationConstituent);
            combinationConstituent.getDefaultDosage().setRegimenConstituent(combinationConstituent);
            regimenConstituentDosageMapper.save(combinationConstituent.getDefaultDosage());
        }
        regimenCombinationConstituentMapper.update(combinationConstituent);
    }

    public void saveRegimenConstituentDosage(RegimenConstituentDosage constituentDosage) {
        if (constituentDosage.getId() == null) {
            regimenConstituentDosageMapper.save(constituentDosage);
        } else {
            regimenConstituentDosageMapper.update(constituentDosage);
        }
    }

    public List<DosageUnit> getDosageUnits() {
        return dosageUnitMapper.getAll();
    }
}
