package com.jeevansetu.repository;

import com.jeevansetu.model.BloodRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodRequestRepository extends MongoRepository<BloodRequest, String> {
    Optional<BloodRequest> findByRequestCode(String requestCode);
    List<BloodRequest> findByStatus(String status, Sort sort);
    List<BloodRequest> findByBloodGroup(String bloodGroup, Sort sort);
    long countByStatus(String status);
}
