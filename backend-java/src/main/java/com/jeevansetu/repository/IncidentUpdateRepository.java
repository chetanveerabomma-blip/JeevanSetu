package com.jeevansetu.repository;

import com.jeevansetu.model.IncidentUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentUpdateRepository extends MongoRepository<IncidentUpdate, String> {
    List<IncidentUpdate> findByIncidentIdOrderByTimestampAsc(String incidentId);
    List<IncidentUpdate> findByIncidentCodeOrderByTimestampAsc(String incidentCode);
}
