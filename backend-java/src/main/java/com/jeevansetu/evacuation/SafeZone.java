package com.jeevansetu.evacuation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "safe_zones")
public class SafeZone {
    @Id
    private String id;
    private String name;
    private String zoneType; // RELIEF_SHELTER, OPEN_GROUND, HOSPITAL_CAMP, CIVIC_STADIUM
    private String address;
    private double latitude;
    private double longitude;
    private int totalCapacity;
    private int currentOccupancy;
    private String contactPerson;
    private String contactPhone;
    private List<String> amenities = new ArrayList<>(); // Drinking Water, Medical Aid, Power Backup, Sanitation
    private boolean active = true;
    private Instant createdAt = Instant.now();

    public SafeZone() {}

    public SafeZone(String name, String zoneType, String address, double latitude, double longitude, int totalCapacity) {
        this.name = name;
        this.zoneType = zoneType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalCapacity = totalCapacity;
        this.currentOccupancy = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public int getAvailableCapacity() { return Math.max(0, totalCapacity - currentOccupancy); }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
