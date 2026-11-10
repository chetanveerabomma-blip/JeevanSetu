package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "relief_centers")
public class ReliefCenter {
    @Id
    private String id;

    @Indexed
    private String disasterEventId;

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    private int totalCapacity = 500;
    private int currentOccupancy = 0;

    private int foodSupplyDays = 7;
    private int waterSupplyLiters = 5000;
    private int medicalKitsAvailable = 50;

    @Indexed
    private String status = "OPERATIONAL"; // OPERATIONAL, FULL, CLOSING, CLOSED

    private String contactPerson;
    private String contactPhone;
    private String notes;
    private Instant lastUpdated = Instant.now();

    public ReliefCenter() {}

    public int getAvailableCapacity() {
        return Math.max(0, totalCapacity - currentOccupancy);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisasterEventId() { return disasterEventId; }
    public void setDisasterEventId(String disasterEventId) { this.disasterEventId = disasterEventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public int getFoodSupplyDays() { return foodSupplyDays; }
    public void setFoodSupplyDays(int foodSupplyDays) { this.foodSupplyDays = foodSupplyDays; }

    public int getWaterSupplyLiters() { return waterSupplyLiters; }
    public void setWaterSupplyLiters(int waterSupplyLiters) { this.waterSupplyLiters = waterSupplyLiters; }

    public int getMedicalKitsAvailable() { return medicalKitsAvailable; }
    public void setMedicalKitsAvailable(int medicalKitsAvailable) { this.medicalKitsAvailable = medicalKitsAvailable; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
