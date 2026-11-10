package com.jeevansetu.reliefdistribution;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relief-distribution")
public class ReliefDistributionController {

    private final ReliefSupplyService supplyService;

    public ReliefDistributionController(ReliefSupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<SupplyInventory>>> getInventory() {
        return ResponseEntity.ok(ApiResponse.ok("Relief supply inventory retrieved", supplyService.getAllInventory()));
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<DistributionPlan>> createPlan(@RequestBody DistributionPlan plan) {
        return ResponseEntity.ok(ApiResponse.ok("Relief distribution plan created", supplyService.createPlan(plan)));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<DistributionPlan>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.ok("Distribution plans retrieved", supplyService.getAllPlans()));
    }

    @PostMapping("/plans/{id}/approve")
    public ResponseEntity<ApiResponse<DistributionPlan>> approvePlan(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {
        String approver = (body != null && body.containsKey("approvedBy")) ? body.get("approvedBy") : "Relief Logistics Commander";
        return ResponseEntity.ok(ApiResponse.ok("Distribution plan approved", supplyService.approvePlan(id, approver)));
    }

    @PostMapping("/plans/{id}/dispatch")
    public ResponseEntity<ApiResponse<SupplyDistributionRecord>> dispatchPlan(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String vehicle = body.getOrDefault("vehicleNumber", "KA-01-RL-4020");
        String driver = body.getOrDefault("driverName", "Officer Ramesh Patel");
        String phone = body.getOrDefault("driverContact", "98450-99881");

        return ResponseEntity.ok(ApiResponse.ok("Supplies dispatched and deducted from inventory", supplyService.dispatchSupplies(id, vehicle, driver, phone)));
    }

    @PostMapping("/confirm-delivery/{recordId}")
    public ResponseEntity<ApiResponse<SupplyDistributionRecord>> confirmDelivery(
            @PathVariable String recordId,
            @RequestBody Map<String, String> body) {
        String recipient = body.getOrDefault("recipientOfficerName", "Camp Superintendent");
        String notes = body.getOrDefault("notes", "All pallets verified in intact condition");

        return ResponseEntity.ok(ApiResponse.ok("Delivery confirmation recorded", supplyService.confirmDelivery(recordId, recipient, notes)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SupplyDistributionRecord>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.ok("Distribution transaction history", supplyService.getDistributionHistory()));
    }
}
