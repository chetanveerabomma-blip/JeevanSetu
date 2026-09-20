package com.jeevansetu.repository;

import com.jeevansetu.model.Hospital;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    Optional<Hospital> findByRegistrationId(String registrationId);
    List<Hospital> findByStatus(String status);
    List<Hospital> findByLocationNear(Point point, Distance distance);
}
