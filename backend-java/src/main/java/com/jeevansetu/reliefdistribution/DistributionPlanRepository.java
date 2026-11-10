package com.jeevansetu.reliefdistribution;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DistributionPlanRepository extends MongoRepository<DistributionPlan, String> {
    List<DistributionPlan> findByStatus(String status);
    List<DistributionPlan> findByReliefCenterId(String reliefCenterId);
}
