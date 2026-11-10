package com.jeevansetu.reliefdistribution;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ReliefSupplyService {

    private final SupplyInventoryRepository inventoryRepository;
    private final DistributionPlanRepository planRepository;
    private final SupplyDistributionRepository distributionRepository;

    public ReliefSupplyService(SupplyInventoryRepository inventoryRepository,
                               DistributionPlanRepository planRepository,
                               SupplyDistributionRepository distributionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.planRepository = planRepository;
        this.distributionRepository = distributionRepository;
    }

    @PostConstruct
    public void seedDefaultReliefSupplies() {
        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(new SupplyInventory("SUP-FOOD-01", "High-Protein Emergency Meal Rations (Pack of 10)", "FOOD_PACK", 1500, "packs", 300, "Central Disaster Depot A"));
            inventoryRepository.save(new SupplyInventory("SUP-WTR-20L", "Purified Drinking Water 20L Carboys", "DRINKING_WATER", 800, "carboys", 150, "Central Water Reserve West"));
            inventoryRepository.save(new SupplyInventory("SUP-MED-TRM", "Trauma Dressing & Burn Treatment Kits", "MEDICINE_KIT", 350, "kits", 80, "Red Cross Medical Depot"));
            inventoryRepository.save(new SupplyInventory("SUP-BLK-THM", "Fleece Thermal Emergency Blankets", "BLANKET", 2200, "units", 400, "Civil Defense Warehouse"));
        }
    }

    // SupplyInventoryService operations
    public List<SupplyInventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public SupplyInventory updateStock(String id, int quantityDelta) {
        SupplyInventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        item.setCurrentStock(Math.max(0, item.getCurrentStock() + quantityDelta));
        return inventoryRepository.save(item);
    }

    // DistributionService operations
    public DistributionPlan createPlan(DistributionPlan plan) {
        plan.setPlanCode("DP-" + (1000 + new Random().nextInt(9000)));
        plan.setStatus("DRAFT");
        plan.setCreatedAt(Instant.now());
        if (plan.getApprovedItems() == null || plan.getApprovedItems().isEmpty()) {
            plan.setApprovedItems(new ArrayList<>(plan.getRequestedItems()));
        }
        return planRepository.save(plan);
    }

    public List<DistributionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public DistributionPlan approvePlan(String planId, String approver) {
        DistributionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.setStatus("APPROVED");
        plan.setApprovedBy(approver);
        return planRepository.save(plan);
    }

    // DeliveryTrackingService operations
    public SupplyDistributionRecord dispatchSupplies(String planId, String vehicleNo, String driverName, String driverPhone) {
        DistributionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Deduct inventory quantities atomically
        for (DistributionPlan.PlanItem item : plan.getApprovedItems()) {
            inventoryRepository.findByItemCode(item.getItemCode()).ifPresent(inv -> {
                inv.setCurrentStock(Math.max(0, inv.getCurrentStock() - item.getQuantity()));
                inventoryRepository.save(inv);
            });
        }

        plan.setStatus("DISPATCHED");
        plan.setDispatchedAt(Instant.now());
        planRepository.save(plan);

        SupplyDistributionRecord record = new SupplyDistributionRecord();
        record.setPlanId(plan.getId());
        record.setReliefCenterId(plan.getReliefCenterId());
        record.setVehicleRegistrationNumber(vehicleNo);
        record.setDriverName(driverName);
        record.setDriverContact(driverPhone);
        record.setDeliveredItems(new ArrayList<>(plan.getApprovedItems()));
        record.setDispatchedAt(Instant.now());

        return distributionRepository.save(record);
    }

    public SupplyDistributionRecord confirmDelivery(String recordId, String recipientName, String notes) {
        SupplyDistributionRecord record = distributionRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Distribution record not found"));

        record.setRecipientOfficerName(recipientName);
        record.setRecipientSignatureNotes(notes);
        record.setDeliveryConfirmedAt(Instant.now());

        // Update parent plan status
        if (record.getPlanId() != null) {
            planRepository.findById(record.getPlanId()).ifPresent(p -> {
                p.setStatus("DELIVERED");
                p.setDeliveredAt(Instant.now());
                planRepository.save(p);
            });
        }

        return distributionRepository.save(record);
    }

    public List<SupplyDistributionRecord> getDistributionHistory() {
        return distributionRepository.findAll();
    }
}
