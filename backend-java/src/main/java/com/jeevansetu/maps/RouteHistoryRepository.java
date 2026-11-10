package com.jeevansetu.maps;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteHistoryRepository extends MongoRepository<RouteHistory, String> {
    List<RouteHistory> findTop20ByOrderByCalculatedAtDesc();
}
