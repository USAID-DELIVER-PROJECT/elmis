package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.vaccine.domain.inventory.VaccineDistributionStatusChange;
import org.springframework.stereotype.Repository;


@Repository
public interface VaccineInventoryDistributionStatusMapper {

    @Insert(" INSERT INTO vaccine_distribution_status_changes(distributionid, status, createdby, createddate, modifiedby, modifieddate)\n" +
            "VALUES (#{distributionId}, #{status}, #{createdBy}, NOW(), #{modifiedBy}, NOW()) ")
    @Options(useGeneratedKeys = true)
    Integer Insert(VaccineDistributionStatusChange statusChange);

}
