package com.jeevansetu.repository;

import com.jeevansetu.model.AgencyMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencyMessageRepository extends MongoRepository<AgencyMessage, String> {
    List<AgencyMessage> findByIncidentId(String incidentId, Sort sort);
    List<AgencyMessage> findByIncidentCode(String incidentCode, Sort sort);
}
