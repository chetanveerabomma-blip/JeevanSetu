package com.jeevansetu.maps;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "AmbulanceLocations")
public class AmbulanceLocation {
    @Id
    private String id;
    private String ambulanceId;
    private String unitCode;
    private String organization;
    private double latitude;
    private double longitude;
    private double speedKmH = 0.0;
    private double headingDegrees = 0.0;
    private String status = "AVAILABLE"; // AVAILABLE, EN_ROUTE, ON_SCENE, RETURNING, MAINTENANCE
    private String currentIncidentId;
    private Instant lastUpdated = Instant.now();

    public AmbulanceLocation() {}

    public AmbulanceLocation(String ambulanceId, String unitCode, double latitude, double longitude, String status) {
        this.ambulanceId = ambulanceId;
        this.unitCode = unitCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(String ambulanceId) { this.ambulanceId = ambulanceId; }

    public String getUnitCode() { return unitCode; }
    public void setUnitCode(String unitCode) { this.unitCode = unitCode; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getSpeedKmH() { return speedKmH; }
    public void setSpeedKmH(double speedKmH) { this.speedKmH = speedKmH; }

    public double getHeadingDegrees() { return headingDegrees; }
    public void setHeadingDegrees(double headingDegrees) { this.headingDegrees = headingDegrees; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentIncidentId() { return currentIncidentId; }
    public void setCurrentIncidentId(String currentIncidentId) { this.currentIncidentId = currentIncidentId; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
