package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "responders")
public class ResponderUnit {
    @Id
    private String id;

    @Indexed(unique = true)
    private String unitCode; // e.g. "AMB-104", "POL-201", "FIRE-301"

    private String name;

    @Indexed
    private ResourceType type; // AMBULANCE, POLICE, FIRE_UNIT

    @Indexed
    private ResourceStatus status = ResourceStatus.AVAILABLE;

    private double latitude;
    private double longitude;
    private String address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String currentIncidentId;
    private String driverName;
    private String contactPhone;
    private String equipmentLevel; // e.g. "Advanced Life Support (ALS)", "Basic Life Support (BLS)", "Trauma", "Hazmat"
    private int capacity;
    private String organization;
    private int completedTrips = 0;
    private Instant lastUpdated = Instant.now();

    public ResponderUnit() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUnitCode() { return unitCode; }
    public void setUnitCode(String unitCode) { this.unitCode = unitCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

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

    public String getCurrentIncidentId() { return currentIncidentId; }
    public void setCurrentIncidentId(String currentIncidentId) { this.currentIncidentId = currentIncidentId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getEquipmentLevel() { return equipmentLevel; }
    public void setEquipmentLevel(String equipmentLevel) { this.equipmentLevel = equipmentLevel; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public int getCompletedTrips() { return completedTrips; }
    public void setCompletedTrips(int completedTrips) { this.completedTrips = completedTrips; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
