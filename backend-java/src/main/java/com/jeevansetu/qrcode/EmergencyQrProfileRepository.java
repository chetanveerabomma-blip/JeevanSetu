package com.jeevansetu.qrcode;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmergencyQrProfileRepository extends MongoRepository<EmergencyQrProfile, String> {
    Optional<EmergencyQrProfile> findByUserId(String userId);
    Optional<EmergencyQrProfile> findByQrAccessKey(String qrAccessKey);
}
