package org.openlmis.report.service;

import org.openlmis.report.model.dto.StockOnHandSummaryDTO;
import org.openlmis.report.repository.StockOnHandSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hassan on 9/10/17.
 */
@Service
public class StockOnHandSummaryService {
    @Autowired
    private StockOnHandSummaryRepository repository;

    public List<StockOnHandSummaryDTO> getStockOnHandSummary(Long userId, String statusDate){
        return repository.getStockOnHandSummary(userId, statusDate);
    }
}
