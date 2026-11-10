package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "disaster_events")
public class DisasterEvent {
    @Id
    private String id;

    @Indexed(unique = true)
    private String eventCode;

    private String title;
    private String disasterType; // FLOOD, CYCLONE, EARTHQUAKE, FIRE, LANDSLIDE, CHEMICAL_SPILL, OTHER
    private String severity; // ADVISORY, WARNING, SEVERE, CATASTROPHIC
    private String description;

    private double centerLatitude;
    private double centerLongitude;
    private double affectedRadiusKm;
    private String regionName;

    @Indexed
    private String status = "ACTIVE"; // ACTIVE, CONTAINED, RECOVERY, RESOLVED

    private int casualtyCount = 0;
    private int displacedCount = 0;
    private int reliefCentersCount = 0;

    private List<String> activeAgencies = new ArrayList<>(); // NDRF, SDRF, POLICE, FIRE, HEALTH_DEPT

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant resolvedAt;

    public DisasterEvent() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDisasterType() { return disasterType; }
    public void setDisasterType(String disasterType) { this.disasterType = disasterType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(double centerLatitude) { this.centerLatitude = centerLatitude; }

    public double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(double centerLongitude) { this.centerLongitude = centerLongitude; }

    public double getAffectedRadiusKm() { return affectedRadiusKm; }
    public void setAffectedRadiusKm(double affectedRadiusKm) { this.affectedRadiusKm = affectedRadiusKm; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCasualtyCount() { return casualtyCount; }
    public void setCasualtyCount(int casualtyCount) { this.casualtyCount = casualtyCount; }

    public int getDisplacedCount() { return displacedCount; }
    public void setDisplacedCount(int displacedCount) { this.displacedCount = displacedCount; }

    public int getReliefCentersCount() { return reliefCentersCount; }
    public void setReliefCentersCount(int reliefCentersCount) { this.reliefCentersCount = reliefCentersCount; }

    public List<String> getActiveAgencies() { return activeAgencies; }
    public void setActiveAgencies(List<String> activeAgencies) { this.activeAgencies = activeAgencies; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
