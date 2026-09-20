package com.jeevansetu.repository;

import com.jeevansetu.model.Assignment;
import com.jeevansetu.model.ResourceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends MongoRepository<Assignment, String> {
    List<Assignment> findByIncidentId(String incidentId);
    List<Assignment> findByResourceId(String resourceId);
    List<Assignment> findByResourceIdAndStatusIn(String resourceId, List<ResourceStatus> statuses);
    Optional<Assignment> findByIncidentIdAndResourceId(String incidentId, String resourceId);
    List<Assignment> findByStatus(ResourceStatus status);
}
