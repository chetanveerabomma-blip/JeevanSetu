package com.jeevansetu.reliefdistribution;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SupplyInventoryRepository extends MongoRepository<SupplyInventory, String> {
    Optional<SupplyInventory> findByItemCode(String itemCode);
}
