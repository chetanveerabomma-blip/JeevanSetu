package com.jeevansetu.repository;

import com.jeevansetu.model.BloodBank;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodBankRepository extends MongoRepository<BloodBank, String> {
    Optional<BloodBank> findByRegistrationId(String registrationId);
    List<BloodBank> findByLocationNear(Point point, Distance distance);
}
