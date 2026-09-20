package com.jeevansetu.repository;

import com.jeevansetu.model.Shelter;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends MongoRepository<Shelter, String> {
    List<Shelter> findByStatus(String status);
    List<Shelter> findByLocationNear(Point point, Distance distance);
}
