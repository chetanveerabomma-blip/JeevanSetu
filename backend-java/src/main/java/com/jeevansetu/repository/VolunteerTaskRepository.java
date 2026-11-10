package com.jeevansetu.repository;

import com.jeevansetu.model.VolunteerTask;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerTaskRepository extends MongoRepository<VolunteerTask, String> {
    Optional<VolunteerTask> findByTaskCode(String taskCode);
    List<VolunteerTask> findByAssignedVolunteerId(String volunteerId, Sort sort);
    List<VolunteerTask> findByIncidentId(String incidentId);
    List<VolunteerTask> findByStatus(String status, Sort sort);
    List<VolunteerTask> findByDispatcherApprovedFalse(Sort sort);
    long countByStatus(String status);
}
