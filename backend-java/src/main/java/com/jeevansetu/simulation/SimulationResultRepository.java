package com.jeevansetu.simulation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulationResultRepository extends MongoRepository<SimulationResult, String> {
    List<SimulationResult> findByScenarioId(String scenarioId);
}
