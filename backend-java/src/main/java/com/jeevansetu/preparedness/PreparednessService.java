package com.jeevansetu.preparedness;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PreparednessService {

    private final PreparednessGuideRepository guideRepository;
    private final EmergencyChecklistRepository checklistRepository;

    public PreparednessService(PreparednessGuideRepository guideRepository,
                               EmergencyChecklistRepository checklistRepository) {
        this.guideRepository = guideRepository;
        this.checklistRepository = checklistRepository;
    }

    @PostConstruct
    public void seedDefaultEducationalData() {
        if (guideRepository.count() == 0) {
            PreparednessGuide flood = new PreparednessGuide(
                    "FLOOD",
                    "Monsoon Flash Flood & Urban Waterlogging Safety",
                    "Standard protocols for electrical isolation, clean water preservation, and safe elevated evacuation.",
                    "en",
                    "National Disaster Management Authority (NDMA)"
            );
            flood.setKeyDos(Arrays.asList(
                    "Turn off main electrical breaker and gas cylinders before evacuating.",
                    "Drink only boiled, chlorination-treated, or packaged mineral water.",
                    "Keep emergency documents, medical prescriptions, and batteries in sealed waterproof pouches."
            ));
            flood.setKeyDonts(Arrays.asList(
                    "DO NOT walk, swim, or drive through moving flood waters (6 inches of rushing water can sweep you away).",
                    "DO NOT touch downed power lines or wade in water near submerged transformers."
            ));
            flood.setDetailedMarkdown("# NDMA Flood Action Plan\n\n1. Move to higher ground immediately.\n2. Tune in to state weather bulletins.\n3. Do not attempt crossing flooded culverts.\n4. Contact State Emergency Operations Centre (SEOC) at 1070.");
            guideRepository.save(flood);

            PreparednessGuide firstAid = new PreparednessGuide(
                    "FIRST_AID",
                    "Trauma First Aid & CPR Protocol",
                    "High-yield emergency life support actions for cardiac arrest, severe hemorrhage, and burn injuries.",
                    "en",
                    "Indian Red Cross Society & WHO"
            );
            firstAid.setKeyDos(Arrays.asList(
                    "Perform Hands-Only CPR: 100 to 120 rhythmic chest compressions per minute at center of chest.",
                    "Apply continuous firm direct pressure with clean cloth over bleeding wounds.",
                    "Cool chemical or heat burns with gentle running tap water for 10-15 minutes (no ice)."
            ));
            firstAid.setKeyDonts(Arrays.asList(
                    "DO NOT move an unconscious accident patient unless there is an imminent fire or explosion hazard (protect cervical spine).",
                    "DO NOT apply butter, toothpaste, or turmeric powder to open burn wounds."
            ));
            firstAid.setDetailedMarkdown("# First Aid Action Guide\n\nAlways call emergency services (112 / 108) first. Ensure scene safety before approaching patient.");
            guideRepository.save(firstAid);

            PreparednessGuide earthquake = new PreparednessGuide(
                    "EARTHQUAKE",
                    "Earthquake Survival: Drop, Cover, and Hold On",
                    "Immediate instinctive protection steps during seismic tremors in residential and high-rise structures.",
                    "en",
                    "National Disaster Management Authority (NDMA)"
            );
            earthquake.setKeyDos(Arrays.asList(
                    "DROP to hands and knees to prevent being knocked over.",
                    "COVER your head and neck beneath a sturdy table or desk.",
                    "HOLD ON until the shaking completely ceases."
            ));
            earthquake.setKeyDonts(Arrays.asList(
                    "DO NOT rush towards elevators or stairwells during active shaking.",
                    "DO NOT stand directly beside glass facades, display mirrors, or heavy hanging shelves."
            ));
            guideRepository.save(earthquake);
        }

        if (checklistRepository.count() == 0) {
            EmergencyChecklist homeKit = new EmergencyChecklist();
            homeKit.setTitle("72-Hour Household Disaster Emergency Survival Kit");
            homeKit.setTargetAudience("HOME");
            homeKit.setDescription("Essential life-support provisions recommended by NDMA for self-sustained survival during regional disruptions.");
            homeKit.setAuthoritativeSource("NDMA Emergency Supply Standards");
            homeKit.setItems(Arrays.asList(
                    new EmergencyChecklist.ChecklistItem("Packaged Drinking Water (3 Litres per person/day)", "12 Litres", true),
                    new EmergencyChecklist.ChecklistItem("Non-perishable energy foods (Biscuits, Nuts, Ready-to-eat meals)", "3-Day Supply", true),
                    new EmergencyChecklist.ChecklistItem("High-Intensity LED Flashlight with extra batteries", "2 Units", true),
                    new EmergencyChecklist.ChecklistItem("Comprehensive First Aid Kit (Antiseptic, Bandages, Paracetamol, ORS)", "1 Box", true),
                    new EmergencyChecklist.ChecklistItem("Hand-cranked or battery-powered AM/FM Emergency Radio", "1 Unit", true),
                    new EmergencyChecklist.ChecklistItem("Whistle (to signal location to rescue search teams)", "2 Units", true),
                    new EmergencyChecklist.ChecklistItem("Prescription Medications & Medical Records Copy", "7-Day Supply", true),
                    new EmergencyChecklist.ChecklistItem("Power Bank (fully charged 20000mAh) with charging cables", "1 Unit", true)
            ));
            checklistRepository.save(homeKit);
        }
    }

    public List<PreparednessGuide> getGuides(String category, String language, String query) {
        String lang = (language != null && !language.isEmpty()) ? language : "en";
        if (query != null && !query.trim().isEmpty()) {
            return guideRepository.findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCase(query.trim(), query.trim());
        }
        if (category != null && !category.isEmpty()) {
            return guideRepository.findByCategoryAndLanguage(category.toUpperCase(), lang);
        }
        return guideRepository.findByLanguage(lang);
    }

    public Optional<PreparednessGuide> getGuideById(String id) {
        return guideRepository.findById(id);
    }

    public List<EmergencyChecklist> getChecklists(String audience) {
        if (audience != null && !audience.isEmpty()) {
            return checklistRepository.findByTargetAudience(audience.toUpperCase());
        }
        return checklistRepository.findAll();
    }

    public String generateDownloadableChecklist(String id) {
        return checklistRepository.findById(id).map(c -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=====================================================\n");
            sb.append("JEEVANSETU EMERGENCY PREPAREDNESS CHECKLIST\n");
            sb.append(c.getTitle()).append("\n");
            sb.append("Authoritative Reference: ").append(c.getAuthoritativeSource()).append("\n");
            sb.append("=====================================================\n\n");
            sb.append(c.getDescription()).append("\n\n");
            sb.append("ESSENTIAL SUPPLY ITEMS:\n");
            for (EmergencyChecklist.ChecklistItem item : c.getItems()) {
                sb.append("[ ] ").append(item.getItemName())
                        .append(" - Qty: ").append(item.getQuantity())
                        .append(item.isEssential() ? " (MANDATORY)" : " (Recommended)")
                        .append("\n");
            }
            sb.append("\n=====================================================\n");
            sb.append("Emergency National Helpline: 112 | Ambulance: 108 | Disaster Control: 1070\n");
            return sb.toString();
        }).orElse("Checklist not found.");
    }
}
