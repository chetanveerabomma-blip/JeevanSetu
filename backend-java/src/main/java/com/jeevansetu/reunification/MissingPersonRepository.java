package com.jeevansetu.reunification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MissingPersonRepository extends MongoRepository<MissingPersonReport, String> {
    List<MissingPersonReport> findByStatusNot(String status);
    List<MissingPersonReport> findByPersonNameContainingIgnoreCase(String name);
    List<MissingPersonReport> findByFamilyGroupId(String familyGroupId);
}
