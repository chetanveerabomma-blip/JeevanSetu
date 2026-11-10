package com.jeevansetu.preparedness;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmergencyChecklistRepository extends MongoRepository<EmergencyChecklist, String> {
    List<EmergencyChecklist> findByTargetAudience(String audience);
}
