package com.jeevansetu.evacuation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvacuationPlanRepository extends MongoRepository<EvacuationPlan, String> {
    List<EvacuationPlan> findByActiveTrueOrderByGeneratedAtDesc();
}
