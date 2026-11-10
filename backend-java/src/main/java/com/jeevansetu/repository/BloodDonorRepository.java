package com.jeevansetu.repository;

import com.jeevansetu.model.BloodDonor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonorRepository extends MongoRepository<BloodDonor, String> {
    Optional<BloodDonor> findByPhone(String phone);
    List<BloodDonor> findByBloodGroupAndIsAvailableTrue(String bloodGroup);
    List<BloodDonor> findByIsAvailableTrue(Sort sort);
    long countByBloodGroup(String bloodGroup);
    long countByIsAvailableTrue();
}
