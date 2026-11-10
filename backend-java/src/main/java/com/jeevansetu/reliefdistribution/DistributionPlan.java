package com.jeevansetu.reliefdistribution;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relief_distribution_plans")
public class DistributionPlan {
    @Id
    private String id;
    private String planCode;
    private String reliefCenterId;
    private String reliefCenterName;
    private int targetBeneficiariesCount;
    private List<PlanItem> requestedItems = new ArrayList<>();
    private List<PlanItem> approvedItems = new ArrayList<>();
    private String status = "DRAFT"; // DRAFT, APPROVED, DISPATCHED, DELIVERED
    private String approvedBy;
    private Instant createdAt = Instant.now();
    private Instant dispatchedAt;
    private Instant deliveredAt;

    public static class PlanItem {
        private String itemCode;
        private String itemName;
        private int quantity;
        private String unit;

        public PlanItem() {}
        public PlanItem(String itemCode, String itemName, int quantity, String unit) {
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unit = unit;
        }

        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    public DistributionPlan() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }

    public String getReliefCenterId() { return reliefCenterId; }
    public void setReliefCenterId(String reliefCenterId) { this.reliefCenterId = reliefCenterId; }

    public String getReliefCenterName() { return reliefCenterName; }
    public void setReliefCenterName(String reliefCenterName) { this.reliefCenterName = reliefCenterName; }

    public int getTargetBeneficiariesCount() { return targetBeneficiariesCount; }
    public void setTargetBeneficiariesCount(int targetBeneficiariesCount) { this.targetBeneficiariesCount = targetBeneficiariesCount; }

    public List<PlanItem> getRequestedItems() { return requestedItems; }
    public void setRequestedItems(List<PlanItem> requestedItems) { this.requestedItems = requestedItems; }

    public List<PlanItem> getApprovedItems() { return approvedItems; }
    public void setApprovedItems(List<PlanItem> approvedItems) { this.approvedItems = approvedItems; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
}
