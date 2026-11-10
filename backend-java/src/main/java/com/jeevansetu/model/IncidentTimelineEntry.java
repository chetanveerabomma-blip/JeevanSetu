package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "incident_timelines")
public class IncidentTimelineEntry {
    @Id
    private String id;

    @Indexed
    private String incidentId;

    @Indexed
    private String incidentCode;

    private String eventType; // CREATED, STATUS_CHANGE, DISPATCH_ASSIGNED, REMARK_ADDED, ESCALATED, ACKNOWLEDGED, RESOLVED
    private String description;
    private String previousStatus;
    private String newStatus;
    private String actorName;
    private String actorRole;
    private Instant timestamp = Instant.now();

    public IncidentTimelineEntry() {}

    public IncidentTimelineEntry(String incidentId, String incidentCode, String eventType, String description, String actorName, String actorRole) {
        this.incidentId = incidentId;
        this.incidentCode = incidentCode;
        this.eventType = eventType;
        this.description = description;
        this.actorName = actorName;
        this.actorRole = actorRole;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
