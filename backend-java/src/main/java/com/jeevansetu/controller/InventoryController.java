package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.InventoryItem;
import com.jeevansetu.model.InventoryTransaction;
import com.jeevansetu.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryOverview()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getAll(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllItems(category)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryItem>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryItem>> addItem(
            @RequestBody InventoryItem item,
            @RequestParam(defaultValue = "Inventory Manager") String actorName) {
        InventoryItem saved = inventoryService.addItem(item, actorName);
        return ResponseEntity.ok(ApiResponse.ok("Item added to inventory", saved));
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<InventoryItem>> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        int delta = Integer.parseInt(body.getOrDefault("quantityDelta", 0).toString());
        String type = (String) body.getOrDefault("transactionType", "RESTOCK");
        String destRef = (String) body.get("destinationRef");
        String actor = (String) body.getOrDefault("actorName", "Dispatcher");
        String notes = (String) body.getOrDefault("notes", "Stock change");

        InventoryItem updated = inventoryService.updateStock(id, delta, type, destRef, actor, notes);
        return ResponseEntity.ok(ApiResponse.ok("Inventory stock updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable String id,
            @RequestParam(defaultValue = "Admin") String actorName) {
        inventoryService.deleteItem(id, actorName);
        return ResponseEntity.ok(ApiResponse.message("Inventory item deleted"));
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockAlerts()));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiResponse<List<InventoryTransaction>>> getTransactions(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemTransactions(id)));
    }
}
