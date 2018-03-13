package org.openlmis.report.repository;

import org.openlmis.report.mapper.lookup.MsdStockStatusReportMapper;
import org.openlmis.report.model.dto.MSDStockStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class MSDStockStatusReportRepository {

    @Autowired
    private MsdStockStatusReportMapper mapper;


    public List<MSDStockStatusDTO> getAllMSDStockStatusReport(Long programId, Long periodId, String productCode) {
        return mapper.getAllMSDStockStatusReport(programId, periodId, productCode);
    }
    public List<HashMap<String,Object>>getStockStatus(){
        return mapper.getStockStatus();
    }

    public HashMap<String,Object>getStockColor(Long mos,Long levelId){
        return mapper.getStockColor(mos,levelId);
    }
}
