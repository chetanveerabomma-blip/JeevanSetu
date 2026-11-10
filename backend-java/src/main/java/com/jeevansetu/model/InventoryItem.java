package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "inventory_items")
public class InventoryItem {
    @Id
    private String id;

    @Indexed(unique = true)
    private String itemCode;

    private String name;

    @Indexed
    private String category; // AMBULANCE, MEDICAL_EQUIPMENT, FIRST_AID_KIT, OXYGEN_CYLINDER, FIRE_SAFETY, RESCUE_VEHICLE, FOOD_SUPPLY, DRINKING_WATER, RELIEF_MATERIAL

    private String locationName; // Base Station, Central Hospital, Depot-A
    private double latitude;
    private double longitude;

    private int totalQuantity = 0;
    private int availableQuantity = 0;
    private int allocatedQuantity = 0;
    private int minStockThreshold = 5;

    private String unit = "UNITS"; // UNITS, LITERS, KITS, CYLINDERS, BOXES
    private String condition = "EXCELLENT"; // EXCELLENT, GOOD, FAIR, NEEDS_SERVICE, DAMAGED

    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String remarks;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public InventoryItem() {}

    public boolean isLowStock() {
        return availableQuantity <= minStockThreshold;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        this.availableQuantity = Math.max(0, totalQuantity - this.allocatedQuantity);
    }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(int allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
        this.availableQuantity = Math.max(0, this.totalQuantity - allocatedQuantity);
    }

    public int getMinStockThreshold() { return minStockThreshold; }
    public void setMinStockThreshold(int minStockThreshold) { this.minStockThreshold = minStockThreshold; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }

    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
