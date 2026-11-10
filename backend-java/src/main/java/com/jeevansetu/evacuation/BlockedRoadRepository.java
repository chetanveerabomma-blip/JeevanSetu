package com.jeevansetu.evacuation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlockedRoadRepository extends MongoRepository<BlockedRoad, String> {
    List<BlockedRoad> findByActiveTrue();
}
