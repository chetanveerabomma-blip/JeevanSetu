package com.jeevansetu.repository;

import com.jeevansetu.model.ReliefCenter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReliefCenterRepository extends MongoRepository<ReliefCenter, String> {
    List<ReliefCenter> findByDisasterEventId(String disasterEventId);
    List<ReliefCenter> findByStatus(String status, Sort sort);
    long countByStatus(String status);
}
