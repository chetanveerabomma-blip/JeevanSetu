package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "blood_banks")
public class BloodBank {
    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String registrationId;

    private String address;
    private double latitude;
    private double longitude;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String phone;
    private String email;

    // e.g. "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    private Map<String, Integer> inventory = new HashMap<>();

    @Indexed
    private String status = "OPERATIONAL";
    private Instant lastUpdated = Instant.now();

    public BloodBank() {
        initDefaultInventory();
    }

    private void initDefaultInventory() {
        String[] types = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        for (String t : types) {
            inventory.putIfAbsent(t, 20);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

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

    public GeoJsonPoint getLocation() { return location; }
    public void setLocation(GeoJsonPoint location) {
        this.location = location;
        if (location != null) {
            this.longitude = location.getX();
            this.latitude = location.getY();
        }
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Map<String, Integer> getInventory() { return inventory; }
    public void setInventory(Map<String, Integer> inventory) { this.inventory = inventory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    public void setStockUnits(String type, int units) {
        if (this.inventory == null) {
            this.inventory = new HashMap<>();
        }
        this.inventory.put(type, units);
    }
}
