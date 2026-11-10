package com.jeevansetu.reunification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SafeArrivalRepository extends MongoRepository<SafeArrivalConfirmation, String> {
    List<SafeArrivalConfirmation> findByFamilyGroupId(String familyGroupId);
    List<SafeArrivalConfirmation> findTop50ByOrderByArrivalTimeDesc();
}
