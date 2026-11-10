package com.jeevansetu.simulation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "simulation_results")
public class SimulationResult {
    @Id
    private String id;
    private String scenarioId;
    private String scenarioTitle;
    private String strategyName; // e.g. "Heuristic Multi-Factor Ranking" vs "Greedy Nearest First"
    private int totalSimulatedIncidents;
    private int criticalIncidentsTriageCount;
    private int successfullyDispatchedCount;
    private int unhandledOrDelayedCount;
    private double averageSimulatedResponseMinutes;
    private double resourceUtilizationPercentage;
    private List<String> simulatedTimeline = new ArrayList<>();
    private List<String> identifiedBottlenecks = new ArrayList<>();
    private String recommendationReport;
    private boolean isSimulation = true;
    private Instant executedAt = Instant.now();

    public SimulationResult() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String scenarioId) { this.scenarioId = scenarioId; }

    public String getScenarioTitle() { return scenarioTitle; }
    public void setScenarioTitle(String scenarioTitle) { this.scenarioTitle = scenarioTitle; }

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }

    public int getTotalSimulatedIncidents() { return totalSimulatedIncidents; }
    public void setTotalSimulatedIncidents(int totalSimulatedIncidents) { this.totalSimulatedIncidents = totalSimulatedIncidents; }

    public int getCriticalIncidentsTriageCount() { return criticalIncidentsTriageCount; }
    public void setCriticalIncidentsTriageCount(int criticalIncidentsTriageCount) { this.criticalIncidentsTriageCount = criticalIncidentsTriageCount; }

    public int getSuccessfullyDispatchedCount() { return successfullyDispatchedCount; }
    public void setSuccessfullyDispatchedCount(int successfullyDispatchedCount) { this.successfullyDispatchedCount = successfullyDispatchedCount; }

    public int getUnhandledOrDelayedCount() { return unhandledOrDelayedCount; }
    public void setUnhandledOrDelayedCount(int unhandledOrDelayedCount) { this.unhandledOrDelayedCount = unhandledOrDelayedCount; }

    public double getAverageSimulatedResponseMinutes() { return averageSimulatedResponseMinutes; }
    public void setAverageSimulatedResponseMinutes(double averageSimulatedResponseMinutes) { this.averageSimulatedResponseMinutes = averageSimulatedResponseMinutes; }

    public double getResourceUtilizationPercentage() { return resourceUtilizationPercentage; }
    public void setResourceUtilizationPercentage(double resourceUtilizationPercentage) { this.resourceUtilizationPercentage = resourceUtilizationPercentage; }

    public List<String> getSimulatedTimeline() { return simulatedTimeline; }
    public void setSimulatedTimeline(List<String> simulatedTimeline) { this.simulatedTimeline = simulatedTimeline; }

    public List<String> getIdentifiedBottlenecks() { return identifiedBottlenecks; }
    public void setIdentifiedBottlenecks(List<String> identifiedBottlenecks) { this.identifiedBottlenecks = identifiedBottlenecks; }

    public String getRecommendationReport() { return recommendationReport; }
    public void setRecommendationReport(String recommendationReport) { this.recommendationReport = recommendationReport; }

    public boolean isSimulation() { return isSimulation; }
    public void setSimulation(boolean simulation) { isSimulation = simulation; }

    public Instant getExecutedAt() { return executedAt; }
    public void setExecutedAt(Instant executedAt) { this.executedAt = executedAt; }
}
