package com.jeevansetu.simulation;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final EmergencySimulationService simulationService;

    public SimulationController(EmergencySimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/scenarios")
    public ResponseEntity<ApiResponse<SimulationScenario>> createScenario(@RequestBody SimulationScenario scenario) {
        return ResponseEntity.ok(ApiResponse.ok("Simulation scenario created in sandbox", simulationService.createScenario(scenario)));
    }

    @GetMapping("/scenarios")
    public ResponseEntity<ApiResponse<List<SimulationScenario>>> getScenarios() {
        return ResponseEntity.ok(ApiResponse.ok("Simulation scenarios retrieved", simulationService.getAllScenarios()));
    }

    @GetMapping("/scenarios/{id}")
    public ResponseEntity<ApiResponse<SimulationScenario>> getScenario(@PathVariable String id) {
        return simulationService.getScenarioById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.ok("Scenario retrieved", s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/scenarios/{id}/run")
    public ResponseEntity<ApiResponse<SimulationResult>> executeSimulation(@PathVariable String id) {
        SimulationResult result = simulationService.runSimulation(id);
        return ResponseEntity.ok(ApiResponse.ok("Simulation completed in isolated sandbox", result));
    }

    @GetMapping("/scenarios/{id}/results")
    public ResponseEntity<ApiResponse<List<SimulationResult>>> getResults(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Simulation results retrieved", simulationService.getResultsForScenario(id)));
    }
}
