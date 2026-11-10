package com.jeevansetu.repository;

import com.jeevansetu.model.GeoZone;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoZoneRepository extends MongoRepository<GeoZone, String> {
    List<GeoZone> findByStatus(String status, Sort sort);
    List<GeoZone> findByActiveIncidentId(String activeIncidentId);
    List<GeoZone> findByActiveDisasterId(String activeDisasterId);
}
