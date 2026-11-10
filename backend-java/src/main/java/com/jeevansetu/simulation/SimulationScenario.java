package com.jeevansetu.simulation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "simulation_scenarios")
public class SimulationScenario {
    @Id
    private String id;
    private String title;
    private String scenarioType; // FLOOD, FIRE, ACCIDENT, EARTHQUAKE, MULTI_HAZARD, AMBULANCE_SHORTAGE, HOSPITAL_CAPACITY_CRUNCH
    private String description;
    private int incidentCount = 5;
    private int availableAmbulanceCount = 2; // Simulated shortage
    private int hospitalBedCapacity = 10;
    private int blockedRoadCount = 2;
    private double epicenterLatitude = 12.9716;
    private double epicenterLongitude = 77.5946;
    private String simulationStatus = "DRAFT"; // DRAFT, RUNNING, COMPLETED
    private boolean isSimulation = true; // STRICT ISOLATION FLAG
    private String createdBy;
    private Instant createdAt = Instant.now();

    public SimulationScenario() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getScenarioType() { return scenarioType; }
    public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIncidentCount() { return incidentCount; }
    public void setIncidentCount(int incidentCount) { this.incidentCount = incidentCount; }

    public int getAvailableAmbulanceCount() { return availableAmbulanceCount; }
    public void setAvailableAmbulanceCount(int availableAmbulanceCount) { this.availableAmbulanceCount = availableAmbulanceCount; }

    public int getHospitalBedCapacity() { return hospitalBedCapacity; }
    public void setHospitalBedCapacity(int hospitalBedCapacity) { this.hospitalBedCapacity = hospitalBedCapacity; }

    public int getBlockedRoadCount() { return blockedRoadCount; }
    public void setBlockedRoadCount(int blockedRoadCount) { this.blockedRoadCount = blockedRoadCount; }

    public double getEpicenterLatitude() { return epicenterLatitude; }
    public void setEpicenterLatitude(double epicenterLatitude) { this.epicenterLatitude = epicenterLatitude; }

    public double getEpicenterLongitude() { return epicenterLongitude; }
    public void setEpicenterLongitude(double epicenterLongitude) { this.epicenterLongitude = epicenterLongitude; }

    public String getSimulationStatus() { return simulationStatus; }
    public void setSimulationStatus(String simulationStatus) { this.simulationStatus = simulationStatus; }

    public boolean isSimulation() { return isSimulation; }
    public void setSimulation(boolean simulation) { isSimulation = simulation; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
