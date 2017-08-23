package org.openlmis.stockmanagement.service;

import lombok.NoArgsConstructor;
import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.stockmanagement.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hassan on 8/22/17.
 */
@Service
@NoArgsConstructor
public class LotService {

    @Autowired
    private LotRepository repository;

    public void insertLot(Lot lot){
        if(lot.getId()==null)
        repository.getOrCreateLot(lot);
        else {
            repository.updateLot(lot);
        }
    }

    public void delete(Long id){
        repository.deleteLot(id);
    }

    public Lot getById(Long id){
        return repository.getById(id);
    }

    public List<Lot> getAll(){
        return repository.getAll();
    }
}
