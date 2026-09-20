package com.jeevansetu.repository;

import com.jeevansetu.model.Volunteer;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends MongoRepository<Volunteer, String> {
    Optional<Volunteer> findByPhone(String phone);
    List<Volunteer> findByAvailabilityStatus(String availabilityStatus);
    List<Volunteer> findByLocationNear(Point point, Distance distance);
    List<Volunteer> findBySkillsContaining(String skill);
}
