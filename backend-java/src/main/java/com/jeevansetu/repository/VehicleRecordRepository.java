package com.jeevansetu.repository;

import com.jeevansetu.model.VehicleRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRecordRepository extends MongoRepository<VehicleRecord, String> {
    Optional<VehicleRecord> findByVehicleNumber(String vehicleNumber);
    List<VehicleRecord> findByMaintenanceStatus(String maintenanceStatus, Sort sort);
    List<VehicleRecord> findByCategory(String category, Sort sort);
    long countByMaintenanceStatus(String maintenanceStatus);
}
