package com.jeevansetu.repository;

import com.jeevansetu.model.DisasterEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisasterEventRepository extends MongoRepository<DisasterEvent, String> {
    Optional<DisasterEvent> findByEventCode(String eventCode);
    List<DisasterEvent> findByStatus(String status, Sort sort);
    List<DisasterEvent> findByDisasterType(String disasterType);
    long countByStatus(String status);
}
