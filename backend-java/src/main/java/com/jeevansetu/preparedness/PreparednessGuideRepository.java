package com.jeevansetu.preparedness;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PreparednessGuideRepository extends MongoRepository<PreparednessGuide, String> {
    List<PreparednessGuide> findByCategoryAndLanguage(String category, String language);
    List<PreparednessGuide> findByLanguage(String language);
    List<PreparednessGuide> findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCase(String q1, String q2);
}
