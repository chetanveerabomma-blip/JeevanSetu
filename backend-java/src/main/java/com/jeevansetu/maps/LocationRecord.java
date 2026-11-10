package com.jeevansetu.maps;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "Locations")
public class LocationRecord {
    @Id
    private String id;
    private String entityId;
    private String entityType; // AMBULANCE, HOSPITAL, INCIDENT, CITIZEN, SHELTER
    private double latitude;
    private double longitude;
    private String address;
    private Instant timestamp = Instant.now();

    public LocationRecord() {}

    public LocationRecord(String entityId, String entityType, double latitude, double longitude, String address) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
