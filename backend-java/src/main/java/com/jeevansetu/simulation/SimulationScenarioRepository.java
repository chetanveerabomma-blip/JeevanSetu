package com.jeevansetu.simulation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulationScenarioRepository extends MongoRepository<SimulationScenario, String> {
    List<SimulationScenario> findBySimulationStatus(String status);
}
