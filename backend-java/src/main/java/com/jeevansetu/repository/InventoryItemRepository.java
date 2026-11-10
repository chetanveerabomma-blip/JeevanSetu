package com.jeevansetu.repository;

import com.jeevansetu.model.InventoryItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends MongoRepository<InventoryItem, String> {
    Optional<InventoryItem> findByItemCode(String itemCode);
    List<InventoryItem> findByCategory(String category, Sort sort);
    List<InventoryItem> findByLocationName(String locationName);
    List<InventoryItem> findByCondition(String condition);
}
