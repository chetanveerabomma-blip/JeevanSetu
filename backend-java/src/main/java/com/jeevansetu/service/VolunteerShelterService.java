package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.Shelter;
import com.jeevansetu.model.Volunteer;
import com.jeevansetu.repository.ShelterRepository;
import com.jeevansetu.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VolunteerShelterService {

    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final AuditLogService auditLogService;

    public VolunteerShelterService(VolunteerRepository volunteerRepository, ShelterRepository shelterRepository, AuditLogService auditLogService) {
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.auditLogService = auditLogService;
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public List<Volunteer> getAvailableVolunteers() {
        return volunteerRepository.findByAvailabilityStatus("AVAILABLE");
    }

    public Volunteer updateVolunteerStatus(String volunteerId, String status) {
        Volunteer v = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found: " + volunteerId));
        v.setAvailabilityStatus(status);
        return volunteerRepository.save(v);
    }

    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    public Shelter updateShelterOccupancy(String shelterId, int occupied, String actorEmail) {
        Shelter s = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelter not found: " + shelterId));
        s.setOccupiedCapacity(occupied);
        if (s.getAvailableCapacity() <= 0) {
            s.setStatus("FULL");
        } else {
            s.setStatus("OPEN");
        }
        s.setLastUpdated(Instant.now());

        Shelter saved = shelterRepository.save(s);
        auditLogService.logAction(
                actorEmail, "SHELTER", "SHELTER_OCCUPANCY_UPDATED",
                s.getId(), s.getName(),
                String.format("Occupancy updated to %d / %d", occupied, s.getTotalCapacity()),
                null, s.getStatus()
        );
        return saved;
    }
}
