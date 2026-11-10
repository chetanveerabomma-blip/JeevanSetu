package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.Volunteer;
import com.jeevansetu.repository.VolunteerRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final AuditLogService auditLogService;

    public VolunteerService(VolunteerRepository volunteerRepository, AuditLogService auditLogService) {
        this.volunteerRepository = volunteerRepository;
        this.auditLogService = auditLogService;
    }

    public Volunteer registerVolunteer(Volunteer volunteer) {
        if (volunteer.getPhone() != null && volunteerRepository.findByPhone(volunteer.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Volunteer already registered with phone: " + volunteer.getPhone());
        }
        if (volunteer.getAvailabilityStatus() == null) {
            volunteer.setAvailabilityStatus("AVAILABLE");
        }
        Volunteer saved = volunteerRepository.save(volunteer);
        auditLogService.log("VOLUNTEER_REGISTER", saved.getId(), "VOLUNTEER", "Volunteer registered: " + saved.getName());
        return saved;
    }

    public Volunteer getVolunteerById(String id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found: " + id));
    }

    public Optional<Volunteer> getVolunteerByUserId(String userId) {
        return volunteerRepository.findByUserId(userId);
    }

    public List<Volunteer> getAllVolunteers(boolean maskSensitiveData) {
        List<Volunteer> list = volunteerRepository.findAll();
        if (maskSensitiveData) {
            return list.stream().map(this::maskVolunteer).collect(Collectors.toList());
        }
        return list;
    }

    public List<Volunteer> getAvailableVolunteers(String skill, Double lat, Double lng, Double radiusKm) {
        List<Volunteer> volunteers;
        if (lat != null && lng != null && radiusKm != null) {
            volunteers = volunteerRepository.findByLocationNear(new Point(lng, lat), new Distance(radiusKm, Metrics.KILOMETERS));
        } else {
            volunteers = volunteerRepository.findByAvailabilityStatus("AVAILABLE");
        }

        return volunteers.stream()
                .filter(v -> "AVAILABLE".equalsIgnoreCase(v.getAvailabilityStatus()))
                .filter(v -> skill == null || skill.isBlank() || (v.getSkills() != null && v.getSkills().stream().anyMatch(s -> s.equalsIgnoreCase(skill))))
                .map(this::maskVolunteer)
                .collect(Collectors.toList());
    }

    public Volunteer updateProfile(String id, Volunteer updated) {
        Volunteer existing = getVolunteerById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getLatitude() != 0) existing.setLatitude(updated.getLatitude());
        if (updated.getLongitude() != 0) existing.setLongitude(updated.getLongitude());
        if (updated.getSkills() != null) existing.setSkills(updated.getSkills());
        if (updated.getServiceArea() != null) existing.setServiceArea(updated.getServiceArea());
        if (updated.getBloodGroup() != null) existing.setBloodGroup(updated.getBloodGroup());
        if (updated.getEmergencyRadiusKm() > 0) existing.setEmergencyRadiusKm(updated.getEmergencyRadiusKm());

        return volunteerRepository.save(existing);
    }

    public Volunteer updateStatus(String id, String status) {
        Volunteer existing = getVolunteerById(id);
        existing.setAvailabilityStatus(status);
        return volunteerRepository.save(existing);
    }

    public Map<String, Object> getVolunteerOverview() {
        long total = volunteerRepository.count();
        long available = volunteerRepository.countByAvailabilityStatus("AVAILABLE");
        long engaged = volunteerRepository.countByAvailabilityStatus("ENGAGED");

        return Map.of(
                "totalVolunteers", total,
                "availableVolunteers", available,
                "engagedVolunteers", engaged
        );
    }

    public Volunteer maskVolunteer(Volunteer v) {
        Volunteer copy = new Volunteer();
        copy.setId(v.getId());
        copy.setName(v.getName());
        copy.setAddress(v.getAddress());
        copy.setLatitude(v.getLatitude());
        copy.setLongitude(v.getLongitude());
        copy.setSkills(v.getSkills());
        copy.setAvailabilityStatus(v.getAvailabilityStatus());
        copy.setBloodGroup(v.getBloodGroup());
        copy.setServiceArea(v.getServiceArea());
        copy.setCompletedTasksCount(v.getCompletedTasksCount());
        copy.setRating(v.getRating());
        copy.setEmergencyRadiusKm(v.getEmergencyRadiusKm());
        copy.setRegisteredAt(v.getRegisteredAt());

        // Mask phone and email
        if (v.getPhone() != null && v.getPhone().length() >= 6) {
            copy.setPhone(v.getPhone().substring(0, 3) + "****" + v.getPhone().substring(v.getPhone().length() - 2));
        } else {
            copy.setPhone("****");
        }
        if (v.getEmail() != null && v.getEmail().contains("@")) {
            String[] parts = v.getEmail().split("@");
            copy.setEmail(parts[0].substring(0, Math.min(2, parts[0].length())) + "***@" + parts[1]);
        }

        return copy;
    }
}
