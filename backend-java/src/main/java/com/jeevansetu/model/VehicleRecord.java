package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "vehicle_records")
public class VehicleRecord {
    @Id
    private String id;

    @Indexed(unique = true)
    private String vehicleNumber; // Plate number e.g. KA-01-EA-1081

    private String vehicleCode;
    private String category; // AMBULANCE_ALS, AMBULANCE_BLS, FIRE_TRUCK, POLICE_PATROL, RESCUE_VAN
    private String makeModel;
    private int year = 2024;

    private int odometerKm = 15000;

    @Indexed
    private String maintenanceStatus = "ACTIVE"; // ACTIVE, IN_SERVICE, MAINTENANCE, OUT_OF_SERVICE

    private String assignedStationOrHospital;
    private String assignedDriverName;
    private String driverPhone;

    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private LocalDate insuranceExpiryDate;
    private LocalDate fitnessCertificateExpiry;

    private String conditionReport = "GOOD"; // EXCELLENT, GOOD, FAIR, POOR
    private String activeIncidentId;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public VehicleRecord() {}

    public boolean isEligibleForDispatch() {
        return "ACTIVE".equalsIgnoreCase(maintenanceStatus) || "IN_SERVICE".equalsIgnoreCase(maintenanceStatus);
    }

    public boolean isDocumentExpiringSoon(int daysThreshold) {
        LocalDate now = LocalDate.now();
        if (insuranceExpiryDate != null && insuranceExpiryDate.isBefore(now.plusDays(daysThreshold))) return true;
        if (fitnessCertificateExpiry != null && fitnessCertificateExpiry.isBefore(now.plusDays(daysThreshold))) return true;
        if (nextServiceDate != null && nextServiceDate.isBefore(now.plusDays(daysThreshold))) return true;
        return false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleCode() { return vehicleCode; }
    public void setVehicleCode(String vehicleCode) { this.vehicleCode = vehicleCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMakeModel() { return makeModel; }
    public void setMakeModel(String makeModel) { this.makeModel = makeModel; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getOdometerKm() { return odometerKm; }
    public void setOdometerKm(int odometerKm) { this.odometerKm = odometerKm; }

    public String getMaintenanceStatus() { return maintenanceStatus; }
    public void setMaintenanceStatus(String maintenanceStatus) { this.maintenanceStatus = maintenanceStatus; }

    public String getAssignedStationOrHospital() { return assignedStationOrHospital; }
    public void setAssignedStationOrHospital(String assignedStationOrHospital) { this.assignedStationOrHospital = assignedStationOrHospital; }

    public String getAssignedDriverName() { return assignedDriverName; }
    public void setAssignedDriverName(String assignedDriverName) { this.assignedDriverName = assignedDriverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }

    public LocalDate getNextServiceDate() { return nextServiceDate; }
    public void setNextServiceDate(LocalDate nextServiceDate) { this.nextServiceDate = nextServiceDate; }

    public LocalDate getInsuranceExpiryDate() { return insuranceExpiryDate; }
    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) { this.insuranceExpiryDate = insuranceExpiryDate; }

    public LocalDate getFitnessCertificateExpiry() { return fitnessCertificateExpiry; }
    public void setFitnessCertificateExpiry(LocalDate fitnessCertificateExpiry) { this.fitnessCertificateExpiry = fitnessCertificateExpiry; }

    public String getConditionReport() { return conditionReport; }
    public void setConditionReport(String conditionReport) { this.conditionReport = conditionReport; }

    public String getActiveIncidentId() { return activeIncidentId; }
    public void setActiveIncidentId(String activeIncidentId) { this.activeIncidentId = activeIncidentId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
