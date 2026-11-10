package com.jeevansetu.reunification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "safe_arrival_confirmations")
public class SafeArrivalConfirmation {
    @Id
    private String id;
    private String personName;
    private String familyGroupId;
    private String arrivalLocation;
    private String shelterId;
    private String statusMessage;
    private String reportedByPhone;
    private Instant arrivalTime = Instant.now();

    public SafeArrivalConfirmation() {}

    public SafeArrivalConfirmation(String personName, String familyGroupId, String arrivalLocation, String statusMessage) {
        this.personName = personName;
        this.familyGroupId = familyGroupId;
        this.arrivalLocation = arrivalLocation;
        this.statusMessage = statusMessage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getFamilyGroupId() { return familyGroupId; }
    public void setFamilyGroupId(String familyGroupId) { this.familyGroupId = familyGroupId; }

    public String getArrivalLocation() { return arrivalLocation; }
    public void setArrivalLocation(String arrivalLocation) { this.arrivalLocation = arrivalLocation; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getReportedByPhone() { return reportedByPhone; }
    public void setReportedByPhone(String reportedByPhone) { this.reportedByPhone = reportedByPhone; }

    public Instant getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Instant arrivalTime) { this.arrivalTime = arrivalTime; }
}
