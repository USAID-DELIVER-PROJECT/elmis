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

package org.openlmis.core.repository;

import org.openlmis.core.domain.SupplyPartner;
import org.openlmis.core.domain.SupplyPartnerProgram;
import org.openlmis.core.domain.SupplyPartnerProgramFacility;
import org.openlmis.core.domain.SupplyPartnerProgramProduct;
import org.openlmis.core.repository.mapper.SupplyPartnerProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SupplyPartnerProgramRepository {

  @Autowired
  private SupplyPartnerProgramMapper mapper;

  public List<SupplyPartner> getPartnersThatSupportProgram(Long programId) {
    return mapper.getPartnersThatSupportProgram(programId);
  }

  public void insert(SupplyPartnerProgram spp) {
    mapper.insert(spp);
    saveProductAndFacilities(spp);
  }

  private void saveProductAndFacilities(SupplyPartnerProgram spp) {
    mapper.deleteProducts(spp.getId());
    mapper.deleteFacilities(spp.getId());
    for (SupplyPartnerProgramFacility facility : spp.getFacilities()) {
      if(facility.getActive()) {
        facility.setSupplyPartnerProgramId(spp.getId());
        mapper.insertFacilities(facility);
      }
    }

    for (SupplyPartnerProgramProduct product : spp.getProducts()) {
      if(product.getActive()) {
        product.setSupplyPartnerProgramId(spp.getId());
        mapper.insertProduct(product);
      }
    }
  }

  public void deleteForSupplyPartner(Long spp) {
    List<SupplyPartnerProgram> programs = mapper.getProgramsForPartner(spp);
    for (SupplyPartnerProgram p : programs) {
      mapper.deleteFacilities(p.getId());
      mapper.deleteProducts(p.getId());
    }
    mapper.delete(spp);
  }

  public void update(SupplyPartnerProgram spp) {
    mapper.update(spp);
    saveProductAndFacilities(spp);
  }

  public List<SupplyPartnerProgram> getSubscriptions(Long facilityId, Long programId) {
    return mapper.getSubscriptions(facilityId, programId);
  }

  public List<SupplyPartnerProgram> getSubscriptionsWithDetails(Long facilityId, Long programId) {
    return mapper.getSubscriptionsWithDetails(facilityId, programId);
  }
}
