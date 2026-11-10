package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "relief_requests")
public class ReliefRequest {
    @Id
    private String id;

    @Indexed
    private String disasterEventId;
    private String reliefCenterId;
    private String reliefCenterName;

    private String requestType; // FOOD, WATER, MEDICAL_SUPPLIES, BLANKETS, RESCUE_BOATS, EVACUATION
    private int quantityRequested;
    private int quantityDispatched = 0;
    private String urgency = "HIGH"; // CRITICAL, HIGH, NORMAL

    @Indexed
    private String status = "PENDING"; // PENDING, APPROVED, IN_TRANSIT, FULFILLED, CANCELLED

    private String requestedBy;
    private String contactPhone;
    private String destinationAddress;
    private Instant createdAt = Instant.now();
    private Instant fulfilledAt;

    public ReliefRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisasterEventId() { return disasterEventId; }
    public void setDisasterEventId(String disasterEventId) { this.disasterEventId = disasterEventId; }

    public String getReliefCenterId() { return reliefCenterId; }
    public void setReliefCenterId(String reliefCenterId) { this.reliefCenterId = reliefCenterId; }

    public String getReliefCenterName() { return reliefCenterName; }
    public void setReliefCenterName(String reliefCenterName) { this.reliefCenterName = reliefCenterName; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public int getQuantityRequested() { return quantityRequested; }
    public void setQuantityRequested(int quantityRequested) { this.quantityRequested = quantityRequested; }

    public int getQuantityDispatched() { return quantityDispatched; }
    public void setQuantityDispatched(int quantityDispatched) { this.quantityDispatched = quantityDispatched; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getFulfilledAt() { return fulfilledAt; }
    public void setFulfilledAt(Instant fulfilledAt) { this.fulfilledAt = fulfilledAt; }
}
