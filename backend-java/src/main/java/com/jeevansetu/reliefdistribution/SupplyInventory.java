package com.jeevansetu.reliefdistribution;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "relief_supply_inventory")
public class SupplyInventory {
    @Id
    private String id;
    private String itemCode;
    private String itemName;
    private String itemCategory; // FOOD_PACK, DRINKING_WATER, MEDICINE_KIT, BLANKET, RESCUE_EQUIPMENT
    private int currentStock;
    private String unitOfMeasure; // packs, litres, kits, units
    private int minimumThreshold;
    private String warehouseLocation;

    public SupplyInventory() {}

    public SupplyInventory(String itemCode, String itemName, String itemCategory, int currentStock, String unitOfMeasure, int minimumThreshold, String warehouseLocation) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.currentStock = currentStock;
        this.unitOfMeasure = unitOfMeasure;
        this.minimumThreshold = minimumThreshold;
        this.warehouseLocation = warehouseLocation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public int getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(int minimumThreshold) { this.minimumThreshold = minimumThreshold; }

    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
}
