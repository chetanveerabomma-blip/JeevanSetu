package com.jeevansetu.maps;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AmbulanceLocationRepository extends MongoRepository<AmbulanceLocation, String> {
    Optional<AmbulanceLocation> findByAmbulanceId(String ambulanceId);
    Optional<AmbulanceLocation> findByUnitCode(String unitCode);
    List<AmbulanceLocation> findByStatus(String status);
}
