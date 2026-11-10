package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "inventory_transactions")
public class InventoryTransaction {
    @Id
    private String id;

    @Indexed
    private String inventoryItemId;
    private String itemCode;
    private String itemName;

    private String transactionType; // RESTOCK, ALLOCATE, RETURN, WRITE_OFF, CONSUMED
    private int quantity;
    private int balanceAfter;

    private String destinationRef; // Incident ID or Disaster Event ID
    private String performedBy;
    private String notes;
    private Instant timestamp = Instant.now();

    public InventoryTransaction() {}

    public InventoryTransaction(String inventoryItemId, String itemCode, String itemName, String transactionType, int quantity, int balanceAfter, String destinationRef, String performedBy, String notes) {
        this.inventoryItemId = inventoryItemId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.balanceAfter = balanceAfter;
        this.destinationRef = destinationRef;
        this.performedBy = performedBy;
        this.notes = notes;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(String inventoryItemId) { this.inventoryItemId = inventoryItemId; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getDestinationRef() { return destinationRef; }
    public void setDestinationRef(String destinationRef) { this.destinationRef = destinationRef; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
