package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "incident_updates")
public class IncidentUpdate {
    @Id
    private String id;

    @Indexed
    private String incidentId;

    @Indexed
    private String incidentCode;

    private String stage; // e.g. "REPORTED", "PRIORITY_CALCULATED", "ASSIGNED", "EN_ROUTE", "ARRIVED", "RESOLVED"
    private String message;
    private String actorRole;
    private String actorName;

    @Indexed
    private Instant timestamp = Instant.now();

    public IncidentUpdate() {}

    public IncidentUpdate(String incidentId, String incidentCode, String stage, String message, String actorRole, String actorName) {
        this.incidentId = incidentId;
        this.incidentCode = incidentCode;
        this.stage = stage;
        this.message = message;
        this.actorRole = actorRole;
        this.actorName = actorName;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
