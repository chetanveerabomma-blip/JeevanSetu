package com.jeevansetu.reliefdistribution;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplyDistributionRepository extends MongoRepository<SupplyDistributionRecord, String> {
    List<SupplyDistributionRecord> findByPlanId(String planId);
    List<SupplyDistributionRecord> findByReliefCenterId(String reliefCenterId);
}
