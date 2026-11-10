package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "geo_zones")
public class GeoZone {
    @Id
    private String id;

    @Indexed
    private String zoneName;

    private String zoneType; // DANGER_ZONE, EVACUATION_ZONE, RESTRICTED_ZONE, BUFFER_ZONE
    private String severity; // CRITICAL, HIGH, MODERATE, ADVISORY

    private double centerLatitude;
    private double centerLongitude;
    private double radiusMeters; // Radius in meters

    @Indexed
    private String status = "ACTIVE"; // ACTIVE, MONITORED, INACTIVE, CANCELLED

    private String description;
    private String activeIncidentId;
    private String activeDisasterId;

    private String createdBy;
    private Instant createdAt = Instant.now();
    private Instant expiresAt;

    public GeoZone() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(double centerLatitude) { this.centerLatitude = centerLatitude; }

    public double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(double centerLongitude) { this.centerLongitude = centerLongitude; }

    public double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(double radiusMeters) { this.radiusMeters = radiusMeters; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getActiveIncidentId() { return activeIncidentId; }
    public void setActiveIncidentId(String activeIncidentId) { this.activeIncidentId = activeIncidentId; }

    public String getActiveDisasterId() { return activeDisasterId; }
    public void setActiveDisasterId(String activeDisasterId) { this.activeDisasterId = activeDisasterId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
