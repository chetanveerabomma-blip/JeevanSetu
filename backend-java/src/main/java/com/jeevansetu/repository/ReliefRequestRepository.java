package com.jeevansetu.repository;

import com.jeevansetu.model.ReliefRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReliefRequestRepository extends MongoRepository<ReliefRequest, String> {
    List<ReliefRequest> findByDisasterEventId(String disasterEventId, Sort sort);
    List<ReliefRequest> findByStatus(String status, Sort sort);
    long countByStatus(String status);
}
