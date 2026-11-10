package com.jeevansetu.postincident;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostIncidentReviewRepository extends MongoRepository<PostIncidentReview, String> {
    Optional<PostIncidentReview> findByIncidentId(String incidentId);
    Optional<PostIncidentReview> findByIncidentCode(String incidentCode);
}
