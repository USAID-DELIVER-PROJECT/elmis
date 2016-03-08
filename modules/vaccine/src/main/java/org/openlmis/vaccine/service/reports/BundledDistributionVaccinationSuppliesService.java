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

package org.openlmis.vaccine.service.reports;

import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplies;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplyDistrict;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplyRegion;
import org.openlmis.vaccine.domain.reports.BundledDistributionVaccinationSupplyReport;
import org.openlmis.vaccine.repository.reports.BundledDistributionVaccinationSuppliesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BundledDistributionVaccinationSuppliesService {
    @Autowired
    private BundledDistributionVaccinationSuppliesRepository vaccinationSuppliesRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineReportService.class);


    public BundledDistributionVaccinationSupplyReport getBundledDistributionVaccinationSupplies(Long year, Long productId){

        List<BundledDistributionVaccinationSupplies> vaccinationSupplyList=null;
        BundledDistributionVaccinationSupplyRegion vaccinationSupplyRegion=null;
        BundledDistributionVaccinationSupplyDistrict vaccinationSupplyDistrict=null;
        BundledDistributionVaccinationSupplyReport vaccinationSupplyReport=null;
        Long totalPopulation=0L;
        try {
            vaccinationSupplyReport= new BundledDistributionVaccinationSupplyReport();
            vaccinationSupplyList=vaccinationSuppliesRepository.getBundledDistributionVaccinationSupplies(year, productId);
            vaccinationSupplyRegion=vaccinationSuppliesRepository.getBundledDistributionVaccinationSuppliesRegionSummary(year, productId);
            vaccinationSupplyDistrict=vaccinationSuppliesRepository.getBundledDistributionVaccinationSuppliesDistrictSummary(year, productId);
            vaccinationSupplyReport.setVaccinationSuppliesList(vaccinationSupplyList);
            vaccinationSupplyReport.setVaccinationSupplyRegion(vaccinationSupplyRegion);
            vaccinationSupplyReport.setVaccinationSupplyDistrict(vaccinationSupplyDistrict);
            for (BundledDistributionVaccinationSupplies vaccinationSupplies: vaccinationSupplyList){
                totalPopulation=   totalPopulation+vaccinationSupplies.getPopulation();
            }
            vaccinationSupplyReport.setTotalPopulation(totalPopulation);
        }catch (Exception ex){
            LOGGER.warn("Exception "+ ex.getMessage(),ex);
        }


        return  vaccinationSupplyReport;
    }


}

