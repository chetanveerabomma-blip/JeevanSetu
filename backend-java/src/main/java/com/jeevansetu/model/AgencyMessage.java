package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "agency_messages")
public class AgencyMessage {
    @Id
    private String id;

    @Indexed
    private String incidentId;

    @Indexed
    private String incidentCode;

    private String senderEmail;
    private String senderName;
    private Role senderRole; // CONTROL_CENTER, AMBULANCE, POLICE, FIRE, HOSPITAL, ADMIN
    private String agencyType; // e.g. "DISPATCH", "EMS", "TRAFFIC_POLICE", "FIRE_RESCUE", "ER_TRAUMA"
    private String message;
    private String priority; // "ROUTINE", "URGENT", "CRITICAL_ALERT"

    @Indexed
    private Instant timestamp = Instant.now();

    public AgencyMessage() {}

    public AgencyMessage(String incidentId, String incidentCode, String senderEmail, String senderName,
                         Role senderRole, String agencyType, String message, String priority) {
        this.incidentId = incidentId;
        this.incidentCode = incidentCode;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.agencyType = agencyType;
        this.message = message;
        this.priority = priority != null ? priority : "ROUTINE";
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public Role getSenderRole() { return senderRole; }
    public void setSenderRole(Role senderRole) { this.senderRole = senderRole; }

    public String getAgencyType() { return agencyType; }
    public void setAgencyType(String agencyType) { this.agencyType = agencyType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
