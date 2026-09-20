package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private String recipientUserId;

    @Indexed
    private Role recipientRole;

    private String title;
    private String message;
    private String incidentId;
    private String incidentCode;
    private String type; // "EMERGENCY_ALERT", "ASSIGNMENT", "ARRIVAL", "HOSPITAL_ALERT"
    private boolean read = false;

    @Indexed
    private Instant createdAt = Instant.now();

    public Notification() {}

    public Notification(String recipientUserId, Role recipientRole, String title, String message, String incidentId, String incidentCode, String type) {
        this.recipientUserId = recipientUserId;
        this.recipientRole = recipientRole;
        this.title = title;
        this.message = message;
        this.incidentId = incidentId;
        this.incidentCode = incidentCode;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    public Role getRecipientRole() { return recipientRole; }
    public void setRecipientRole(Role recipientRole) { this.recipientRole = recipientRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
