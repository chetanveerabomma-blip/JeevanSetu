package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "emergencies")
public class Emergency {
    @Id
    private String id;

    @Indexed(unique = true)
    private String incidentCode;

    @Indexed
    private EmergencyType type;

    @Indexed
    private EmergencyPriority priority;

    @Indexed
    private EmergencyStatus status = EmergencyStatus.REPORTED;

    private String reporterId;
    private String reporterName;
    private String reporterPhone;

    private double latitude;
    private double longitude;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String description;
    private int peopleAffected;
    private int injuredCount;
    private boolean unconscious;
    private boolean severeBleeding;
    private boolean fireInvolved;
    private boolean trapped;
    private String additionalInfo;

    private List<ResourceType> requiredResources = new ArrayList<>();
    private List<String> assignedResourceIds = new ArrayList<>();

    @Indexed
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant resolvedAt;

    private String aiCategory;
    private String aiSeverity;
    private String aiSummary;

    public Emergency() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public EmergencyType getType() { return type; }
    public void setType(EmergencyType type) { this.type = type; }

    public EmergencyPriority getPriority() { return priority; }
    public void setPriority(EmergencyPriority priority) { this.priority = priority; }

    public EmergencyStatus getStatus() { return status; }
    public void setStatus(EmergencyStatus status) { this.status = status; }

    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
        if (this.longitude != 0) {
            this.location = new GeoJsonPoint(this.longitude, this.latitude);
        }
    }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
        if (this.latitude != 0) {
            this.location = new GeoJsonPoint(this.longitude, this.latitude);
        }
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public GeoJsonPoint getLocation() { return location; }
    public void setLocation(GeoJsonPoint location) {
        this.location = location;
        if (location != null) {
            this.longitude = location.getX();
            this.latitude = location.getY();
        }
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public List<ResourceType> getRequiredResources() { return requiredResources; }
    public void setRequiredResources(List<ResourceType> requiredResources) { this.requiredResources = requiredResources; }

    public List<String> getAssignedResourceIds() { return assignedResourceIds; }
    public void setAssignedResourceIds(List<String> assignedResourceIds) { this.assignedResourceIds = assignedResourceIds; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getAiCategory() { return aiCategory; }
    public void setAiCategory(String aiCategory) { this.aiCategory = aiCategory; }

    public String getAiSeverity() { return aiSeverity; }
    public void setAiSeverity(String aiSeverity) { this.aiSeverity = aiSeverity; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
