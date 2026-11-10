package com.jeevansetu.reliefdistribution;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "relief_distribution_records")
public class SupplyDistributionRecord {
    @Id
    private String id;
    private String planId;
    private String reliefCenterId;
    private String vehicleRegistrationNumber;
    private String driverName;
    private String driverContact;
    private List<DistributionPlan.PlanItem> deliveredItems = new ArrayList<>();
    private String recipientOfficerName;
    private String recipientSignatureNotes;
    private Instant dispatchedAt;
    private Instant deliveryConfirmedAt;

    public SupplyDistributionRecord() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getReliefCenterId() { return reliefCenterId; }
    public void setReliefCenterId(String reliefCenterId) { this.reliefCenterId = reliefCenterId; }

    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }
    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) { this.vehicleRegistrationNumber = vehicleRegistrationNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverContact() { return driverContact; }
    public void setDriverContact(String driverContact) { this.driverContact = driverContact; }

    public List<DistributionPlan.PlanItem> getDeliveredItems() { return deliveredItems; }
    public void setDeliveredItems(List<DistributionPlan.PlanItem> deliveredItems) { this.deliveredItems = deliveredItems; }

    public String getRecipientOfficerName() { return recipientOfficerName; }
    public void setRecipientOfficerName(String recipientOfficerName) { this.recipientOfficerName = recipientOfficerName; }

    public String getRecipientSignatureNotes() { return recipientSignatureNotes; }
    public void setRecipientSignatureNotes(String recipientSignatureNotes) { this.recipientSignatureNotes = recipientSignatureNotes; }

    public Instant getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(Instant dispatchedAt) { this.dispatchedAt = dispatchedAt; }

    public Instant getDeliveryConfirmedAt() { return deliveryConfirmedAt; }
    public void setDeliveryConfirmedAt(Instant deliveryConfirmedAt) { this.deliveryConfirmedAt = deliveryConfirmedAt; }
}
