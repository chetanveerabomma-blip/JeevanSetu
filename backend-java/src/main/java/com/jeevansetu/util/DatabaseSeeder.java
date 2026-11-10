package com.jeevansetu.util;

import com.jeevansetu.model.*;
import com.jeevansetu.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final ResponderUnitRepository responderUnitRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodBankRepository bloodBankRepository;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final EmergencyRepository emergencyRepository;
    private final VolunteerTaskRepository volunteerTaskRepository;
    private final DisasterEventRepository disasterRepository;
    private final ReliefCenterRepository reliefCenterRepository;
    private final ReliefRequestRepository reliefRequestRepository;
    private final InventoryItemRepository inventoryRepository;
    private final VehicleRecordRepository vehicleRepository;
    private final BloodDonorRepository bloodDonorRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final GeoZoneRepository geoZoneRepository;
    private final IncidentTimelineRepository timelineRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            UserRepository userRepository,
            ResponderUnitRepository responderUnitRepository,
            HospitalRepository hospitalRepository,
            BloodBankRepository bloodBankRepository,
            VolunteerRepository volunteerRepository,
            ShelterRepository shelterRepository,
            EmergencyRepository emergencyRepository,
            VolunteerTaskRepository volunteerTaskRepository,
            DisasterEventRepository disasterRepository,
            ReliefCenterRepository reliefCenterRepository,
            ReliefRequestRepository reliefRequestRepository,
            InventoryItemRepository inventoryRepository,
            VehicleRecordRepository vehicleRepository,
            BloodDonorRepository bloodDonorRepository,
            BloodRequestRepository bloodRequestRepository,
            GeoZoneRepository geoZoneRepository,
            IncidentTimelineRepository timelineRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.responderUnitRepository = responderUnitRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.emergencyRepository = emergencyRepository;
        this.volunteerTaskRepository = volunteerTaskRepository;
        this.disasterRepository = disasterRepository;
        this.reliefCenterRepository = reliefCenterRepository;
        this.reliefRequestRepository = reliefRequestRepository;
        this.inventoryRepository = inventoryRepository;
        this.vehicleRepository = vehicleRepository;
        this.bloodDonorRepository = bloodDonorRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.geoZoneRepository = geoZoneRepository;
        this.timelineRepository = timelineRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String defaultPass = passwordEncoder.encode("Demo@1234");

        // 1. Seed Core Users if empty
        if (userRepository.count() == 0) {
            seedCoreUsers(defaultPass);
        }

        // 2. Seed Responders if empty
        if (responderUnitRepository.count() == 0) {
            seedResponders(defaultPass);
        }

        // 3. Seed Hospitals if empty
        if (hospitalRepository.count() == 0) {
            seedHospitals(defaultPass);
        }

        // 4. Seed Blood Banks if empty
        if (bloodBankRepository.count() == 0) {
            seedBloodBanks(defaultPass);
        }

        // 5. Seed Volunteers if empty
        if (volunteerRepository.count() == 0) {
            seedVolunteers(defaultPass);
        }

        // 6. Seed Shelters if empty
        if (shelterRepository.count() == 0) {
            seedShelters();
        }

        // 7. Seed Emergencies & Timelines if empty
        if (emergencyRepository.count() == 0) {
            seedEmergencies();
        }

        // 8. Seed Disasters & Relief Centers
        if (disasterRepository.count() == 0) {
            seedDisasterModule();
        }

        // 9. Seed Inventory Items
        if (inventoryRepository.count() == 0) {
            seedInventory();
        }

        // 10. Seed Vehicle Records
        if (vehicleRepository.count() == 0) {
            seedVehicles();
        }

        // 11. Seed Blood Donors & Requests
        if (bloodDonorRepository.count() == 0) {
            seedBloodCoordination();
        }

        // 12. Seed Geo Zones
        if (geoZoneRepository.count() == 0) {
            seedGeoZones();
        }

        // 13. Seed Volunteer Tasks
        if (volunteerTaskRepository.count() == 0) {
            seedVolunteerTasks();
        }

        logger.info("JeevanSetu database validation and seeding check complete. All collections initialized!");
    }

    private void seedCoreUsers(String defaultPass) {
        User admin = new User("admin@jeevansetu.gov.in", "9876543210", defaultPass, Role.ADMIN, "Dr. Vikram Seth (System Admin)");
        admin.setOrganization("JeevanSetu Central Command");
        userRepository.save(admin);

        User ctrl = new User("command@jeevansetu.gov.in", "9876543211", defaultPass, Role.CONTROL_CENTER, "Officer Ananya Sharma (Tactical Dispatcher)");
        ctrl.setOrganization("State Emergency Management Center");
        userRepository.save(ctrl);

        User citizen = new User("citizen@demo.jeevansetu.in", "9876543212", defaultPass, Role.CITIZEN, "Aarav Patel (Citizen)");
        userRepository.save(citizen);

        User dispatcher = new User("dispatcher@jeevansetu.gov.in", "9876543213", defaultPass, Role.DISPATCHER, "Officer Rajesh Mehra");
        dispatcher.setOrganization("112 Integrated Dispatch Center");
        userRepository.save(dispatcher);

        User reliefMgr = new User("relief@jeevansetu.gov.in", "9876543214", defaultPass, Role.RELIEF_MANAGER, "Suresh Nambiar (Disaster Relief Director)");
        reliefMgr.setOrganization("National Disaster Response Coordination");
        userRepository.save(reliefMgr);
    }

    private void seedResponders(String defaultPass) {
        String[][] data = {
                {"AMB-101", "City Victoria ALS Ambulance", "AMBULANCE", "Advanced Life Support (ALS)", "Rajiv Kumar", "9811100101"},
                {"AMB-102", "Bowring BLS Ambulance", "AMBULANCE", "Basic Life Support (BLS)", "Sunil Gowda", "9811100102"},
                {"AMB-103", "NIMHANS Neuro-Critical Care Van", "AMBULANCE", "Critical Care & Ventilator", "Kavita Rao", "9811100103"},
                {"AMB-104", "Manipal Rapid Trauma Unit", "AMBULANCE", "Advanced Trauma & ALS", "Manjunath S", "9811100104"},
                {"AMB-105", "Apollo Pediatric Care Ambulance", "AMBULANCE", "Pediatric & Neonatal ICU", "Siddharth Verma", "9811100105"},
                {"POL-201", "Cubbon Park Quick Response Patrol", "POLICE", "High-Speed Interceptor", "Inspector Ramesh Babu", "9811100201"},
                {"POL-202", "MG Road Traffic & Escort Unit", "POLICE", "Crowd Control & Siren", "SI Suresh Kumar", "9811100202"},
                {"FIRE-301", "Central Station Heavy Rescue Tender", "FIRE_UNIT", "Heavy Foam & Hydraulic Cutters", "Commander Anthony Das", "9811100301"},
                {"FIRE-302", "Koramangala High-Rise Ladder Unit", "FIRE_UNIT", "Hydraulic Platform Ladder 52m", "Officer Bhaskar Hegde", "9811100302"}
        };

        for (int i = 0; i < data.length; i++) {
            ResponderUnit u = new ResponderUnit();
            u.setUnitCode(data[i][0]);
            u.setName(data[i][1]);
            u.setType(ResourceType.valueOf(data[i][2]));
            u.setStatus(i == 0 || i == 5 || i == 7 ? ResourceStatus.AVAILABLE : (i == 1 ? ResourceStatus.AVAILABLE : ResourceStatus.AVAILABLE));
            u.setEquipmentLevel(data[i][3]);
            u.setDriverName(data[i][4]);
            u.setContactPhone(data[i][5]);
            u.setLatitude(12.9716 + (i * 0.006 - 0.02));
            u.setLongitude(77.5946 + (i * 0.005 - 0.02));
            u.setAddress("Station Post #" + (i + 1) + ", Metro Sector");
            responderUnitRepository.save(u);
        }
    }

    private void seedHospitals(String defaultPass) {
        String[][] hosp = {
                {"Victoria Government Hospital", "Fort Road, Kalasipalya", "HOSP-1001", "080-26701150"},
                {"Bowring and Lady Curzon Hospital", "Shivajinagar Central", "HOSP-1002", "080-25591325"},
                {"NIMHANS Institute of Neurosciences", "Hosur Road", "HOSP-1003", "080-26995000"},
                {"Manipal Trauma & Multi-Specialty", "HAL Airport Road", "HOSP-1004", "080-25024444"},
                {"Apollo Super Specialty Hospital", "Bannerghatta Road", "HOSP-1005", "080-26304050"}
        };

        for (int i = 0; i < hosp.length; i++) {
            Hospital h = new Hospital();
            h.setName(hosp[i][0]);
            h.setAddress(hosp[i][1]);
            h.setRegistrationId(hosp[i][2]);
            h.setContactPhone(hosp[i][3]);
            h.setLatitude(12.9650 + (i * 0.012 - 0.02));
            h.setLongitude(77.5900 + (i * 0.010 - 0.02));
            h.setTotalBeds(300 + i * 50);
            h.setAvailableEmergencyBeds(20 + i * 5);
            h.setAvailableIcuBeds(8 + i * 2);
            h.setAvailableVentilators(4 + i);
            h.setOxygenStockLevel(95 - i * 3);
            hospitalRepository.save(h);
        }
    }

    private void seedBloodBanks(String defaultPass) {
        String[][] bbs = {
                {"Red Cross Central Blood Bank", "Race Course Road", "BB-501", "080-22264205"},
                {"Rotary Bangalore Blood Bank", "Chamarajpet", "BB-502", "080-26610088"},
                {"Victoria Hospital Blood Bank", "Kalasipalya", "BB-503", "080-26701155"}
        };
        for (int i = 0; i < bbs.length; i++) {
            BloodBank bb = new BloodBank();
            bb.setName(bbs[i][0]);
            bb.setAddress(bbs[i][1]);
            bb.setRegistrationId(bbs[i][2]);
            bb.setPhone(bbs[i][3]);
            bb.setLatitude(12.9750 + i * 0.005);
            bb.setLongitude(77.5850 + i * 0.008);
            bb.setStockUnits("A+", 24 + i * 5);
            bb.setStockUnits("A-", 8 + i * 2);
            bb.setStockUnits("B+", 32 + i * 4);
            bb.setStockUnits("B-", 6 + i);
            bb.setStockUnits("AB+", 14 + i * 3);
            bb.setStockUnits("AB-", 4 + i);
            bb.setStockUnits("O+", 45 + i * 6);
            bb.setStockUnits("O-", 10 + i * 2);
            bloodBankRepository.save(bb);
        }
    }

    private void seedVolunteers(String defaultPass) {
        String[] volNames = {
                "Gautam Gambhir", "Priya Nair", "Manish Pandey", "Varun Dhawan", "Kiran Mazumdar",
                "Deepak Hooda", "Rashmi Shukla", "Pranav Anand", "Anupama Sharma", "Tarun Bajaj"
        };
        String[] bloodGroups = {"O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB+", "O+", "B+"};

        for (int i = 0; i < volNames.length; i++) {
            Volunteer v = new Volunteer();
            v.setName(volNames[i]);
            v.setPhone("98900" + (60000 + i));
            v.setEmail(String.format("volunteer%d@demo.jeevansetu.in", i + 1));
            v.setAddress("Community Ward Sector " + (i + 1));
            v.setLatitude(12.9716 + (i * 0.005 - 0.025));
            v.setLongitude(77.5946 + (i * 0.004 - 0.02));
            v.setEmergencyRadiusKm(10.0);
            v.setAvailabilityStatus("AVAILABLE");
            v.setBloodGroup(bloodGroups[i]);
            v.setServiceArea("Central Urban District");
            v.setCompletedTasksCount(3 + i * 2);
            v.setRating(4.8);
            v.getSkills().add("First Aid");
            if (i % 2 == 0) v.getSkills().add("Driving");
            if (i % 3 == 0) v.getSkills().add("Rescue");
            if (i % 4 == 0) v.getSkills().add("Blood Donation");
            volunteerRepository.save(v);
        }
    }

    private void seedShelters() {
        for (int i = 0; i < 6; i++) {
            Shelter s = new Shelter();
            s.setName("Disaster Relief Center " + (char) ('A' + i));
            s.setAddress("Community Stadium Sector " + (i + 1));
            s.setLatitude(12.9716 + (i * 0.009 - 0.025));
            s.setLongitude(77.5946 - (i * 0.008 - 0.015));
            s.setTotalCapacity(400 + i * 50);
            s.setOccupiedCapacity(120 + i * 25);
            s.setStatus("OPEN");
            s.setContactPhone("080-2560" + (7000 + i));
            shelterRepository.save(s);
        }
    }

    private void seedEmergencies() {
        EmergencyType[] types = EmergencyType.values();
        EmergencyPriority[] priorities = EmergencyPriority.values();
        Instant now = Instant.now();

        for (int i = 0; i < 20; i++) {
            Emergency e = new Emergency();
            e.setIncidentCode(String.format("JS-2026-%06d", 100000 + i));
            EmergencyType t = types[i % types.length];
            EmergencyPriority p = priorities[i % priorities.length];
            e.setType(t);
            e.setPriority(p);
            e.setStatus(i < 8 ? EmergencyStatus.RESOLVED : (i < 14 ? EmergencyStatus.PROCESSING : EmergencyStatus.REPORTED));
            e.setReporterName("Citizen " + (i + 1));
            e.setReporterPhone("98100" + (10000 + i));
            e.setLatitude(12.9716 + ((i % 5) * 0.007 - 0.015));
            e.setLongitude(77.5946 + ((i % 4) * 0.006 - 0.012));
            e.setAddress("Location Marker #" + (i + 1) + ", Metro Region");
            e.setPeopleAffected(1 + (i % 5));
            e.setInjuredCount(i % 3);
            e.setUnconscious(p == EmergencyPriority.CRITICAL && i % 2 == 0);
            e.setSevereBleeding(p == EmergencyPriority.CRITICAL && i % 3 == 0);
            e.setDescription("Reported emergency event: " + t + " near city junction");
            e.setCreatedAt(now.minus((20 - i) * 3, ChronoUnit.HOURS));
            if (e.getStatus() == EmergencyStatus.RESOLVED) {
                e.setResolvedAt(e.getCreatedAt().plus(40, ChronoUnit.MINUTES));
            }
            Emergency saved = emergencyRepository.save(e);

            // Seed initial timeline
            IncidentTimelineEntry entry = new IncidentTimelineEntry(saved.getId(), saved.getIncidentCode(),
                    "REPORTED", "Incident reported by caller", "Citizen", "CITIZEN");
            timelineRepository.save(entry);
        }
    }

    private void seedDisasterModule() {
        DisasterEvent de = new DisasterEvent();
        de.setEventCode("DIS-2026-FLOOD-01");
        de.setTitle("Monsoon Urban Inundation & Flash Flood");
        de.setDisasterType("FLOOD");
        de.setSeverity("SEVERE");
        de.setDescription("Heavy continuous rainfall causing flash floods in low-lying lakeside basins.");
        de.setCenterLatitude(12.9600);
        de.setCenterLongitude(77.5800);
        de.setAffectedRadiusKm(6.5);
        de.setRegionName("South-Eastern Lake Belt");
        de.setStatus("ACTIVE");
        de.setCasualtyCount(0);
        de.setDisplacedCount(340);
        de.setReliefCentersCount(3);
        de.setActiveAgencies(List.of("NDRF", "SDRF", "FIRE_RESCUE", "CIVIL_DEFENSE"));
        DisasterEvent savedDis = disasterRepository.save(de);

        // Relief Centers for this disaster
        ReliefCenter rc1 = new ReliefCenter();
        rc1.setDisasterEventId(savedDis.getId());
        rc1.setName("Victoria Hall Flood Relief Camp");
        rc1.setAddress("Kalasipalya Grounds");
        rc1.setLatitude(12.9620);
        rc1.setLongitude(77.5810);
        rc1.setTotalCapacity(500);
        rc1.setCurrentOccupancy(180);
        rc1.setFoodSupplyDays(6);
        rc1.setWaterSupplyLiters(6000);
        rc1.setMedicalKitsAvailable(65);
        rc1.setStatus("OPERATIONAL");
        rc1.setContactPerson("R. K. Sharma (Camp Director)");
        rc1.setContactPhone("9845011223");
        reliefCenterRepository.save(rc1);

        ReliefCenter rc2 = new ReliefCenter();
        rc2.setDisasterEventId(savedDis.getId());
        rc2.setName("St. Joseph Community Emergency Shelter");
        rc2.setAddress("Residency Road Campus");
        rc2.setLatitude(12.9680);
        rc2.setLongitude(77.5950);
        rc2.setTotalCapacity(350);
        rc2.setCurrentOccupancy(110);
        rc2.setFoodSupplyDays(8);
        rc2.setWaterSupplyLiters(4500);
        rc2.setMedicalKitsAvailable(40);
        rc2.setStatus("OPERATIONAL");
        rc2.setContactPerson("Sister Mary (Coordinator)");
        rc2.setContactPhone("9845022334");
        reliefCenterRepository.save(rc2);

        // Relief Request
        ReliefRequest req = new ReliefRequest();
        req.setDisasterEventId(savedDis.getId());
        req.setReliefCenterId(rc1.getId());
        req.setReliefCenterName(rc1.getName());
        req.setRequestType("WATER");
        req.setQuantityRequested(2000);
        req.setQuantityDispatched(1000);
        req.setUrgency("HIGH");
        req.setStatus("IN_TRANSIT");
        req.setRequestedBy("Camp Logistics Team");
        req.setContactPhone("9845011223");
        req.setDestinationAddress(rc1.getAddress());
        reliefRequestRepository.save(req);
    }

    private void seedInventory() {
        Object[][] items = {
                {"INV-OX-001", "Medical Oxygen Cylinder 47L", "OXYGEN_CYLINDER", "Victoria Base Depot", 12.9650, 77.5900, 60, 48, 12, 10, "CYLINDERS", "EXCELLENT"},
                {"INV-MED-002", "Trauma First Aid Advanced Kit", "FIRST_AID_KIT", "Command Center Depot", 12.9716, 77.5946, 120, 105, 15, 20, "KITS", "EXCELLENT"},
                {"INV-EQ-003", "Portable Defibrillator AED Pro", "MEDICAL_EQUIPMENT", "Emergency Bay 1", 12.9716, 77.5946, 25, 22, 3, 5, "UNITS", "EXCELLENT"},
                {"INV-WAT-004", "Packaged Mineral Drinking Water 20L", "DRINKING_WATER", "Disaster Relief Depot-A", 12.9620, 77.5810, 500, 420, 80, 50, "BOTTLES", "GOOD"},
                {"INV-FOOD-005", "Emergency High-Calorie Ration Pack", "FOOD_SUPPLY", "Disaster Relief Depot-A", 12.9620, 77.5810, 1000, 850, 150, 100, "PACKS", "GOOD"},
                {"INV-FIRE-006", "Thermal Fire Resistant Blankets", "FIRE_SAFETY", "Central Fire Station", 12.9780, 77.5900, 80, 74, 6, 15, "BLANKETS", "EXCELLENT"}
        };

        for (Object[] row : items) {
            InventoryItem item = new InventoryItem();
            item.setItemCode((String) row[0]);
            item.setName((String) row[1]);
            item.setCategory((String) row[2]);
            item.setLocationName((String) row[3]);
            item.setLatitude((Double) row[4]);
            item.setLongitude((Double) row[5]);
            item.setTotalQuantity((Integer) row[6]);
            item.setAvailableQuantity((Integer) row[7]);
            item.setAllocatedQuantity((Integer) row[8]);
            item.setMinStockThreshold((Integer) row[9]);
            item.setUnit((String) row[10]);
            item.setCondition((String) row[11]);
            item.setLastMaintenanceDate(LocalDate.now().minusMonths(1));
            item.setNextMaintenanceDate(LocalDate.now().plusMonths(5));
            inventoryRepository.save(item);
        }
    }

    private void seedVehicles() {
        Object[][] vdata = {
                {"KA-01-EA-1081", "AMB-101", "AMBULANCE_ALS", "Force Traveller ALS", 2024, 14200, "ACTIVE", "Victoria Trauma Hospital", "Rajiv Kumar", "9811100101"},
                {"KA-01-EA-1082", "AMB-102", "AMBULANCE_BLS", "Tata Winger BLS", 2023, 28500, "ACTIVE", "Bowring Central Clinic", "Sunil Gowda", "9811100102"},
                {"KA-01-EA-1083", "AMB-103", "AMBULANCE_ALS", "Mercedes Sprinter ICU", 2024, 9800, "ACTIVE", "NIMHANS Trauma Care", "Kavita Rao", "9811100103"},
                {"KA-01-FIRE-301", "FIRE-301", "FIRE_TRUCK", "Ashok Leyland Foam Tender", 2022, 35000, "ACTIVE", "Central Fire Station", "Anthony Das", "9811100301"},
                {"KA-01-POL-201", "POL-201", "POLICE_PATROL", "Mahindra Scorpio Interceptor", 2023, 42000, "ACTIVE", "Cubbon Police Post", "Ramesh Babu", "9811100201"},
                {"KA-01-EA-1089", "AMB-109", "AMBULANCE_BLS", "Mahindra Bolero Ambulance", 2021, 62000, "MAINTENANCE", "Central Workshop Depot", "D. Naik", "9811100199"}
        };

        for (Object[] row : vdata) {
            VehicleRecord vr = new VehicleRecord();
            vr.setVehicleNumber((String) row[0]);
            vr.setVehicleCode((String) row[1]);
            vr.setCategory((String) row[2]);
            vr.setMakeModel((String) row[3]);
            vr.setYear((Integer) row[4]);
            vr.setOdometerKm((Integer) row[5]);
            vr.setMaintenanceStatus((String) row[6]);
            vr.setAssignedStationOrHospital((String) row[7]);
            vr.setAssignedDriverName((String) row[8]);
            vr.setDriverPhone((String) row[9]);
            vr.setLastServiceDate(LocalDate.now().minusMonths(2));
            vr.setNextServiceDate(LocalDate.now().plusMonths(4));
            vr.setInsuranceExpiryDate(LocalDate.now().plusMonths(9));
            vr.setFitnessCertificateExpiry(LocalDate.now().plusMonths(14));
            vr.setConditionReport("GOOD");
            vehicleRepository.save(vr);
        }
    }

    private void seedBloodCoordination() {
        Object[][] donors = {
                {"Sanjay Verma", "O+", "9876500111", "Central City", 12.9716, 77.5946, 5},
                {"Deepika Padukone", "A+", "9876500112", "Indiranagar", 12.9780, 77.6400, 3},
                {"Rahul Dravid", "O-", "9876500113", "Malleswaram", 12.9900, 77.5700, 12},
                {"Anita Deshmukh", "B+", "9876500114", "Koramangala", 12.9350, 77.6200, 4},
                {"Kunal Kapoor", "AB+", "9876500115", "Jayanagar", 12.9250, 77.5800, 2},
                {"Meera Nandan", "B-", "9876500116", "Whitefield", 12.9698, 77.7500, 6}
        };

        for (Object[] row : donors) {
            BloodDonor bd = new BloodDonor();
            bd.setFullName((String) row[0]);
            bd.setBloodGroup((String) row[1]);
            bd.setPhone((String) row[2]);
            bd.setCity((String) row[3]);
            bd.setLatitude((Double) row[4]);
            bd.setLongitude((Double) row[5]);
            bd.setTotalDonations((Integer) row[6]);
            bd.setAvailable(true);
            bd.setVerified(true);
            bd.setLastDonationDate(LocalDate.now().minusMonths(4));
            bloodDonorRepository.save(bd);
        }

        // Blood Request
        BloodRequest br = new BloodRequest();
        br.setRequestCode("REQ-BLD-0104");
        br.setHospitalName("Victoria Government Hospital");
        br.setHospitalAddress("Fort Road, Kalasipalya");
        br.setLatitude(12.9650);
        br.setLongitude(77.5900);
        br.setBloodGroup("O-");
        br.setUnitsRequired(3);
        br.setUnitsFulfilled(1);
        br.setUrgency("CRITICAL");
        br.setPatientName("Emergency Trauma Patient (Bed #04)");
        br.setContactPerson("Dr. Satish (ER Blood Officer)");
        br.setContactPhone("080-26701150");
        br.setStatus("MATCHED");
        bloodRequestRepository.save(br);
    }

    private void seedGeoZones() {
        GeoZone z1 = new GeoZone();
        z1.setZoneName("Monsoon Flood Basin Zone - Red Alert");
        z1.setZoneType("DANGER_ZONE");
        z1.setSeverity("CRITICAL");
        z1.setCenterLatitude(12.9600);
        z1.setCenterLongitude(77.5800);
        z1.setRadiusMeters(1500);
        z1.setDescription("Severe waterlogging and electrical hazard zone. Evacuation advised.");
        z1.setStatus("ACTIVE");
        z1.setCreatedBy("Command Tactical Desk");
        geoZoneRepository.save(z1);

        GeoZone z2 = new GeoZone();
        z2.setZoneName("City Central Safe Evacuation Buffer");
        z2.setZoneType("BUFFER_ZONE");
        z2.setSeverity("MODERATE");
        z2.setCenterLatitude(12.9716);
        z2.setCenterLongitude(77.5946);
        z2.setRadiusMeters(2500);
        z2.setDescription("Designated green corridor for emergency vehicle traffic routing.");
        z2.setStatus("ACTIVE");
        z2.setCreatedBy("Traffic Command Center");
        geoZoneRepository.save(z2);
    }

    private void seedVolunteerTasks() {
        VolunteerTask vt1 = new VolunteerTask();
        vt1.setTaskCode("TASK-VOL-01");
        vt1.setTitle("First-Aid Assistance at Victoria Relief Camp");
        vt1.setDescription("Support medical officers with dressing minor lacerations and triaging arriving evacuees.");
        vt1.setRequiredSkill("First Aid");
        vt1.setLocationAddress("Victoria Hall Flood Relief Camp");
        vt1.setLatitude(12.9620);
        vt1.setLongitude(77.5810);
        vt1.setStatus("APPROVED");
        vt1.setDispatcherApproved(true);
        vt1.setApprovedByDispatcher("Officer Ananya Sharma");
        volunteerTaskRepository.save(vt1);

        VolunteerTask vt2 = new VolunteerTask();
        vt2.setTaskCode("TASK-VOL-02");
        vt2.setTitle("Emergency Drinking Water Transport");
        vt2.setDescription("Drive light pickup with water cartons from Central Depot to Relief Camp-B.");
        vt2.setRequiredSkill("Driving");
        vt2.setLocationAddress("Central Depot to St. Joseph Campus");
        vt2.setLatitude(12.9680);
        vt2.setLongitude(77.5950);
        vt2.setStatus("PROPOSED");
        vt2.setDispatcherApproved(false);
        volunteerTaskRepository.save(vt2);
    }
}
