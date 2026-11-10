package com.jeevansetu.publicsafety;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PublicSafetyReportRepository extends MongoRepository<PublicSafetyReport, String> {
    List<PublicSafetyReport> findByStatus(String status);
    List<PublicSafetyReport> findByCategory(String category);
    List<PublicSafetyReport> findByStatusIn(List<String> statuses);
    List<PublicSafetyReport> findTop50ByOrderByReportedAtDesc();
}
