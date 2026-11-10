package com.jeevansetu.maps;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "HospitalLocations")
public class HospitalLocation {
    @Id
    private String id;
    private String hospitalId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int icuBedsAvailable;
    private int emergencyBedsAvailable;
    private int ventilatorsAvailable;
    private String emergencyContactPhone;
    private boolean verified = true;
    private List<String> specializedServices = new ArrayList<>();
    private Instant lastUpdated = Instant.now();

    public HospitalLocation() {}

    public HospitalLocation(String hospitalId, String name, String address, double latitude, double longitude, int icuBeds, int erBeds) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.icuBedsAvailable = icuBeds;
        this.emergencyBedsAvailable = erBeds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getIcuBedsAvailable() { return icuBedsAvailable; }
    public void setIcuBedsAvailable(int icuBedsAvailable) { this.icuBedsAvailable = icuBedsAvailable; }

    public int getEmergencyBedsAvailable() { return emergencyBedsAvailable; }
    public void setEmergencyBedsAvailable(int emergencyBedsAvailable) { this.emergencyBedsAvailable = emergencyBedsAvailable; }

    public int getVentilatorsAvailable() { return ventilatorsAvailable; }
    public void setVentilatorsAvailable(int ventilatorsAvailable) { this.ventilatorsAvailable = ventilatorsAvailable; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public List<String> getSpecializedServices() { return specializedServices; }
    public void setSpecializedServices(List<String> specializedServices) { this.specializedServices = specializedServices; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
