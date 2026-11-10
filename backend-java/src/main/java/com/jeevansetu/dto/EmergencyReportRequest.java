package com.jeevansetu.dto;

import com.jeevansetu.model.EmergencyType;

public class EmergencyReportRequest {
    private EmergencyType type;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private int peopleAffected;
    private int injuredCount;
    private boolean unconscious;
    private boolean severeBleeding;
    private boolean fireInvolved;
    private boolean trapped;
    private String additionalInfo;
    private String clientGeneratedId;

    public EmergencyReportRequest() {}

    public EmergencyType getType() { return type; }
    public void setType(EmergencyType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

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

    public String getClientGeneratedId() { return clientGeneratedId; }
    public void setClientGeneratedId(String clientGeneratedId) { this.clientGeneratedId = clientGeneratedId; }
}
