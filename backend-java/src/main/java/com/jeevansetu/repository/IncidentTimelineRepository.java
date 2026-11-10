package com.jeevansetu.repository;

import com.jeevansetu.model.IncidentTimelineEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentTimelineRepository extends MongoRepository<IncidentTimelineEntry, String> {
    List<IncidentTimelineEntry> findByIncidentId(String incidentId, Sort sort);
    List<IncidentTimelineEntry> findByIncidentCode(String incidentCode, Sort sort);
}
