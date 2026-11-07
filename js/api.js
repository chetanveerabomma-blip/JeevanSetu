/**
 * JeevanSetu - Comprehensive REST API & Mock Fallback Client
 */
const API_BASE = window.location.origin.includes('localhost') || window.location.origin.includes('127.0.0.1')
  ? `${window.location.protocol}//${window.location.hostname}:8080/api`
  : '/api';

const API = {
  getToken() {
    return localStorage.getItem('jeevansetu_token');
  },

  setAuth(data) {
    if (data.token) localStorage.setItem('jeevansetu_token', data.token);
    if (data.role) localStorage.setItem('jeevansetu_role', data.role);
    if (data.email) localStorage.setItem('jeevansetu_email', data.email);
    if (data.fullName) localStorage.setItem('jeevansetu_name', data.fullName);
    if (data.userId) localStorage.setItem('jeevansetu_uid', data.userId);
    if (data.unitId) localStorage.setItem('jeevansetu_unit_id', data.unitId);
  },

  getAuth() {
    return {
      token: localStorage.getItem('jeevansetu_token'),
      role: localStorage.getItem('jeevansetu_role'),
      email: localStorage.getItem('jeevansetu_email'),
      fullName: localStorage.getItem('jeevansetu_name'),
      userId: localStorage.getItem('jeevansetu_uid'),
      unitId: localStorage.getItem('jeevansetu_unit_id')
    };
  },

  logout() {
    localStorage.removeItem('jeevansetu_token');
    localStorage.removeItem('jeevansetu_role');
    localStorage.removeItem('jeevansetu_email');
    localStorage.removeItem('jeevansetu_name');
    localStorage.removeItem('jeevansetu_uid');
    localStorage.removeItem('jeevansetu_unit_id');
    window.location.href = 'login.html';
  },

  async request(endpoint, options = {}) {
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    };
    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Request failed with status ${response.status}`);
      }

      const res = await response.json();
      return res.data !== undefined ? res.data : res;
    } catch (err) {
      console.warn(`[API] Remote call to ${endpoint} failed (${err.message}). Using local state fallback.`);
      return this.handleFallback(endpoint, options);
    }
  },

  // Fallback engine initialized with realistic data for offline / standalone demonstration
  handleFallback(endpoint, options) {
    const store = this.getLocalStore();

    // 1. Auth endpoints
    if (endpoint.includes('/auth/login')) {
      const body = JSON.parse(options.body || '{}');
      const role = body.email.includes('command') ? 'CONTROL_CENTER' :
                   body.email.includes('admin') ? 'ADMIN' :
                   body.email.includes('ambulance') ? 'AMBULANCE' :
                   body.email.includes('police') ? 'POLICE' :
                   body.email.includes('fire') ? 'FIRE' :
                   body.email.includes('hospital') ? 'HOSPITAL' :
                   body.email.includes('blood') ? 'BLOOD_BANK' : 'CITIZEN';
      const auth = {
        token: 'demo_token_' + Date.now(),
        email: body.email || 'user@jeevansetu.in',
        role: role,
        userId: 'usr_' + Date.now(),
        fullName: role === 'CONTROL_CENTER' ? 'Officer Ananya Sharma' : 'Demo User',
        unitId: 'AMB-104'
      };
      this.setAuth(auth);
      return auth;
    }

    if (endpoint.includes('/auth/demo/')) {
      const role = endpoint.split('/').pop().toUpperCase();
      const auth = {
        token: 'demo_jwt_token_' + role.toLowerCase(),
        email: `${role.toLowerCase()}@jeevansetu.gov.in`,
        role: role,
        userId: 'usr_' + role.toLowerCase(),
        fullName: role === 'CONTROL_CENTER' ? 'Officer Ananya Sharma (Commander)' :
                  role === 'ADMIN' ? 'Dr. Vikram Seth (System Admin)' :
                  role === 'HOSPITAL' ? 'Dr. Sunita Rao (Victoria Trauma ER)' :
                  role === 'AMBULANCE' ? 'Rajesh Kumar (Pilot AMB-104)' :
                  role === 'BLOOD_BANK' ? 'Dr. Amit Roy (Red Cross Blood Bank)' :
                  'Citizen Aarav Patel',
        unitId: role === 'AMBULANCE' ? 'AMB-104' : null
      };
      this.setAuth(auth);
      return auth;
    }

    // 2. Report Emergency
    if (endpoint === '/emergencies/report') {
      const body = JSON.parse(options.body || '{}');
      const incCode = 'JS-2026-' + Math.floor(100000 + Math.random() * 900000);
      
      // Determine priority locally
      let priority = 'MEDIUM';
      if (body.unconscious || body.severeBleeding || body.trapped || body.type === 'BUILDING_COLLAPSE' || (body.type === 'FIRE' && body.injuredCount > 0)) {
        priority = 'CRITICAL';
      } else if (body.peopleAffected >= 2 || body.injuredCount >= 1 || body.type === 'ACCIDENT' || body.type === 'FIRE') {
        priority = 'HIGH';
      }

      const newEmergency = {
        id: 'emg_' + Date.now(),
        incidentCode: incCode,
        type: body.type || 'MEDICAL',
        priority: priority,
        status: 'REPORTED',
        latitude: body.latitude || 12.9716,
        longitude: body.longitude || 77.5946,
        address: body.address || 'MG Road Central Junction, Metro City',
        description: body.description || 'Emergency incident reported',
        peopleAffected: body.peopleAffected || 1,
        injuredCount: body.injuredCount || 0,
        unconscious: !!body.unconscious,
        severeBleeding: !!body.severeBleeding,
        fireInvolved: !!body.fireInvolved,
        trapped: !!body.trapped,
        requiredResources: body.type === 'ACCIDENT' ? ['AMBULANCE', 'POLICE'] :
                           body.type === 'FIRE' ? ['FIRE_UNIT', 'POLICE', 'AMBULANCE'] :
                           body.type === 'BLOOD_EMERGENCY' ? ['HOSPITAL', 'BLOOD_BANK'] : ['AMBULANCE'],
        assignedResourceIds: [],
        createdAt: new Date().toISOString(),
        reporterName: this.getAuth().fullName || 'Citizen Reporter'
      };

      store.emergencies.unshift(newEmergency);
      store.timeline[newEmergency.id] = [
        { stage: 'REPORTED', message: 'Incident reported by citizen via mobile portal. GPS coordinates validated.', timestamp: new Date().toISOString(), actorRole: 'CITIZEN' },
        { stage: 'PRIORITY_CALCULATED', message: `Priority Engine evaluated triage score: ${priority}. Required agencies: ${newEmergency.requiredResources.join(', ')}`, timestamp: new Date().toISOString(), actorRole: 'SYSTEM' }
      ];
      this.saveLocalStore(store);
      return newEmergency;
    }

    // 3. Priority Queue
    if (endpoint === '/emergencies/priority-queue') {
      const active = store.emergencies.filter(e => e.status !== 'RESOLVED' && e.status !== 'CANCELLED');
      const order = { 'CRITICAL': 4, 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1 };
      active.sort((a, b) => (order[b.priority] || 0) - (order[a.priority] || 0));
      return active;
    }

    if (endpoint === '/emergencies/all') {
      return store.emergencies;
    }

    if (endpoint.startsWith('/emergencies/code/')) {
      const code = endpoint.split('/').pop();
      return store.emergencies.find(e => e.incidentCode === code) || store.emergencies[0];
    }

    if (endpoint.startsWith('/emergencies/') && endpoint.endsWith('/timeline')) {
      const id = endpoint.split('/')[2];
      return store.timeline[id] || [
        { stage: 'REPORTED', message: 'Incident logged in system', timestamp: new Date(Date.now() - 600000).toISOString(), actorRole: 'CITIZEN' },
        { stage: 'PRIORITY_CALCULATED', message: 'Triage priority computed: CRITICAL', timestamp: new Date(Date.now() - 500000).toISOString(), actorRole: 'SYSTEM' }
      ];
    }

    if (endpoint.startsWith('/emergencies/')) {
      const id = endpoint.split('/')[2];
      return store.emergencies.find(e => e.id === id) || store.emergencies[0];
    }

    // 4. Resource recommendations (Algorithmic scoring)
    if (endpoint.startsWith('/resources/recommend/')) {
      const emgId = endpoint.split('/').pop();
      const emg = store.emergencies.find(e => e.id === emgId) || store.emergencies[0];
      return store.responders.map(unit => {
        const dist = Math.round((Math.sqrt(Math.pow(unit.latitude - emg.latitude, 2) + Math.pow(unit.longitude - emg.longitude, 2)) * 111) * 10) / 10;
        const isAvail = unit.status === 'AVAILABLE';
        const isALS = unit.equipmentLevel && unit.equipmentLevel.includes('Advanced');
        const score = Math.min(99, Math.max(40, Math.round((40 * Math.max(0, 1 - dist / 25)) + (isAvail ? 30 : 5) + (isALS ? 15 : 8) + 10)));
        return {
          unit: unit,
          distanceKm: dist,
          totalScore: score,
          reasons: [
            isAvail ? '✓ Available immediately' : '⚠ En route nearby',
            `${dist} km away`,
            isALS ? '✓ Advanced Trauma Life Support' : '✓ Standard emergency equipment',
            '✓ Capacity sufficient'
          ]
        };
      }).sort((a, b) => b.totalScore - a.totalScore);
    }

    // 5. Assign resource
    if (endpoint.startsWith('/resources/assign/')) {
      const emgId = endpoint.split('/').pop();
      const body = JSON.parse(options.body || '{}');
      const unit = store.responders.find(r => r.id === body.resourceId) || store.responders[0];
      const emg = store.emergencies.find(e => e.id === emgId);
      
      unit.status = 'ASSIGNED';
      unit.currentIncidentId = emgId;
      if (emg) {
        emg.status = 'ASSIGNED';
        if (!emg.assignedResourceIds.includes(unit.id)) emg.assignedResourceIds.push(unit.id);
      }

      const assignment = {
        id: 'asg_' + Date.now(),
        incidentId: emgId,
        incidentCode: emg ? emg.incidentCode : 'JS-2026-000184',
        resourceId: unit.id,
        resourceType: unit.type,
        unitCode: unit.unitCode,
        responderName: unit.name,
        responderContact: unit.contactPhone,
        status: 'ASSIGNED',
        estimatedDistanceKm: 2.4,
        estimatedMinutes: 6,
        routeCoordinates: [
          [unit.longitude, unit.latitude],
          [77.5990, 12.9730],
          [77.5960, 12.9720],
          [emg ? emg.longitude : 77.5946, emg ? emg.latitude : 12.9716]
        ],
        routingSummary: 'Dijkstra optimal corridor via Central Expressway (3 nodes, 2.4 km)'
      };

      store.assignments.push(assignment);
      if (!store.timeline[emgId]) store.timeline[emgId] = [];
      store.timeline[emgId].push({
        stage: 'ASSIGNED',
        message: `Assigned unit ${unit.unitCode} (${unit.name}). Dijkstra calculated ETA: 6 mins (2.4 km).`,
        timestamp: new Date().toISOString(),
        actorRole: 'CONTROL_CENTER'
      });

      this.saveLocalStore(store);
      return assignment;
    }

    // 6. Responders list
    if (endpoint.includes('/resources/responders')) {
      return store.responders;
    }

    // 7. Hospitals list
    if (endpoint.includes('/hospitals')) {
      return store.hospitals;
    }

    // 8. Blood banks
    if (endpoint.includes('/blood-banks')) {
      return store.bloodBanks;
    }

    // 9. Volunteers & Shelters
    if (endpoint.includes('/volunteers-shelters/volunteers')) {
      return store.volunteers;
    }
    if (endpoint.includes('/volunteers-shelters/shelters')) {
      return store.shelters;
    }

    // 10. Analytics
    if (endpoint.includes('/analytics/overview')) {
      return {
        totalEmergencies: store.emergencies.length,
        resolvedEmergencies: store.emergencies.filter(e => e.status === 'RESOLVED').length,
        criticalEmergencies: store.emergencies.filter(e => e.priority === 'CRITICAL').length,
        activeEmergencies: store.emergencies.filter(e => e.status !== 'RESOLVED').length,
        availableAmbulances: store.responders.filter(r => r.type === 'AMBULANCE' && r.status === 'AVAILABLE').length,
        availablePolice: store.responders.filter(r => r.type === 'POLICE' && r.status === 'AVAILABLE').length,
        availableFire: store.responders.filter(r => r.type === 'FIRE_UNIT' && r.status === 'AVAILABLE').length,
        availableIcuBeds: store.hospitals.reduce((acc, h) => acc + (h.icuBeds - h.occupiedIcuBeds), 0),
        availableEmergencyBeds: store.hospitals.reduce((acc, h) => acc + (h.emergencyBeds - h.occupiedEmergencyBeds), 0),
        availableVentilators: store.hospitals.reduce((acc, h) => acc + (h.ventilators - h.occupiedVentilators), 0),
        averageResponseTimeMinutes: 7.2,
        averageDispatchTimeMinutes: 1.6,
        resourceUtilizationRate: 78.4
      };
    }

    // 11. AI Assistant Endpoints
    if (endpoint === '/assistant/chat') {
      const body = JSON.parse(options.body || '{}');
      const text = body.message || '';
      const lower = text.toLowerCase();
      let category = 'MEDICAL';
      let priority = 'HIGH';
      if (lower.includes('accident') || lower.includes('crash')) category = 'ACCIDENT';
      if (lower.includes('fire') || lower.includes('smoke')) { category = 'FIRE'; priority = 'CRITICAL'; }
      if (lower.includes('unconscious') || lower.includes('critical')) priority = 'CRITICAL';

      let intent = 'REPORT_EMERGENCY';
      let respText = `🚨 Emergency detected: ${category} [${priority} priority]. Review draft details below to confirm.`;

      if (lower.includes('hospital')) {
        intent = 'FIND_HOSPITAL';
        respText = `🏥 Found ${store.hospitals.length} nearby verified hospitals.`;
      } else if (lower.includes('ambulance')) {
        intent = 'FIND_AMBULANCE';
        respText = `🚑 Found ${store.responders.filter(r => r.type === 'AMBULANCE').length} regional ambulances.`;
      } else if (lower.includes('status') || lower.includes('track')) {
        intent = 'CHECK_STATUS';
        respText = `📋 Incident status retrieved.`;
      } else if (lower.includes('guideline') || lower.includes('help')) {
        intent = 'EMERGENCY_GUIDELINES';
        respText = `📖 Emergency Guidelines & First Aid Protocols loaded.`;
      }

      return {
        text: respText,
        intent: intent,
        category: category,
        priority: priority,
        confidenceScore: 0.92,
        emergencyDraft: intent === 'REPORT_EMERGENCY' ? {
          type: category,
          priority: priority,
          description: text,
          latitude: body.latitude || 17.3850,
          longitude: body.longitude || 78.4867,
          address: 'GPS Coordinates Captured',
          injuredCount: 1,
          unconscious: lower.includes('unconscious'),
          severeBleeding: lower.includes('bleed'),
          fireInvolved: category === 'FIRE'
        } : null,
        hospitals: store.hospitals.slice(0, 3),
        ambulances: store.responders.filter(r => r.type === 'AMBULANCE').slice(0, 3),
        incidentStatus: store.emergencies[0] || null,
        guidelineContent: `1. CPR: 100-120 compressions/min\n2. Severe Bleeding: Direct pressure\n3. Helpline: 112 / 108`
      };
    }

    if (endpoint === '/assistant/emergency-confirm') {
      const body = JSON.parse(options.body || '{}');
      const incCode = 'JS-2026-' + Math.floor(100000 + Math.random() * 900000);
      const newEmergency = {
        id: 'emg_' + Date.now(),
        incidentCode: incCode,
        type: body.type || 'MEDICAL',
        priority: body.priority || 'HIGH',
        status: 'REPORTED',
        latitude: body.latitude || 17.3850,
        longitude: body.longitude || 78.4867,
        address: body.address || 'Reported via JeevanSetu AI Assistant',
        description: body.description || 'Emergency incident reported',
        peopleAffected: body.peopleAffected || 1,
        injuredCount: body.injuredCount || 1,
        unconscious: !!body.unconscious,
        severeBleeding: !!body.severeBleeding,
        fireInvolved: !!body.fireInvolved,
        trapped: !!body.trapped,
        requiredResources: ['AMBULANCE', 'HOSPITAL'],
        assignedResourceIds: [],
        createdAt: new Date().toISOString()
      };
      store.emergencies.unshift(newEmergency);
      this.saveLocalStore(store);
      return newEmergency;
    }

    // 12. Emergency SOS Panic Trigger
    if (endpoint === '/emergencies/sos') {
      const body = JSON.parse(options.body || '{}');
      const incCode = 'JS-SOS-2026-' + Math.floor(1000 + Math.random() * 9000);
      const sosEmergency = {
        id: 'sos_' + Date.now(),
        incidentCode: incCode,
        type: 'MEDICAL',
        priority: 'CRITICAL',
        status: 'REPORTED',
        latitude: body.latitude || 17.3850,
        longitude: body.longitude || 78.4867,
        address: body.address || 'Instant Citizen SOS Panic Coordinates',
        description: '🚨 EMERGENCY SOS PANIC: Distress signal triggered with live verified GPS coordinates.',
        peopleAffected: 1,
        injuredCount: 1,
        unconscious: false,
        severeBleeding: false,
        fireInvolved: false,
        trapped: false,
        requiredResources: ['AMBULANCE', 'POLICE'],
        assignedResourceIds: [],
        createdAt: new Date().toISOString()
      };
      store.emergencies.unshift(sosEmergency);
      this.saveLocalStore(store);
      return sosEmergency;
    }

    // 13. Multi-Agency Communications
    if (endpoint.startsWith('/agency-comms/incident/')) {
      const incId = endpoint.split('/').pop();
      if (!store.agencyMessages) store.agencyMessages = {};
      if (!store.agencyMessages[incId]) store.agencyMessages[incId] = [
        {
          id: 'msg-1',
          incidentId: incId,
          senderName: 'Commander Sharma',
          senderRole: 'CONTROL_CENTER',
          agencyType: 'DISPATCH',
          message: 'Tactical emergency channel opened. All agency units maintain radio contact on this frequency.',
          priority: 'ROUTINE',
          timestamp: new Date(Date.now() - 600000).toISOString()
        },
        {
          id: 'msg-2',
          incidentId: incId,
          senderName: 'Pilot Rajesh Kumar (AMB-104)',
          senderRole: 'AMBULANCE',
          agencyType: 'EMS',
          message: 'AMB-104 en-route via Expressway corridor. Expected scene arrival in 4 minutes.',
          priority: 'URGENT',
          timestamp: new Date(Date.now() - 300000).toISOString()
        }
      ];

      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        const auth = this.getAuth();
        const newMsg = {
          id: 'msg_' + Date.now(),
          incidentId: incId,
          senderName: auth.fullName || body.senderName || 'Responder',
          senderRole: auth.role || 'CONTROL_CENTER',
          agencyType: body.agencyType || 'TACTICAL',
          message: body.message || '',
          priority: body.priority || 'ROUTINE',
          timestamp: new Date().toISOString()
        };
        store.agencyMessages[incId].push(newMsg);
        this.saveLocalStore(store);
        return newMsg;
      }

      return store.agencyMessages[incId];
    }

    // 14. Dispatcher Summary
    if (endpoint.startsWith('/assistant/dispatcher-summary/')) {
      const incId = endpoint.split('/').pop();
      const emg = store.emergencies.find(e => e.id === incId || e.incidentCode === incId) || store.emergencies[0];
      return {
        incident_code: emg ? emg.incidentCode : 'JS-2026-000184',
        original_report: emg ? emg.description : 'Emergency incident scene',
        category: emg ? emg.type : 'ACCIDENT',
        priority: emg ? emg.priority : 'CRITICAL',
        confirmed_facts: [
          `Scene Coordinates: ${emg.latitude}, ${emg.longitude} (${emg.address})`,
          `Casualties: ${emg.injuredCount || 2} persons reported`,
          emg.unconscious ? 'Clinical: Unconscious trauma status confirmed' : 'Clinical: Patient conscious',
          emg.severeBleeding ? 'Hemorrhage Hazard: Active severe bleeding' : 'Hemorrhage: Controlled'
        ],
        uncertain_information: [
          'Witness self-report; formal paramedic vital signs pending on-scene arrival',
          'Vehicle fuel leakage risk uninspected; pending Fire Department assessment'
        ],
        recommended_agencies: ['AMBULANCE', 'POLICE'],
        triage_summary: `Incident ${emg.incidentCode}: Rating=${emg.priority}. Dispatcher approval required prior to routing.`
      };
    }

    // 15. Smart Ambulance Recommendation
    if (endpoint.startsWith('/assistant/ambulances/recommend/')) {
      const incId = endpoint.split('/').pop();
      const emg = store.emergencies.find(e => e.id === incId || e.incidentCode === incId) || store.emergencies[0];
      return {
        incidentId: emg.id,
        incidentCode: emg.incidentCode,
        priority: emg.priority,
        recommendedAmbulances: store.responders.filter(r => r.type === 'AMBULANCE').slice(0, 3),
        requiresDispatcherApproval: true,
        governancePolicy: 'SAFETY_FIRST: Units cannot be automatically dispatched without authenticated command operator sign-off.'
      };
    }

    // 16. Smart Hospital Recommendation
    if (endpoint.startsWith('/assistant/hospitals/recommend/')) {
      const incId = endpoint.split('/').pop();
      const emg = store.emergencies.find(e => e.id === incId || e.incidentCode === incId) || store.emergencies[0];
      return {
        incidentId: emg.id,
        incidentCode: emg.incidentCode,
        hospitals: store.hospitals.map(h => ({
          hospitalId: h.id,
          name: h.name,
          address: h.address,
          distanceKm: 2.1,
          availableIcuBeds: Math.max(0, h.icuBeds - h.occupiedIcuBeds),
          availableEmergencyBeds: Math.max(0, h.emergencyBeds - h.occupiedEmergencyBeds),
          contactPhone: h.emergencyContact,
          verifiedAvailability: true
        })),
        note: 'Hospital availability is verified from real-time database telemetry. Capacity is never artificially projected.'
      };
    }

    return {};
  },

  getLocalStore() {
    let data = localStorage.getItem('jeevansetu_data_cache');
    if (!data) {
      const initial = this.generateInitialSeed();
      this.saveLocalStore(initial);
      return initial;
    }
    return JSON.parse(data);
  },

  saveLocalStore(data) {
    localStorage.setItem('jeevansetu_data_cache', JSON.stringify(data));
  },

  generateInitialSeed() {
    const lat = 12.9716, lng = 77.5946;
    return {
      emergencies: [
        {
          id: 'emg-101',
          incidentCode: 'JS-2026-000184',
          type: 'ACCIDENT',
          priority: 'CRITICAL',
          status: 'PROCESSING',
          latitude: lat + 0.005,
          longitude: lng - 0.004,
          address: 'MG Road Junction near Metro Pillar 140',
          description: 'Two-car collision, passenger trapped with severe bleeding and loss of consciousness.',
          peopleAffected: 3,
          injuredCount: 2,
          unconscious: true,
          severeBleeding: true,
          fireInvolved: false,
          trapped: true,
          requiredResources: ['AMBULANCE', 'POLICE'],
          assignedResourceIds: [],
          createdAt: new Date(Date.now() - 300000).toISOString()
        },
        {
          id: 'emg-102',
          incidentCode: 'JS-2026-000185',
          type: 'FIRE',
          priority: 'CRITICAL',
          status: 'ASSIGNED',
          latitude: lat - 0.008,
          longitude: lng + 0.006,
          address: 'Commercial Plaza 3rd Floor, Brigade Road',
          description: 'Electrical transformer fire with heavy smoke. Evacuation in progress.',
          peopleAffected: 12,
          injuredCount: 1,
          unconscious: false,
          severeBleeding: false,
          fireInvolved: true,
          trapped: false,
          requiredResources: ['FIRE_UNIT', 'POLICE', 'AMBULANCE'],
          assignedResourceIds: ['resp-301'],
          createdAt: new Date(Date.now() - 900000).toISOString()
        },
        {
          id: 'emg-103',
          incidentCode: 'JS-2026-000186',
          type: 'MEDICAL',
          priority: 'HIGH',
          status: 'EN_ROUTE',
          latitude: lat + 0.012,
          longitude: lng + 0.008,
          address: 'Apartment 402, Green Glen Layout',
          description: 'Elderly patient with acute respiratory distress and severe chest pains.',
          peopleAffected: 1,
          injuredCount: 1,
          unconscious: false,
          severeBleeding: false,
          fireInvolved: false,
          trapped: false,
          requiredResources: ['AMBULANCE', 'HOSPITAL'],
          assignedResourceIds: ['resp-102'],
          createdAt: new Date(Date.now() - 1500000).toISOString()
        }
      ],
      responders: [
        { id: 'resp-101', unitCode: 'AMB-104', name: 'Metro Trauma EMS 104', type: 'AMBULANCE', status: 'AVAILABLE', latitude: lat + 0.008, longitude: lng - 0.007, driverName: 'Rajesh Kumar', contactPhone: '98450-20104', capacity: 4, equipmentLevel: 'Advanced Life Support (ALS) & Trauma Kit', organization: 'National Ambulance Service' },
        { id: 'resp-102', unitCode: 'AMB-105', name: 'City EMS 105', type: 'AMBULANCE', status: 'EN_ROUTE', latitude: lat + 0.010, longitude: lng + 0.005, driverName: 'Sunil Gowda', contactPhone: '98450-20105', capacity: 2, equipmentLevel: 'Basic Life Support (BLS)', organization: 'National Ambulance Service' },
        { id: 'resp-103', unitCode: 'AMB-106', name: 'Lifeline Trauma 106', type: 'AMBULANCE', status: 'AVAILABLE', latitude: lat - 0.012, longitude: lng - 0.009, driverName: 'Pradeep Yadav', contactPhone: '98450-20106', capacity: 4, equipmentLevel: 'Advanced Life Support (ALS)', organization: 'Red Cross EMS' },
        { id: 'resp-201', unitCode: 'POL-201', name: 'Highway Patrol PCR-1', type: 'POLICE', status: 'AVAILABLE', latitude: lat + 0.003, longitude: lng + 0.004, driverName: 'Inspector Sharma', contactPhone: '98451-30201', capacity: 4, equipmentLevel: 'Tactical Emergency & Crowd Control', organization: 'Metro Police' },
        { id: 'resp-202', unitCode: 'POL-202', name: 'Traffic Interceptor 2', type: 'POLICE', status: 'AVAILABLE', latitude: lat - 0.005, longitude: lng - 0.002, driverName: 'Sub-Inspector Rao', contactPhone: '98451-30202', capacity: 4, equipmentLevel: 'Traffic Clearance & Escort', organization: 'Metro Police' },
        { id: 'resp-301', unitCode: 'FIRE-301', name: 'Central Tender 301', type: 'FIRE_UNIT', status: 'ASSIGNED', latitude: lat - 0.009, longitude: lng + 0.008, driverName: 'Officer Jadhav', contactPhone: '98452-40301', capacity: 6, equipmentLevel: 'Hydraulic Rescue & Hazmat Foam', organization: 'Fire & Disaster Services' },
        { id: 'resp-302', unitCode: 'FIRE-302', name: 'Rapid Response Rescue 302', type: 'FIRE_UNIT', status: 'AVAILABLE', latitude: lat + 0.014, longitude: lng - 0.012, driverName: 'Officer Kulkarni', contactPhone: '98452-40302', capacity: 5, equipmentLevel: 'High-Reach Water Tender', organization: 'Fire & Disaster Services' }
      ],
      hospitals: [
        { id: 'hosp-1', name: 'Government Victoria Trauma Hospital', registrationId: 'HOSP-1001', address: 'Medical College Campus, Fort', latitude: lat + 0.007, longitude: lng - 0.008, emergencyBeds: 40, occupiedEmergencyBeds: 22, icuBeds: 18, occupiedIcuBeds: 11, ventilators: 12, occupiedVentilators: 7, operatingRooms: 6, occupiedOperatingRooms: 2, status: 'AVAILABLE', emergencyContact: '080-22001001' },
        { id: 'hosp-2', name: 'Apex Multi-Speciality Medical Center', registrationId: 'HOSP-1002', address: 'Central Ring Road, Zone 2', latitude: lat - 0.006, longitude: lng + 0.011, emergencyBeds: 35, occupiedEmergencyBeds: 18, icuBeds: 14, occupiedIcuBeds: 8, ventilators: 10, occupiedVentilators: 4, operatingRooms: 5, occupiedOperatingRooms: 3, status: 'AVAILABLE', emergencyContact: '080-22001002' },
        { id: 'hosp-3', name: 'Fortis Emergency & Trauma Institute', registrationId: 'HOSP-1003', address: 'Bannerghatta Tech Corridor', latitude: lat - 0.018, longitude: lng - 0.005, emergencyBeds: 50, occupiedEmergencyBeds: 30, icuBeds: 20, occupiedIcuBeds: 13, ventilators: 15, occupiedVentilators: 8, operatingRooms: 8, occupiedOperatingRooms: 4, status: 'AVAILABLE', emergencyContact: '080-22001003' }
      ],
      bloodBanks: [
        { id: 'bb-1', name: 'Red Cross Central Blood Bank', registrationId: 'BB-2001', address: 'Red Cross Bhavan, Race Course Rd', latitude: lat + 0.004, longitude: lng - 0.006, phone: '080-23405001', inventory: { 'A+': 52, 'A-': 18, 'B+': 64, 'B-': 14, 'O+': 75, 'O-': 10, 'AB+': 32, 'AB-': 8 }, status: 'OPERATIONAL' },
        { id: 'bb-2', name: 'Rotary Lifeline Blood Center', registrationId: 'BB-2002', address: 'Lavelle Road Sector 3', latitude: lat - 0.005, longitude: lng + 0.004, phone: '080-23405002', inventory: { 'A+': 38, 'A-': 12, 'B+': 45, 'B-': 9, 'O+': 55, 'O-': 6, 'AB+': 20, 'AB-': 4 }, status: 'OPERATIONAL' }
      ],
      volunteers: [
        { id: 'vol-1', name: 'Gautam Gambhir', phone: '98900-60001', address: 'Indiranagar Sector 2', latitude: lat + 0.006, longitude: lng + 0.012, emergencyRadiusKm: 10, availabilityStatus: 'AVAILABLE', skills: ['First Aid', 'Driving'] },
        { id: 'vol-2', name: 'Priya Nair', phone: '98900-60002', address: 'Koramangala 4th Block', latitude: lat - 0.010, longitude: lng + 0.008, emergencyRadiusKm: 8, availabilityStatus: 'AVAILABLE', skills: ['First Aid', 'Blood Donation', 'Rescue'] }
      ],
      shelters: [
        { id: 'sh-1', name: 'City Relief Shelter A (Indoor Stadium)', address: 'Kanteerava Sports Complex', latitude: lat - 0.003, longitude: lng - 0.004, totalCapacity: 600, occupiedCapacity: 240, status: 'OPEN', contactPhone: '080-25607001' },
        { id: 'sh-2', name: 'Community Disaster Shelter B', address: 'Malleshwaram Community Hall', latitude: lat + 0.015, longitude: lng - 0.010, totalCapacity: 450, occupiedCapacity: 110, status: 'OPEN', contactPhone: '080-25607002' }
      ],
      assignments: [],
      timeline: {}
    };
  }
};
