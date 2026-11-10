package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "hospitals")
public class Hospital {
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

    private String emergencyContact;
    private int emergencyBeds;
    private int occupiedEmergencyBeds;
    private int icuBeds;
    private int occupiedIcuBeds;
    private int ventilators;
    private int occupiedVentilators;
    private int operatingRooms;
    private int occupiedOperatingRooms;
    private int ambulanceCapacity;
    private int availableStaff;

    @Indexed
    private String status = "AVAILABLE"; // AVAILABLE, CRITICAL_CAPACITY, FULL
    private Instant lastUpdated = Instant.now();

    public Hospital() {}

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

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public int getEmergencyBeds() { return emergencyBeds; }
    public void setEmergencyBeds(int emergencyBeds) { this.emergencyBeds = emergencyBeds; }

    public int getOccupiedEmergencyBeds() { return occupiedEmergencyBeds; }
    public void setOccupiedEmergencyBeds(int occupiedEmergencyBeds) { this.occupiedEmergencyBeds = occupiedEmergencyBeds; }

    public int getIcuBeds() { return icuBeds; }
    public void setIcuBeds(int icuBeds) { this.icuBeds = icuBeds; }

    public int getOccupiedIcuBeds() { return occupiedIcuBeds; }
    public void setOccupiedIcuBeds(int occupiedIcuBeds) { this.occupiedIcuBeds = occupiedIcuBeds; }

    public int getVentilators() { return ventilators; }
    public void setVentilators(int ventilators) { this.ventilators = ventilators; }

    public int getOccupiedVentilators() { return occupiedVentilators; }
    public void setOccupiedVentilators(int occupiedVentilators) { this.occupiedVentilators = occupiedVentilators; }

    public int getOperatingRooms() { return operatingRooms; }
    public void setOperatingRooms(int operatingRooms) { this.operatingRooms = operatingRooms; }

    public int getOccupiedOperatingRooms() { return occupiedOperatingRooms; }
    public void setOccupiedOperatingRooms(int occupiedOperatingRooms) { this.occupiedOperatingRooms = occupiedOperatingRooms; }

    public int getAmbulanceCapacity() { return ambulanceCapacity; }
    public void setAmbulanceCapacity(int ambulanceCapacity) { this.ambulanceCapacity = ambulanceCapacity; }

    public int getAvailableStaff() { return availableStaff; }
    public void setAvailableStaff(int availableStaff) { this.availableStaff = availableStaff; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    public int getAvailableEmergencyBeds() {
        return Math.max(0, emergencyBeds - occupiedEmergencyBeds);
    }

    public int getAvailableIcuBeds() {
        return Math.max(0, icuBeds - occupiedIcuBeds);
    }

    public int getAvailableVentilators() {
        return Math.max(0, ventilators - occupiedVentilators);
    }

    public void setContactPhone(String phone) { this.emergencyContact = phone; }
    public void setTotalBeds(int beds) { this.emergencyBeds = beds; }
    public void setAvailableEmergencyBeds(int beds) { this.emergencyBeds = beds + this.occupiedEmergencyBeds; }
    public void setAvailableIcuBeds(int beds) { this.icuBeds = beds + this.occupiedIcuBeds; }
    public void setAvailableVentilators(int vents) { this.ventilators = vents + this.occupiedVentilators; }
    public void setOxygenStockLevel(int level) {}
}
