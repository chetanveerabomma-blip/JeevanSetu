package com.jeevansetu.qrcode;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QrAccessLogRepository extends MongoRepository<QrAccessLog, String> {
    List<QrAccessLog> findByQrAccessKeyOrderByAccessedAtDesc(String qrAccessKey);
    List<QrAccessLog> findByProfileUserIdOrderByAccessedAtDesc(String userId);
}
