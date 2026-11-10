package com.jeevansetu.maps;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "EmergencyZones")
public class EmergencyZone {
    @Id
    private String id;
    private String zoneName;
    private String zoneType; // FLOOD_AFFECTED, FIRE_PERIMETER, ACCIDENT_SCENE, BLOCKED_ROAD, RESTRICTED_AREA, SAFE_ZONE
    private String shapeType = "CIRCLE"; // CIRCLE, POLYGON
    private double centerLatitude;
    private double centerLongitude;
    private double radiusMeters;
    private List<List<Double>> polygonCoordinates = new ArrayList<>(); // [[lng, lat], ...]
    private String severity = "HIGH"; // CRITICAL, HIGH, MODERATE, SAFE
    private String advisoryNotice;
    private boolean active = true;
    private String createdBy;
    private Instant createdAt = Instant.now();

    public EmergencyZone() {}

    public EmergencyZone(String zoneName, String zoneType, double centerLat, double centerLng, double radiusMeters, String advisoryNotice) {
        this.zoneName = zoneName;
        this.zoneType = zoneType;
        this.centerLatitude = centerLat;
        this.centerLongitude = centerLng;
        this.radiusMeters = radiusMeters;
        this.advisoryNotice = advisoryNotice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public String getShapeType() { return shapeType; }
    public void setShapeType(String shapeType) { this.shapeType = shapeType; }

    public double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(double centerLatitude) { this.centerLatitude = centerLatitude; }

    public double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(double centerLongitude) { this.centerLongitude = centerLongitude; }

    public double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(double radiusMeters) { this.radiusMeters = radiusMeters; }

    public List<List<Double>> getPolygonCoordinates() { return polygonCoordinates; }
    public void setPolygonCoordinates(List<List<Double>> polygonCoordinates) { this.polygonCoordinates = polygonCoordinates; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getAdvisoryNotice() { return advisoryNotice; }
    public void setAdvisoryNotice(String advisoryNotice) { this.advisoryNotice = advisoryNotice; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
