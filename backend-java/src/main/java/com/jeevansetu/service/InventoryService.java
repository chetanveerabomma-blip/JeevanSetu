package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.InventoryItem;
import com.jeevansetu.model.InventoryTransaction;
import com.jeevansetu.repository.InventoryItemRepository;
import com.jeevansetu.repository.InventoryTransactionRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryItemRepository itemRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public InventoryService(InventoryItemRepository itemRepository,
                            InventoryTransactionRepository transactionRepository,
                            AuditLogService auditLogService,
                            WebSocketEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public InventoryItem addItem(InventoryItem item, String actorName) {
        if (item.getItemCode() == null || item.getItemCode().isBlank()) {
            item.setItemCode("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        item.setAvailableQuantity(item.getTotalQuantity() - item.getAllocatedQuantity());
        item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());

        InventoryItem saved = itemRepository.save(item);

        recordTransaction(saved.getId(), saved.getItemCode(), saved.getName(), "RESTOCK",
                saved.getTotalQuantity(), saved.getAvailableQuantity(), null, actorName, "Initial inventory intake");

        auditLogService.log("ADD_INVENTORY", saved.getId(), "INVENTORY", "Added: " + saved.getName());
        return saved;
    }

    public List<InventoryItem> getAllItems(String category) {
        if (category != null && !category.isBlank()) {
            return itemRepository.findByCategory(category, Sort.by(Sort.Direction.ASC, "name"));
        }
        return itemRepository.findAll(Sort.by(Sort.Direction.ASC, "category"));
    }

    public InventoryItem getItemById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + id));
    }

    public InventoryItem updateStock(String id, int quantityDelta, String transactionType, String destinationRef, String actorName, String notes) {
        InventoryItem item = getItemById(id);

        if ("RESTOCK".equalsIgnoreCase(transactionType)) {
            item.setTotalQuantity(item.getTotalQuantity() + quantityDelta);
            item.setAvailableQuantity(item.getAvailableQuantity() + quantityDelta);
        } else if ("ALLOCATE".equalsIgnoreCase(transactionType)) {
            if (item.getAvailableQuantity() < quantityDelta) {
                throw new IllegalArgumentException("Insufficient available stock. Available: " + item.getAvailableQuantity());
            }
            item.setAllocatedQuantity(item.getAllocatedQuantity() + quantityDelta);
            item.setAvailableQuantity(item.getAvailableQuantity() - quantityDelta);
        } else if ("RETURN".equalsIgnoreCase(transactionType)) {
            int toReturn = Math.min(item.getAllocatedQuantity(), quantityDelta);
            item.setAllocatedQuantity(item.getAllocatedQuantity() - toReturn);
            item.setAvailableQuantity(item.getAvailableQuantity() + toReturn);
        } else if ("CONSUMED".equalsIgnoreCase(transactionType) || "WRITE_OFF".equalsIgnoreCase(transactionType)) {
            int toDeduct = Math.min(item.getAvailableQuantity(), quantityDelta);
            item.setTotalQuantity(Math.max(0, item.getTotalQuantity() - toDeduct));
            item.setAvailableQuantity(Math.max(0, item.getAvailableQuantity() - toDeduct));
        }

        item.setUpdatedAt(Instant.now());
        InventoryItem saved = itemRepository.save(item);

        recordTransaction(saved.getId(), saved.getItemCode(), saved.getName(), transactionType,
                quantityDelta, saved.getAvailableQuantity(), destinationRef, actorName, notes);

        // Low stock notification
        if (saved.isLowStock()) {
            eventPublisher.publish("INVENTORY_LOW_STOCK", Map.of(
                    "itemId", saved.getId(),
                    "itemCode", saved.getItemCode(),
                    "name", saved.getName(),
                    "available", saved.getAvailableQuantity(),
                    "threshold", saved.getMinStockThreshold()
            ));
        }

        return saved;
    }

    public void deleteItem(String id, String actorName) {
        InventoryItem item = getItemById(id);
        itemRepository.delete(item);
        auditLogService.log("DELETE_INVENTORY", id, "INVENTORY", "Deleted: " + item.getName() + " by " + actorName);
    }

    public List<InventoryItem> getLowStockAlerts() {
        return itemRepository.findAll().stream()
                .filter(InventoryItem::isLowStock)
                .collect(Collectors.toList());
    }

    public List<InventoryTransaction> getItemTransactions(String itemId) {
        return transactionRepository.findByInventoryItemId(itemId, Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    public Map<String, Object> getInventoryOverview() {
        List<InventoryItem> all = itemRepository.findAll();
        int totalItems = all.size();
        int totalStock = all.stream().mapToInt(InventoryItem::getTotalQuantity).sum();
        int allocatedStock = all.stream().mapToInt(InventoryItem::getAllocatedQuantity).sum();
        int availableStock = all.stream().mapToInt(InventoryItem::getAvailableQuantity).sum();
        long lowStockCount = all.stream().filter(InventoryItem::isLowStock).count();

        return Map.of(
                "totalItems", totalItems,
                "totalStock", totalStock,
                "allocatedStock", allocatedStock,
                "availableStock", availableStock,
                "lowStockAlerts", lowStockCount
        );
    }

    private void recordTransaction(String itemId, String itemCode, String name, String type, int qty, int balance, String ref, String actor, String notes) {
        InventoryTransaction tx = new InventoryTransaction(itemId, itemCode, name, type, qty, balance, ref, actor, notes);
        transactionRepository.save(tx);
    }
}
