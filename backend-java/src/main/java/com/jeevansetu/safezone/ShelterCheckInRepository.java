package com.jeevansetu.safezone;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterCheckInRepository extends MongoRepository<ShelterCheckIn, String> {
    List<ShelterCheckIn> findByShelterIdAndStatusIn(String shelterId, List<String> statuses);
    Optional<ShelterCheckIn> findByCheckInCode(String checkInCode);
    List<ShelterCheckIn> findByStatus(String status);
}
