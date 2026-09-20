package com.jeevansetu.dto;

import java.util.Map;

public class UpdateBloodStockRequest {
    private Map<String, Integer> inventory;

    public UpdateBloodStockRequest() {}

    public Map<String, Integer> getInventory() { return inventory; }
    public void setInventory(Map<String, Integer> inventory) { this.inventory = inventory; }
}
