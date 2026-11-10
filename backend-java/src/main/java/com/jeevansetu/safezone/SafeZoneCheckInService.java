package com.jeevansetu.safezone;

import com.jeevansetu.evacuation.SafeZoneService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class SafeZoneCheckInService {

    private final ShelterCheckInRepository checkInRepository;
    private final SafeZoneService safeZoneService;

    public SafeZoneCheckInService(ShelterCheckInRepository checkInRepository,
                                  SafeZoneService safeZoneService) {
        this.checkInRepository = checkInRepository;
        this.safeZoneService = safeZoneService;
    }

    public ShelterCheckIn checkInPerson(ShelterCheckIn checkIn) {
        checkIn.setCheckInCode("CHK-" + (1000 + new Random().nextInt(9000)));
        checkIn.setStatus("ARRIVED");
        checkIn.setArrivalTime(Instant.now());
        if (checkIn.getVerifiedByIdMethod() == null) {
            checkIn.setVerifiedByIdMethod("UNVERIFIED_SELF_REPORT");
            checkIn.setVerifiedIdentity(false);
        }

        ShelterCheckIn saved = checkInRepository.save(checkIn);
        // Increment shelter occupancy
        if (checkIn.getShelterId() != null) {
            safeZoneService.updateOccupancy(checkIn.getShelterId(), checkIn.getFamilyMembersCount());
        }
        return saved;
    }

    public Optional<ShelterCheckIn> confirmSafeStatus(String checkInCode) {
        return checkInRepository.findByCheckInCode(checkInCode).map(c -> {
            c.setStatus("SAFE");
            return checkInRepository.save(c);
        });
    }

    public Optional<ShelterCheckIn> checkOutPerson(String id) {
        return checkInRepository.findById(id).map(c -> {
            c.setStatus("DEPARTED");
            c.setDepartureTime(Instant.now());
            // Decrement shelter occupancy
            if (c.getShelterId() != null) {
                safeZoneService.updateOccupancy(c.getShelterId(), -c.getFamilyMembersCount());
            }
            return checkInRepository.save(c);
        });
    }

    public Map<String, Object> getShelterOccupancyActivity(String shelterId) {
        List<ShelterCheckIn> active = checkInRepository.findByShelterIdAndStatusIn(shelterId, Arrays.asList("ARRIVED", "SAFE"));
        int currentHeadcount = active.stream().mapToInt(ShelterCheckIn::getFamilyMembersCount).sum();

        Map<String, Object> result = new HashMap<>();
        result.put("shelterId", shelterId);
        result.put("activeCheckInsCount", active.size());
        result.put("totalPersonsSheltered", currentHeadcount);
        result.put("recentResidents", active);
        return result;
    }

    public List<ShelterCheckIn> getUnresolvedCheckIns() {
        // Records that are still marked ARRIVED without safe confirmation or departure after 24h
        return checkInRepository.findByStatus("ARRIVED");
    }
}
