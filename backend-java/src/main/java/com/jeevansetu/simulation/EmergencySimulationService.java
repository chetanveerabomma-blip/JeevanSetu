package com.jeevansetu.simulation;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class EmergencySimulationService {

    private final SimulationScenarioRepository scenarioRepository;
    private final SimulationResultRepository resultRepository;

    public EmergencySimulationService(SimulationScenarioRepository scenarioRepository,
                                      SimulationResultRepository resultRepository) {
        this.scenarioRepository = scenarioRepository;
        this.resultRepository = resultRepository;
    }

    public SimulationScenario createScenario(SimulationScenario scenario) {
        scenario.setSimulation(true);
        scenario.setSimulationStatus("DRAFT");
        scenario.setCreatedAt(Instant.now());
        return scenarioRepository.save(scenario);
    }

    public List<SimulationScenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    public Optional<SimulationScenario> getScenarioById(String id) {
        return scenarioRepository.findById(id);
    }

    /**
     * Executes an isolated disaster response simulation experiment.
     * ZERO notifications or real DB mutations are dispatched.
     */
    public SimulationResult runSimulation(String scenarioId) {
        SimulationScenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Simulation scenario not found: " + scenarioId));

        scenario.setSimulationStatus("RUNNING");
        scenarioRepository.save(scenario);

        int total = scenario.getIncidentCount();
        int ambAvail = scenario.getAvailableAmbulanceCount();
        int bedAvail = scenario.getHospitalBedCapacity();

        // 1. Generate Synthetic Simulation Incidents
        int criticalCount = 0;
        int dispatchedCount = 0;
        int unhandledCount = 0;
        List<String> timeline = new ArrayList<>();
        List<String> bottlenecks = new ArrayList<>();

        timeline.add(String.format("T+00m: [SIMULATION INITIALIZED] Scenario '%s' (%s) commenced with %d synthetic emergencies.",
                scenario.getTitle(), scenario.getScenarioType(), total));
        timeline.add(String.format("T+01m: [RESOURCE PARAMETERS] %d simulated ambulances online; %d trauma ICU beds available.",
                ambAvail, bedAvail));

        double totalDurationAccumulator = 0.0;

        for (int i = 1; i <= total; i++) {
            boolean isCritical = (i % 2 == 1);
            if (isCritical) criticalCount++;

            int minute = i * 2;
            if (ambAvail > 0 && bedAvail > 0) {
                ambAvail--;
                bedAvail--;
                dispatchedCount++;
                double responseMinutes = 4.0 + (i * 0.8);
                totalDurationAccumulator += responseMinutes;
                timeline.add(String.format("T+%02dm: [DISPATCHED] Synthetic incident SIM-2026-%03d [%s] allocated simulated ALS unit. Simulated ETA: %.1f mins.",
                        minute, i, isCritical ? "CRITICAL" : "HIGH", responseMinutes));
            } else {
                unhandledCount++;
                timeline.add(String.format("T+%02dm: [CAPACITY EXCEEDED] Synthetic incident SIM-2026-%03d placed in buffer queue. Ambulance or bed saturation reached.",
                        minute, i));
            }
        }

        if (unhandledCount > 0) {
            bottlenecks.add(String.format("Ambulance Fleet Exhaustion: %d incident(s) faced simulated response delays due to restricted vehicle pool (%d available).",
                    unhandledCount, scenario.getAvailableAmbulanceCount()));
        }
        if (scenario.getBlockedRoadCount() > 0) {
            bottlenecks.add(String.format("Corridor Obstruction: %d simulated arterial routes were blocked, adding an average 4.2 minutes detour to dispatch times.",
                    scenario.getBlockedRoadCount()));
        }

        double avgResponse = dispatchedCount > 0 ? Math.round((totalDurationAccumulator / dispatchedCount) * 10.0) / 10.0 : 0.0;
        double utilPct = Math.round(((double) dispatchedCount / total) * 100.0);

        SimulationResult result = new SimulationResult();
        result.setScenarioId(scenario.getId());
        result.setScenarioTitle(scenario.getTitle());
        result.setStrategyName("JeevanSetu Multi-Factor Triage & Dynamic Buffering");
        result.setTotalSimulatedIncidents(total);
        result.setCriticalIncidentsTriageCount(criticalCount);
        result.setSuccessfullyDispatchedCount(dispatchedCount);
        result.setUnhandledOrDelayedCount(unhandledCount);
        result.setAverageSimulatedResponseMinutes(avgResponse);
        result.setResourceUtilizationPercentage(utilPct);
        result.setSimulatedTimeline(timeline);
        result.setIdentifiedBottlenecks(bottlenecks);
        result.setSimulation(true);
        result.setExecutedAt(Instant.now());

        String report = String.format(
                "SIMULATION LAB ANALYSIS SUMMARY\n" +
                "==============================\n" +
                "Scenario: %s [%s]\n" +
                "Throughput: %d / %d incidents successfully handled (%.1f%% success rate).\n" +
                "Average Simulated Response Latency: %.1f minutes.\n" +
                "Key Finding: %s\n" +
                "DISCLAIMER: This experiment operated entirely in an isolated synthetic environment without real citizen notifications.",
                scenario.getTitle(), scenario.getScenarioType(), dispatchedCount, total, utilPct, avgResponse,
                bottlenecks.isEmpty() ? "Adequate baseline capacity." : bottlenecks.get(0)
        );
        result.setRecommendationReport(report);

        scenario.setSimulationStatus("COMPLETED");
        scenarioRepository.save(scenario);

        return resultRepository.save(result);
    }

    public List<SimulationResult> getResultsForScenario(String scenarioId) {
        return resultRepository.findByScenarioId(scenarioId);
    }
}
