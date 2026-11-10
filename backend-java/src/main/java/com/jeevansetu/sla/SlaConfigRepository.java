package com.jeevansetu.sla;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SlaConfigRepository extends MongoRepository<SlaConfig, String> {
    Optional<SlaConfig> findByPriority(String priority);
}
