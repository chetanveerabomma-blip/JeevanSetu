package com.jeevansetu.maps;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmergencyZoneRepository extends MongoRepository<EmergencyZone, String> {
    List<EmergencyZone> findByActiveTrue();
    List<EmergencyZone> findByZoneType(String zoneType);
}
