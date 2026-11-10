package com.jeevansetu.maps;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<LocationRecord, String> {
    List<LocationRecord> findByEntityType(String entityType);
    List<LocationRecord> findByEntityId(String entityId);
}
