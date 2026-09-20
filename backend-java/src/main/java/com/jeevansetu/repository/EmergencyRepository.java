package com.jeevansetu.repository;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.EmergencyType;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyRepository extends MongoRepository<Emergency, String> {
    Optional<Emergency> findByIncidentCode(String incidentCode);
    List<Emergency> findByStatus(EmergencyStatus status, Sort sort);
    List<Emergency> findByStatusIn(List<EmergencyStatus> statuses, Sort sort);
    List<Emergency> findByPriority(EmergencyPriority priority);
    List<Emergency> findByType(EmergencyType type);
    List<Emergency> findByReporterId(String reporterId, Sort sort);
    List<Emergency> findByLocationNear(Point point, Distance distance);
    long countByStatus(EmergencyStatus status);
    long countByPriority(EmergencyPriority priority);
    List<Emergency> findByCreatedAtBetween(Instant start, Instant end);
}
