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

@Document(collection = "volunteers")
public class Volunteer {
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed
    private String phone;
    private String email;

    private double latitude;
    private double longitude;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private List<String> skills = new ArrayList<>(); // "First Aid", "Blood Donation", "Rescue", "Driving", "General Assistance"

    @Indexed
    private String availabilityStatus = "AVAILABLE"; // AVAILABLE, ENGAGED, OFFLINE
    private double emergencyRadiusKm = 10.0;
    private String activeIncidentId;
    private Instant registeredAt = Instant.now();

    public Volunteer() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public double getEmergencyRadiusKm() { return emergencyRadiusKm; }
    public void setEmergencyRadiusKm(double emergencyRadiusKm) { this.emergencyRadiusKm = emergencyRadiusKm; }

    public String getActiveIncidentId() { return activeIncidentId; }
    public void setActiveIncidentId(String activeIncidentId) { this.activeIncidentId = activeIncidentId; }

    public Instant getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Instant registeredAt) { this.registeredAt = registeredAt; }
}
