package com.jeevansetu.evacuation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SafeZoneRepository extends MongoRepository<SafeZone, String> {
    List<SafeZone> findByActiveTrue();
    List<SafeZone> findByZoneType(String zoneType);
}
