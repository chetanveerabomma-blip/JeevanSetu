package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "incident_chat_messages")
public class IncidentChatMessage {
    @Id
    private String id;

    @Indexed
    private String incidentId;
    private String incidentCode;

    private String senderId;
    private String senderName;
    private String senderRole; // DISPATCHER, AMBULANCE, HOSPITAL, POLICE, FIRE, VOLUNTEER

    private String targetAgency; // ALL, AMBULANCE, HOSPITAL, POLICE, FIRE
    private String messageText;

    private String attachmentUrl;
    private String attachmentType; // IMAGE, DOCUMENT, AUDIO, LOCATION

    private boolean isRead = false;
    private String readBy;
    private Instant readAt;

    private Instant timestamp = Instant.now();

    public IncidentChatMessage() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getTargetAgency() { return targetAgency; }
    public void setTargetAgency(String targetAgency) { this.targetAgency = targetAgency; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getReadBy() { return readBy; }
    public void setReadBy(String readBy) { this.readBy = readBy; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
