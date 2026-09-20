package com.jeevansetu.dto;

public class UpdateHospitalCapacityRequest {
    private int emergencyBeds;
    private int occupiedEmergencyBeds;
    private int icuBeds;
    private int occupiedIcuBeds;
    private int ventilators;
    private int occupiedVentilators;
    private int operatingRooms;
    private int occupiedOperatingRooms;
    private String status;

    public UpdateHospitalCapacityRequest() {}

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
