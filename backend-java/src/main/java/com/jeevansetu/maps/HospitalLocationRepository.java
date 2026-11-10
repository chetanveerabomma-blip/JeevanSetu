package com.jeevansetu.maps;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HospitalLocationRepository extends MongoRepository<HospitalLocation, String> {
    Optional<HospitalLocation> findByHospitalId(String hospitalId);
}
