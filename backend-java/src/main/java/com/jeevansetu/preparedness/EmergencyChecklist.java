package com.jeevansetu.preparedness;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "emergency_checklists")
public class EmergencyChecklist {
    @Id
    private String id;
    private String title;
    private String targetAudience; // HOME, VEHICLE, WORKPLACE, FLOOD_BAG
    private String description;
    private List<ChecklistItem> items = new ArrayList<>();
    private String authoritativeSource;

    public static class ChecklistItem {
        private String itemName;
        private String quantity;
        private boolean essential;

        public ChecklistItem() {}
        public ChecklistItem(String itemName, String quantity, boolean essential) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.essential = essential;
        }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
        public boolean isEssential() { return essential; }
        public void setEssential(boolean essential) { this.essential = essential; }
    }

    public EmergencyChecklist() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ChecklistItem> getItems() { return items; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }

    public String getAuthoritativeSource() { return authoritativeSource; }
    public void setAuthoritativeSource(String authoritativeSource) { this.authoritativeSource = authoritativeSource; }
}
