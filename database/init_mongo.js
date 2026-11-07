/**
 * JeevanSetu - MongoDB Initialization & Geospatial Seed Script
 * Database: jeevansetu
 * Usage: mongosh jeevansetu database/init_mongo.js
 */

const dbName = "jeevansetu";
const db = db.getSiblingDB(dbName);

print("Initializing collections and indexes for database: " + dbName);

// 1. Create Collections with Geospatial & Triage Indexes
db.createCollection("users");
db.createCollection("emergencies");
db.createCollection("responders");
db.createCollection("hospitals");
db.createCollection("blood_banks");
db.createCollection("volunteers");
db.createCollection("shelters");
db.createCollection("assignments");
db.createCollection("incident_updates");
db.createCollection("agency_messages");
db.createCollection("chat_messages");
db.createCollection("audit_logs");
db.createCollection("notifications");
db.createCollection("analytics");

// Geospatial 2dsphere indexes
db.emergencies.createIndex({ "location": "2dsphere" });
db.responders.createIndex({ "location": "2dsphere" });
db.hospitals.createIndex({ "location": "2dsphere" });
db.blood_banks.createIndex({ "location": "2dsphere" });
db.volunteers.createIndex({ "location": "2dsphere" });
db.shelters.createIndex({ "location": "2dsphere" });

// Triage and query performance indexes
db.emergencies.createIndex({ "incidentCode": 1 }, { unique: true });
db.emergencies.createIndex({ "status": 1, "priority": 1 });
db.emergencies.createIndex({ "createdAt": -1 });
db.users.createIndex({ "email": 1 }, { unique: true });
db.responders.createIndex({ "unitCode": 1 }, { unique: true });
db.responders.createIndex({ "type": 1, "status": 1 });
db.agency_messages.createIndex({ "incidentId": 1, "timestamp": 1 });
db.chat_messages.createIndex({ "conversationId": 1, "timestamp": 1 });

print("Indexes successfully provisioned. Seeding reference records...");

// 2. Pre-seed Demonstration Data
const centralLat = 12.9716;
const centralLng = 77.5946;

// Pre-seed Hospitals
db.hospitals.deleteMany({});
db.hospitals.insertMany([
  {
    name: "Government Victoria Trauma Hospital",
    registrationId: "HOSP-1001",
    address: "Medical College Campus, Fort Road",
    latitude: centralLat + 0.007,
    longitude: centralLng - 0.008,
    emergencyBeds: 40,
    occupiedEmergencyBeds: 22,
    icuBeds: 18,
    occupiedIcuBeds: 11,
    ventilators: 12,
    occupiedVentilators: 7,
    operatingRooms: 6,
    occupiedOperatingRooms: 2,
    status: "AVAILABLE",
    emergencyContact: "080-22001001",
    specialities: ["TRAUMA_SURGERY", "NEUROLOGY", "BURNS_UNIT", "ICU"]
  },
  {
    name: "Apex Multi-Speciality Medical Center",
    registrationId: "HOSP-1002",
    address: "Central Ring Road, Zone 2",
    latitude: centralLat - 0.006,
    longitude: centralLng + 0.011,
    emergencyBeds: 35,
    occupiedEmergencyBeds: 18,
    icuBeds: 14,
    occupiedIcuBeds: 8,
    ventilators: 10,
    occupiedVentilators: 4,
    operatingRooms: 5,
    occupiedOperatingRooms: 3,
    status: "AVAILABLE",
    emergencyContact: "080-22001002",
    specialities: ["CARDIOLOGY", "ORTHOPEDIC", "TRAUMA_ICU"]
  },
  {
    name: "Fortis Emergency & Trauma Institute",
    registrationId: "HOSP-1003",
    address: "Bannerghatta Tech Corridor",
    latitude: centralLat - 0.018,
    longitude: centralLng - 0.005,
    emergencyBeds: 50,
    occupiedEmergencyBeds: 30,
    icuBeds: 20,
    occupiedIcuBeds: 13,
    ventilators: 15,
    occupiedVentilators: 8,
    operatingRooms: 8,
    occupiedOperatingRooms: 4,
    status: "AVAILABLE",
    emergencyContact: "080-22001003",
    specialities: ["ADVANCED_TRAUMA", "CARDIOLOGY", "PEDIATRICS"]
  }
]);

// Pre-seed Responder Units (Ambulance, Police PCR, Fire Tender)
db.responders.deleteMany({});
db.responders.insertMany([
  {
    unitCode: "AMB-104",
    name: "Metro Trauma EMS 104",
    type: "AMBULANCE",
    status: "AVAILABLE",
    latitude: centralLat + 0.008,
    longitude: centralLng - 0.007,
    driverName: "Rajesh Kumar",
    contactPhone: "98450-20104",
    capacity: 4,
    equipmentLevel: "Advanced Life Support (ALS) & Trauma Kit",
    organization: "National Ambulance Service"
  },
  {
    unitCode: "AMB-105",
    name: "City EMS 105",
    type: "AMBULANCE",
    status: "EN_ROUTE",
    latitude: centralLat + 0.010,
    longitude: centralLng + 0.005,
    driverName: "Sunil Gowda",
    contactPhone: "98450-20105",
    capacity: 2,
    equipmentLevel: "Basic Life Support (BLS)",
    organization: "National Ambulance Service"
  },
  {
    unitCode: "POL-201",
    name: "Highway Patrol PCR-1",
    type: "POLICE",
    status: "AVAILABLE",
    latitude: centralLat + 0.003,
    longitude: centralLng + 0.004,
    driverName: "Inspector Sharma",
    contactPhone: "98451-30201",
    capacity: 4,
    equipmentLevel: "Traffic Interceptor & Tactical First-Aid",
    organization: "Metro Police Department"
  },
  {
    unitCode: "FIRE-301",
    name: "Central Tender 301",
    type: "FIRE_UNIT",
    status: "AVAILABLE",
    latitude: centralLat - 0.009,
    longitude: centralLng + 0.008,
    driverName: "Officer Jadhav",
    contactPhone: "98452-40301",
    capacity: 6,
    equipmentLevel: "High-Pressure Water Tender & Hydraulic Cutters",
    organization: "Fire & Disaster Relief Services"
  }
]);

// Pre-seed Blood Banks
db.blood_banks.deleteMany({});
db.blood_banks.insertMany([
  {
    registrationId: "BB-2001",
    name: "Red Cross Central Blood Bank",
    address: "Red Cross Bhavan, Race Course Road",
    latitude: centralLat + 0.004,
    longitude: centralLng - 0.006,
    phone: "080-23405001",
    inventory: { "A+": 52, "A-": 18, "B+": 64, "B-": 14, "O+": 75, "O-": 10, "AB+": 32, "AB-": 8 },
    status: "OPERATIONAL"
  },
  {
    registrationId: "BB-2002",
    name: "Rotary Lifeline Blood Center",
    address: "Lavelle Road Sector 3",
    latitude: centralLat - 0.005,
    longitude: centralLng + 0.004,
    phone: "080-23405002",
    inventory: { "A+": 38, "A-": 12, "B+": 45, "B-": 9, "O+": 55, "O-": 6, "AB+": 20, "AB-": 4 },
    status: "OPERATIONAL"
  }
]);

print("MongoDB seed operations completed successfully for JeevanSetu platform.");
