package com.jeevansetu.repository;

import com.jeevansetu.model.IncidentChatMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentChatMessageRepository extends MongoRepository<IncidentChatMessage, String> {
    List<IncidentChatMessage> findByIncidentId(String incidentId, Sort sort);
    List<IncidentChatMessage> findByIncidentIdAndTargetAgencyIn(String incidentId, List<String> targets, Sort sort);
    long countByIncidentIdAndIsReadFalse(String incidentId);
}
