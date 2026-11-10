package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "blood_requests")
public class BloodRequest {
    @Id
    private String id;

    @Indexed(unique = true)
    private String requestCode;

    private String hospitalId;
    private String hospitalName;

    @Indexed
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-

    private int unitsRequired = 1;
    private int unitsFulfilled = 0;

    @Indexed
    private String urgency = "URGENT"; // CRITICAL, URGENT, ROUTINE

    private String patientName;
    private String contactPerson;
    private String contactPhone;
    private String hospitalAddress;
    private double latitude;
    private double longitude;

    @Indexed
    private String status = "PENDING"; // PENDING, MATCHED, FULFILLED, CANCELLED

    private Instant createdAt = Instant.now();
    private Instant fulfilledAt;

    public BloodRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequestCode() { return requestCode; }
    public void setRequestCode(String requestCode) { this.requestCode = requestCode; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsRequired() { return unitsRequired; }
    public void setUnitsRequired(int unitsRequired) { this.unitsRequired = unitsRequired; }

    public int getUnitsFulfilled() { return unitsFulfilled; }
    public void setUnitsFulfilled(int unitsFulfilled) { this.unitsFulfilled = unitsFulfilled; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getHospitalAddress() { return hospitalAddress; }
    public void setHospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getFulfilledAt() { return fulfilledAt; }
    public void setFulfilledAt(Instant fulfilledAt) { this.fulfilledAt = fulfilledAt; }
}
