package com.jeevansetu.repository;

import com.jeevansetu.model.ResourceStatus;
import com.jeevansetu.model.ResourceType;
import com.jeevansetu.model.ResponderUnit;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponderUnitRepository extends MongoRepository<ResponderUnit, String> {
    Optional<ResponderUnit> findByUnitCode(String unitCode);
    List<ResponderUnit> findByType(ResourceType type);
    List<ResponderUnit> findByStatus(ResourceStatus status);
    List<ResponderUnit> findByTypeAndStatus(ResourceType type, ResourceStatus status);
    List<ResponderUnit> findByLocationNear(Point point, Distance distance);
    List<ResponderUnit> findByTypeAndStatusAndLocationNear(ResourceType type, ResourceStatus status, Point point, Distance distance);
    Optional<ResponderUnit> findByCurrentIncidentId(String incidentId);
    long countByTypeAndStatus(ResourceType type, ResourceStatus status);
    long countByStatus(ResourceStatus status);
}
