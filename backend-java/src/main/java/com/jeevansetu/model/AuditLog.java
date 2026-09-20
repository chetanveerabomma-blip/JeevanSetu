package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;

    @Indexed
    private String userEmail;

    private String userRole;

    @Indexed
    private String action; // e.g. "EMERGENCY_REPORTED", "RESPONDER_ASSIGNED", "STATUS_EN_ROUTE", "ICU_UPDATED"

    @Indexed
    private String incidentId;
    private String incidentCode;
    private String details;
    private String oldStatus;
    private String newStatus;
    private String ipAddress;

    @Indexed
    private Instant timestamp = Instant.now();

    public AuditLog() {}

    public AuditLog(String userEmail, String userRole, String action, String incidentId, String incidentCode, String details, String oldStatus, String newStatus) {
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.action = action;
        this.incidentId = incidentId;
        this.incidentCode = incidentCode;
        this.details = details;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
