package com.jeevansetu.repository;

import com.jeevansetu.model.InventoryTransaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends MongoRepository<InventoryTransaction, String> {
    List<InventoryTransaction> findByInventoryItemId(String inventoryItemId, Sort sort);
    List<InventoryTransaction> findByDestinationRef(String destinationRef, Sort sort);
}
