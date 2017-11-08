package org.openlmis.report.repository;

import org.openlmis.report.mapper.StockOnHandSummaryMapper;
import org.openlmis.report.mapper.VaccineStockStatusMapper;
import org.openlmis.report.model.dto.StockOnHandSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hassan on 9/10/17.
 */

@Repository
public class StockOnHandSummaryRepository {

    @Autowired
    private StockOnHandSummaryMapper mapper;

    public List<StockOnHandSummaryDTO> getStockOnHandSummary(Long userId, String statusDate){
        return mapper.getStockOnHandSummary(userId, statusDate);
    }
}
