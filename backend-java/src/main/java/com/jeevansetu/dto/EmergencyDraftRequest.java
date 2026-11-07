package com.jeevansetu.dto;

import com.jeevansetu.model.EmergencyType;

public class EmergencyDraftRequest {
    private String conversationId;
    private EmergencyType type;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private int peopleAffected;
    private int injuredCount;
    private boolean unconscious;
    private boolean severeBleeding;
    private boolean fireInvolved;
    private boolean trapped;
    private String additionalInfo;
    private String contactPhone;
    private String reporterName;

    public EmergencyDraftRequest() {}

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public EmergencyType getType() { return type; }
    public void setType(EmergencyType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(int peopleAffected) { this.peopleAffected = peopleAffected; }

    public int getInjuredCount() { return injuredCount; }
    public void setInjuredCount(int injuredCount) { this.injuredCount = injuredCount; }

    public boolean isUnconscious() { return unconscious; }
    public void setUnconscious(boolean unconscious) { this.unconscious = unconscious; }

    public boolean isSevereBleeding() { return severeBleeding; }
    public void setSevereBleeding(boolean severeBleeding) { this.severeBleeding = severeBleeding; }

    public boolean isFireInvolved() { return fireInvolved; }
    public void setFireInvolved(boolean fireInvolved) { this.fireInvolved = fireInvolved; }

    public boolean isTrapped() { return trapped; }
    public void setTrapped(boolean trapped) { this.trapped = trapped; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
}
