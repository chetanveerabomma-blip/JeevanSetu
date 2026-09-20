package com.jeevansetu.util;

import com.jeevansetu.model.*;
import com.jeevansetu.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            UserRepository userRepository,
            ResponderUnitRepository responderUnitRepository,
            HospitalRepository hospitalRepository,
            BloodBankRepository bloodBankRepository,
            VolunteerRepository volunteerRepository,
            ShelterRepository shelterRepository,
            EmergencyRepository emergencyRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.responderUnitRepository = responderUnitRepository;
        this.hospitalRepository = hospitalRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
        this.emergencyRepository = emergencyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded with demo records ({} users found). Skipping initialization.", userRepository.count());
            return;
        }

        logger.info("Initializing JeevanSetu demo database with comprehensive realistic dataset...");

        String defaultPass = passwordEncoder.encode("Demo@1234");

        // 1. Command Center & Admin Accounts
        User admin = new User("admin@jeevansetu.gov.in", "9876543210", defaultPass, Role.ADMIN, "Dr. Vikram Seth (System Admin)");
        admin.setOrganization("State Disaster Management Authority");
        userRepository.save(admin);

        User controlCenter = new User("command@jeevansetu.gov.in", "9876543211", defaultPass, Role.CONTROL_CENTER, "Officer Ananya Sharma");
        controlCenter.setOrganization("Integrated Emergency Operations Center");
        userRepository.save(controlCenter);

        // 2. Seed 20 Citizens
        String[] citizenNames = {
                "Aarav Patel", "Diya Sengupta", "Rohan Mehta", "Pooja Verma", "Karthik Iyer",
                "Sneha Nair", "Aditya Joshi", "Anjali Deshmukh", "Nikhil Reddy", "Meera Pillai",
                "Vikram Chauhan", "Tanvi Bhat", "Arjun Saxena", "Ritu Kulkarni", "Devansh Roy",
                "Shreya Das", "Siddharth Rao", "Kavya Menon", "Harsh Vardhan", "Deepa Nambiar"
        };
        for (int i = 0; i < citizenNames.length; i++) {
            String email = String.format("citizen%d@demo.jeevansetu.in", i + 1);
            String phone = String.format("98100%05d", 10000 + i);
            User c = new User(email, phone, defaultPass, Role.CITIZEN, citizenNames[i]);
            c.setCity("Metro Central");
            c.setEmergencyContact("98200" + (10000 + i));
            userRepository.save(c);
        }

        // 3. Seed 10 Ambulances
        String[] ambDrivers = {
                "Rajesh Kumar", "Sunil Gowda", "Pradeep Yadav", "Imran Khan", "Santosh Patil",
                "Mahesh Shinde", "Anand Swamy", "Dinesh Verma", "Kishore Babu", "Girish Hegde"
        };
        for (int i = 0; i < 10; i++) {
            String unitCode = String.format("AMB-%03d", 101 + i);
            ResponderUnit amb = new ResponderUnit();
            amb.setUnitCode(unitCode);
            amb.setName("Metro EMS " + unitCode);
            amb.setType(ResourceType.AMBULANCE);
            amb.setStatus(i == 0 ? ResourceStatus.AVAILABLE : (i % 3 == 0 ? ResourceStatus.AVAILABLE : ResourceStatus.AVAILABLE));
            amb.setDriverName(ambDrivers[i]);
            amb.setContactPhone("98450" + (20000 + i));
            amb.setCapacity(i % 2 == 0 ? 4 : 2);
            amb.setEquipmentLevel(i % 3 == 0 ? "Advanced Life Support (ALS) & Trauma Kit" : "Basic Life Support (BLS) & Defibrillator");
            amb.setOrganization("National Ambulance Service 108");
            amb.setLatitude(12.9716 + (i * 0.008 - 0.03));
            amb.setLongitude(77.5946 + (i * 0.007 - 0.025));
            amb.setAddress("EMS Depot Sector " + (i + 1));
            responderUnitRepository.save(amb);

            // Create Responder User account for primary ambulance
            if (i == 0) {
                User ambUser = new User("ambulance@jeevansetu.gov.in", amb.getContactPhone(), defaultPass, Role.AMBULANCE, amb.getDriverName());
                ambUser.setUnitId(amb.getId());
                ambUser.setOrganization(amb.getOrganization());
                userRepository.save(ambUser);
            }
        }

        // 4. Seed 10 Police Units
        for (int i = 0; i < 10; i++) {
            String unitCode = String.format("POL-%03d", 201 + i);
            ResponderUnit pol = new ResponderUnit();
            pol.setUnitCode(unitCode);
            pol.setName("City Police Patrol " + unitCode);
            pol.setType(ResourceType.POLICE);
            pol.setStatus(ResourceStatus.AVAILABLE);
            pol.setDriverName("Inspector Sharma " + (i + 1));
            pol.setContactPhone("98451" + (30000 + i));
            pol.setCapacity(4);
            pol.setEquipmentLevel("Tactical Response & Crowd Control");
            pol.setOrganization("State Police Emergency Wing");
            pol.setLatitude(12.9716 + (i * 0.006 - 0.02));
            pol.setLongitude(77.5946 - (i * 0.006 - 0.02));
            pol.setAddress("Police Beat Division " + (i + 1));
            responderUnitRepository.save(pol);

            if (i == 0) {
                User polUser = new User("police@jeevansetu.gov.in", pol.getContactPhone(), defaultPass, Role.POLICE, "Sub-Inspector Sharma");
                polUser.setUnitId(pol.getId());
                polUser.setOrganization(pol.getOrganization());
                userRepository.save(polUser);
            }
        }

        // 5. Seed 8 Fire Units
        for (int i = 0; i < 8; i++) {
            String unitCode = String.format("FIRE-%03d", 301 + i);
            ResponderUnit fire = new ResponderUnit();
            fire.setUnitCode(unitCode);
            fire.setName("Fire & Rescue Tender " + unitCode);
            fire.setType(ResourceType.FIRE_UNIT);
            fire.setStatus(ResourceStatus.AVAILABLE);
            fire.setDriverName("Fire Officer Jadhav " + (i + 1));
            fire.setContactPhone("98452" + (40000 + i));
            fire.setCapacity(6);
            fire.setEquipmentLevel(i % 2 == 0 ? "Heavy Hydraulic Rescue & Hazmat Foam" : "Standard Water Tender & Ladder");
            fire.setOrganization("Fire & Emergency Disaster Response");
            fire.setLatitude(12.9716 + (i * 0.009 - 0.035));
            fire.setLongitude(77.5946 + (i * 0.005 - 0.015));
            fire.setAddress("Fire Brigade Station " + (i + 1));
            responderUnitRepository.save(fire);

            if (i == 0) {
                User fireUser = new User("fire@jeevansetu.gov.in", fire.getContactPhone(), defaultPass, Role.FIRE, "Chief Fire Officer Jadhav");
                fireUser.setUnitId(fire.getId());
                fireUser.setOrganization(fire.getOrganization());
                userRepository.save(fireUser);
            }
        }

        // 6. Seed 10 Hospitals
        String[] hospitalNames = {
                "Government Victoria Trauma Hospital", "Apex Multi-Speciality Medical Center",
                "Fortis Emergency Care & Institute", "Apollo Super-Speciality Hospital",
                "Manipal Comprehensive Hospital", "St. John's Medical College & Hospital",
                "Bowring & Lady Curzon Hospital", "Narayana Health Emergency Center",
                "Columbia Asia Trauma Care", "Sparsh Orthopaedic & Trauma Center"
        };
        for (int i = 0; i < 10; i++) {
            Hospital h = new Hospital();
            h.setName(hospitalNames[i]);
            h.setRegistrationId(String.format("HOSP-REG-%04d", 1001 + i));
            h.setAddress("Medical Enclave Zone " + (i + 1) + ", Metro City");
            h.setEmergencyContact("080-2200" + (1000 + i));
            h.setLatitude(12.9716 + (i * 0.007 - 0.025));
            h.setLongitude(77.5946 + (i * 0.006 - 0.02));
            h.setEmergencyBeds(30 + i * 5);
            h.setOccupiedEmergencyBeds(15 + i * 2);
            h.setIcuBeds(12 + i * 2);
            h.setOccupiedIcuBeds(6 + i);
            h.setVentilators(8 + i);
            h.setOccupiedVentilators(3 + i / 2);
            h.setOperatingRooms(6);
            h.setOccupiedOperatingRooms(2);
            h.setAmbulanceCapacity(5);
            h.setAvailableStaff(40 + i * 5);
            h.setStatus("AVAILABLE");
            hospitalRepository.save(h);

            if (i == 0) {
                User hospUser = new User("hospital@jeevansetu.gov.in", h.getEmergencyContact(), defaultPass, Role.HOSPITAL, "Dr. Sunita Rao (ER Director)");
                hospUser.setRegistrationId(h.getRegistrationId());
                hospUser.setOrganization(h.getName());
                userRepository.save(hospUser);
            }
        }

        // 7. Seed 5 Blood Banks
        String[] bloodBankNames = {
                "Red Cross Central Blood Bank", "Rotary Blood Bank & Research Center",
                "Lions Metropolitan Blood Center", "City Lifeline Transfusion Bank", "Metro Community Blood Center"
        };
        for (int i = 0; i < 5; i++) {
            BloodBank bb = new BloodBank();
            bb.setName(bloodBankNames[i]);
            bb.setRegistrationId(String.format("BB-REG-%04d", 2001 + i));
            bb.setAddress("Blood Services Complex Road " + (i + 1));
            bb.setPhone("080-2340" + (5000 + i));
            bb.setEmail("contact@" + bb.getName().toLowerCase().replaceAll("[^a-z]", "") + ".org");
            bb.setLatitude(12.9716 + (i * 0.008 - 0.015));
            bb.setLongitude(77.5946 - (i * 0.007 - 0.015));
            Map<String, Integer> stock = new HashMap<>();
            stock.put("A+", 45 + i * 5);
            stock.put("A-", 15 + i * 2);
            stock.put("B+", 50 + i * 4);
            stock.put("B-", 18 + i * 3);
            stock.put("O+", 60 + i * 6);
            stock.put("O-", 12 + i * 2);
            stock.put("AB+", 30 + i * 3);
            stock.put("AB-", 8 + i);
            bb.setInventory(stock);
            bloodBankRepository.save(bb);

            if (i == 0) {
                User bbUser = new User("bloodbank@jeevansetu.gov.in", bb.getPhone(), defaultPass, Role.BLOOD_BANK, "Dr. Amit Roy (Blood Bank Manager)");
                bbUser.setRegistrationId(bb.getRegistrationId());
                bbUser.setOrganization(bb.getName());
                userRepository.save(bbUser);
            }
        }

        // 8. Seed 20 Volunteers
        String[] volNames = {
                "Gautam Gambhir", "Priya Nair", "Manish Pandey", "Varun Dhawan", "Kiran Mazumdar",
                "Deepak Hooda", "Rashmi Shukla", "Pranav Anand", "Anupama Parameswaran", "Tarun Bajaj",
                "Neha Kakkar", "Alok Nath", "Pankaj Tripathi", "Radhika Apte", "Manoj Bajpayee",
                "Swara Bhasker", "Ayushmann Khurrana", "Bhumika Chawla", "Rajkummar Rao", "Taapsee Pannu"
        };
        for (int i = 0; i < 20; i++) {
            Volunteer v = new Volunteer();
            v.setName(volNames[i]);
            v.setPhone("98900" + (60000 + i));
            v.setEmail(String.format("volunteer%d@demo.jeevansetu.in", i + 1));
            v.setAddress("Community Ward Sector " + (i + 1));
            v.setLatitude(12.9716 + (i * 0.005 - 0.04));
            v.setLongitude(77.5946 + (i * 0.004 - 0.035));
            v.setEmergencyRadiusKm(8.0 + (i % 5));
            v.setAvailabilityStatus("AVAILABLE");
            v.getSkills().add("First Aid");
            if (i % 2 == 0) v.getSkills().add("Driving");
            if (i % 3 == 0) v.getSkills().add("Rescue");
            if (i % 4 == 0) v.getSkills().add("Blood Donation");
            volunteerRepository.save(v);

            if (i == 0) {
                User volUser = new User("volunteer@jeevansetu.gov.in", v.getPhone(), defaultPass, Role.VOLUNTEER, v.getName());
                volUser.setOrganization("Civil Defense Volunteer Corps");
                userRepository.save(volUser);
            }
        }

        // 9. Seed 8 Shelters
        for (int i = 0; i < 8; i++) {
            Shelter s = new Shelter();
            s.setName("Disaster Relief Shelter Center " + (char) ('A' + i));
            s.setAddress("Community Stadium Sector " + (i + 1));
            s.setLatitude(12.9716 + (i * 0.009 - 0.03));
            s.setLongitude(77.5946 - (i * 0.008 - 0.02));
            s.setTotalCapacity(400 + i * 50);
            s.setOccupiedCapacity(150 + i * 30);
            s.setAccessibilityNotes("Wheelchair ramp, backup generator, emergency food supply kitchen");
            s.setStatus("OPEN");
            s.setContactPhone("080-2560" + (7000 + i));
            shelterRepository.save(s);
        }

        // 10. Seed 30 Historical & Sample Emergencies
        EmergencyType[] types = EmergencyType.values();
        EmergencyPriority[] priorities = EmergencyPriority.values();
        Instant now = Instant.now();

        for (int i = 0; i < 30; i++) {
            Emergency e = new Emergency();
            e.setIncidentCode(String.format("JS-2026-%06d", 100000 + i));
            EmergencyType t = types[i % types.length];
            EmergencyPriority p = priorities[i % priorities.length];
            e.setType(t);
            e.setPriority(p);
            e.setStatus(i < 10 ? EmergencyStatus.RESOLVED : (i < 20 ? EmergencyStatus.RESOLVED : EmergencyStatus.PROCESSING));
            e.setReporterName("Citizen " + (i + 1));
            e.setReporterPhone("98100" + (10000 + i));
            e.setLatitude(12.9716 + ((i % 7) * 0.008 - 0.025));
            e.setLongitude(77.5946 + ((i % 5) * 0.007 - 0.018));
            e.setAddress("Location Marker #" + (i + 1) + ", Metro Region");
            e.setPeopleAffected(1 + (i % 6));
            e.setInjuredCount(i % 4);
            e.setUnconscious(p == EmergencyPriority.CRITICAL && i % 2 == 0);
            e.setSevereBleeding(p == EmergencyPriority.CRITICAL && i % 3 == 0);
            e.setFireInvolved(t == EmergencyType.FIRE || t == EmergencyType.BUILDING_COLLAPSE);
            e.setDescription("Simulated emergency event: " + t + " with priority " + p);
            e.setCreatedAt(now.minus((30 - i) * 6, ChronoUnit.HOURS));
            if (e.getStatus() == EmergencyStatus.RESOLVED) {
                e.setResolvedAt(e.getCreatedAt().plus(45, ChronoUnit.MINUTES));
            }
            emergencyRepository.save(e);
        }

        logger.info("JeevanSetu demo database successfully seeded with all 18 required collections and realistic entities!");
    }
}
