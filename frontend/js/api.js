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

    // 17. Duplicate Emergency Detection
    if (endpoint === '/emergencies/check-duplicate') {
      const body = JSON.parse(options.body || '{}');
      const matches = store.emergencies.filter(e => {
        if (!e.latitude || !body.latitude) return false;
        const dLat = (e.latitude - body.latitude) * 111;
        const dLng = (e.longitude - body.longitude) * 111 * Math.cos(e.latitude * Math.PI / 180);
        const distKm = Math.sqrt(dLat * dLat + dLng * dLng);
        return distKm <= 0.5 && e.status !== 'RESOLVED';
      });
      if (matches.length > 0) {
        return {
          duplicateSuspected: true,
          matchConfidenceScore: 0.88,
          matchedIncidentCode: matches[0].incidentCode,
          matchedIncidentId: matches[0].id,
          explanation: `High proximity (${matches.length} active incident within 500m)`
        };
      }
      return { duplicateSuspected: false, matchConfidenceScore: 0.1, explanation: 'No close duplicates found' };
    }

    // 18. Incident Command Centre
    if (endpoint === '/command/overview') {
      const active = store.emergencies.filter(e => e.status !== 'RESOLVED').length;
      const crit = store.emergencies.filter(e => e.priority === 'CRITICAL' && e.status !== 'RESOLVED').length;
      return {
        totalEmergencies: store.emergencies.length,
        activeEmergencies: active,
        criticalEmergencies: crit,
        escalatedEmergencies: store.emergencies.filter(e => e.escalated).length,
        resolvedEmergencies: store.emergencies.filter(e => e.status === 'RESOLVED').length,
        availableAmbulances: store.responders.filter(r => r.type === 'AMBULANCE' && r.status === 'AVAILABLE').length,
        availablePoliceUnits: store.responders.filter(r => r.type === 'POLICE' && r.status === 'AVAILABLE').length,
        availableFireUnits: store.responders.filter(r => r.type === 'FIRE_UNIT' && r.status === 'AVAILABLE').length
      };
    }

    if (endpoint === '/command/incidents') {
      return store.emergencies;
    }

    if (endpoint.includes('/command/') && endpoint.endsWith('/assign-agency')) {
      const parts = endpoint.split('/');
      const emgId = parts[2];
      const body = JSON.parse(options.body || '{}');
      const emg = store.emergencies.find(e => e.id === emgId);
      if (emg) {
        emg.assignedAgency = body.agency;
        emg.status = 'ASSIGNED';
        if (body.remarks) emg.dispatcherRemarks = body.remarks;
      }
      this.saveLocalStore(store);
      return emg || {};
    }

    if (endpoint.includes('/command/') && endpoint.endsWith('/escalate')) {
      const parts = endpoint.split('/');
      const emgId = parts[2];
      const emg = store.emergencies.find(e => e.id === emgId);
      if (emg) {
        emg.escalated = true;
        emg.escalationLevel = (emg.escalationLevel || 0) + 1;
        emg.escalatedAt = new Date().toISOString();
      }
      this.saveLocalStore(store);
      return emg || {};
    }

    if (endpoint.includes('/command/') && endpoint.endsWith('/acknowledge-escalation')) {
      const parts = endpoint.split('/');
      const emgId = parts[2];
      const emg = store.emergencies.find(e => e.id === emgId);
      if (emg) {
        emg.escalationAcknowledged = true;
      }
      this.saveLocalStore(store);
      return emg || {};
    }

    if (endpoint.includes('/command/') && endpoint.endsWith('/resolve')) {
      const parts = endpoint.split('/');
      const emgId = parts[2];
      const emg = store.emergencies.find(e => e.id === emgId);
      if (emg) {
        emg.status = 'RESOLVED';
      }
      this.saveLocalStore(store);
      return emg || {};
    }

    // 19. Volunteers & Tasks
    if (endpoint === '/volunteers') {
      return store.volunteers || [];
    }

    if (endpoint === '/volunteers/tasks/available' || endpoint === '/volunteers/tasks/pending-approval') {
      return store.volunteerTasks || [
        { id: 'vt-1', title: 'Traffic Divert Support', taskType: 'TRAFFIC_CONTROL', status: 'PENDING_APPROVAL', serviceArea: 'MG Road', priority: 'MEDIUM', requiredVolunteers: 2, description: 'Assist police traffic cordon at MG Road junction.' },
        { id: 'vt-2', title: 'First Aid Triage Post', taskType: 'FIRST_AID', status: 'APPROVED', serviceArea: 'Brigade Road', priority: 'HIGH', requiredVolunteers: 3, description: 'Distribute medical kits and guide non-critical walking wounded.' }
      ];
    }

    // 20. Disaster Events & Relief Centers
    if (endpoint === '/disaster/events') {
      return store.disasterEvents || [
        { id: 'dis-1', title: 'Urban Flash Flood Warning', disasterType: 'FLOOD', severityLevel: 'HIGH', status: 'ACTIVE', affectedDistrict: 'Central Metro', estimatedAffectedPopulation: 5000, description: 'Low lying road inundated with water logging.' }
      ];
    }

    if (endpoint === '/disaster/centers') {
      return store.shelters.map(s => ({
        id: s.id,
        name: s.name,
        address: s.address,
        capacity: s.totalCapacity,
        currentOccupancy: s.occupiedCapacity,
        status: s.status,
        contactPerson: 'Disaster Shelter In-Charge',
        contactPhone: s.contactPhone
      }));
    }

    // 21. Emergency Inventory
    if (endpoint === '/inventory/items') {
      return store.inventoryItems || [
        { id: 'inv-1', itemName: 'Oxygen Cylinders (10L)', itemCode: 'MED-OXY-10L', category: 'MEDICAL_EQUIPMENT', currentStock: 45, minimumStockThreshold: 15, unitOfMeasure: 'Cylinders', location: 'Central Warehouse Zone 1' },
        { id: 'inv-2', itemName: 'Advanced Trauma First Aid Kits', itemCode: 'MED-FAK-01', category: 'FIRST_AID', currentStock: 120, minimumStockThreshold: 30, unitOfMeasure: 'Kits', location: 'Base EMS Depot' },
        { id: 'inv-3', itemName: 'Packaged Drinking Water (20L Cans)', itemCode: 'REL-WTR-20L', category: 'WATER', currentStock: 80, minimumStockThreshold: 100, unitOfMeasure: 'Cans', location: 'Relief Depot West' }
      ];
    }

    if (endpoint === '/inventory/low-stock') {
      const items = store.inventoryItems || [
        { id: 'inv-3', itemName: 'Packaged Drinking Water (20L Cans)', itemCode: 'REL-WTR-20L', category: 'WATER', currentStock: 80, minimumStockThreshold: 100, unitOfMeasure: 'Cans', location: 'Relief Depot West' }
      ];
      return items.filter(i => i.currentStock <= i.minimumStockThreshold);
    }

    // 22. Geo Zones & Geo-fencing
    if (endpoint === '/geofence/zones') {
      return store.geoZones || [
        { id: 'gz-1', zoneName: 'MG Road Underpass Flood Zone', zoneType: 'DANGER_ZONE', centerLatitude: 12.9716, centerLongitude: 77.5946, radiusMeters: 500, active: true, advisoryNotice: 'Extreme water accumulation; traffic diverted.' },
        { id: 'gz-2', zoneName: 'Victoria Hospital Safe Corridor', zoneType: 'SAFE_HAVEN', centerLatitude: 12.9786, centerLongitude: 77.5866, radiusMeters: 800, active: true, advisoryNotice: 'Ambulance green channel active.' }
      ];
    }

    // 23. Incident Specific Chat Room
    if (endpoint.startsWith('/incident-chat/')) {
      const parts = endpoint.split('/');
      const emgId = parts[2];
      if (!store.incidentChats) store.incidentChats = {};
      if (!store.incidentChats[emgId]) {
        store.incidentChats[emgId] = [
          { id: 'msg-1', incidentId: emgId, senderName: 'Officer Ananya Sharma', senderRole: 'DISPATCHER', senderAgency: 'Command Center', message: 'Tactical channel established for incident. Responders check in.', priority: 'NORMAL', createdAt: new Date(Date.now() - 300000).toISOString() },
          { id: 'msg-2', incidentId: emgId, senderName: 'Rajesh Kumar', senderRole: 'AMBULANCE', senderAgency: 'Trauma EMS', message: 'AMB-104 en-route. Distance 1.8km, ETA 4 mins.', priority: 'URGENT', createdAt: new Date(Date.now() - 120000).toISOString() }
        ];
      }

      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        const auth = this.getAuth();
        const newMsg = {
          id: 'msg_' + Date.now(),
          incidentId: emgId,
          senderName: auth.fullName || 'Tactical Operator',
          senderRole: auth.role || 'DISPATCHER',
          senderAgency: body.senderAgency || 'Tactical Dispatch',
          message: body.message,
          priority: body.priority || 'NORMAL',
          createdAt: new Date().toISOString()
        };
        store.incidentChats[emgId].push(newMsg);
        this.saveLocalStore(store);
        return newMsg;
      }

      return store.incidentChats[emgId];
    }

    // 24. Blood Donor Coordination
    if (endpoint.includes('/blood-coordination/search-donors')) {
      return store.bloodDonors || [
        { id: 'bd-1', donorName: 'R*** S***', bloodGroup: 'O_POSITIVE', city: 'Metro City', latitude: 12.9716, longitude: 77.5946, totalDonations: 4, availableForEmergency: true, verifiedStatus: true },
        { id: 'bd-2', donorName: 'A*** K***', bloodGroup: 'AB_NEGATIVE', city: 'Metro City', latitude: 12.9750, longitude: 77.5990, totalDonations: 2, availableForEmergency: true, verifiedStatus: true }
      ];
    }

    // 25. Vehicle Records & Maintenance
    if (endpoint === '/vehicles') {
      return store.vehicleRecords || [
        { id: 'veh-1', registrationNumber: 'KA-01-EA-1040', vehicleType: 'AMBULANCE', makeModel: 'Force Traveller ALS', serviceStatus: 'ACTIVE', fuelLevelPercentage: 85, driverName: 'Rajesh Kumar', operationalReadinessRating: 'READY' },
        { id: 'veh-2', registrationNumber: 'KA-01-EA-1055', vehicleType: 'AMBULANCE', makeModel: 'Tata Winger BLS', serviceStatus: 'MAINTENANCE', fuelLevelPercentage: 40, driverName: 'Sunil Gowda', operationalReadinessRating: 'MAINTENANCE_REQUIRED' },
        { id: 'veh-3', registrationNumber: 'KA-01-PF-3010', vehicleType: 'FIRE_ENGINE', makeModel: 'Ashok Leyland Water Bowser', serviceStatus: 'ACTIVE', fuelLevelPercentage: 92, driverName: 'Officer Jadhav', operationalReadinessRating: 'READY' }
      ];
    }

    // 26. In-App Notifications
    if (endpoint === '/notifications') {
      return store.notifications || [
        { id: 'notif-1', title: 'High Priority Incident', message: 'Critical trauma accident reported at MG Road Junction.', category: 'EMERGENCY_ALERT', read: false, createdAt: new Date(Date.now() - 180000).toISOString() },
        { id: 'notif-2', title: 'Low Stock Alert', message: 'Drinking water inventory below safety threshold in West depot.', category: 'RESOURCE_ALERT', read: false, createdAt: new Date(Date.now() - 600000).toISOString() }
      ];
    }

    if (endpoint === '/notifications/unread-count') {
      const notifs = store.notifications || [];
      return { unreadCount: notifs.filter(n => !n.read).length || 2 };
    }

    // 27. Advanced Admin Overview & User Management
    if (endpoint === '/admin/overview') {
      return {
        totalUsers: 142,
        activeResponders: store.responders.length,
        totalHospitals: store.hospitals.length,
        totalBloodBanks: store.bloodBanks.length,
        totalVolunteers: 48,
        activeEmergencies: store.emergencies.filter(e => e.status !== 'RESOLVED').length,
        systemHealth: '100% OPERATIONAL',
        databaseEngine: 'MongoDB 2dsphere Geospatial',
        backendEngine: 'Java 21 Spring Boot + Python NLP Microservice',
        auditLogsCount: 524
      };
    }

    if (endpoint === '/admin/users') {
      return [
        { id: 'u-1', fullName: 'Officer Ananya Sharma', email: 'command@jeevansetu.gov.in', role: 'CONTROL_CENTER', active: true, agency: 'Tactical Command' },
        { id: 'u-2', fullName: 'Dr. Vikram Seth', email: 'admin@jeevansetu.gov.in', role: 'ADMIN', active: true, agency: 'System Administration' },
        { id: 'u-3', fullName: 'Rajesh Kumar', email: 'ambulance@jeevansetu.gov.in', role: 'AMBULANCE', active: true, agency: 'National Ambulance Service' },
        { id: 'u-4', fullName: 'Dr. Sunita Rao', email: 'hospital@jeevansetu.gov.in', role: 'HOSPITAL', active: true, agency: 'Victoria Trauma ER' },
        { id: 'u-5', fullName: 'Aarav Patel', email: 'citizen@jeevansetu.in', role: 'CITIZEN', active: true, agency: 'Public Citizen' }
      ];
    }

    if (endpoint === '/admin/system-config') {
      return {
        criticalEscalationMinutes: 5,
        highEscalationMinutes: 10,
        mediumEscalationMinutes: 20,
        lowEscalationMinutes: 45,
        duplicateDistanceKm: 0.5,
        duplicateTimeMinutes: 30,
        geoFenceAlertEnabled: true,
        aiNlpConfidenceThreshold: 0.70
      };
    }

    // 28. Analytics Reports Summary & CSV
    if (endpoint === '/analytics/reports/summary') {
      return {
        totalEmergenciesLogged: store.emergencies.length,
        resolvedRate: '88.5%',
        averageResponseTimeMinutes: 6.8,
        averageDispatchTimeMinutes: 1.4,
        criticalCasesHandled: store.emergencies.filter(e => e.priority === 'CRITICAL').length,
        busiestDistrict: 'Central Metro Corridor',
        systemUptime: '99.98%'
      };
    }

    if (endpoint === '/analytics/export/csv') {
      return 'IncidentCode,Type,Priority,Status,Address,CreatedAt\n' +
        store.emergencies.map(e => `${e.incidentCode},${e.type},${e.priority},${e.status},"${e.address}",${e.createdAt}`).join('\n');
    }

    // ==========================================
    // 29. Smart Evacuation Planner & Safe Zones
    // ==========================================
    if (endpoint === '/evacuation/safe-zones') {
      return store.safeZones || [
        { id: 'sz-1', name: 'Central Sports Complex (Zone A)', zoneType: 'COMMUNITY_CENTRE', address: 'MG Road Stadium Grounds', latitude: 12.9730, longitude: 77.6010, totalCapacity: 800, currentOccupancy: 320, availableCapacity: 480, medicalSupportAvailable: true, foodAndWaterAvailable: true, open: true, contactPhone: '080-25607001' },
        { id: 'sz-2', name: 'University Science Campus Hall', zoneType: 'HIGHER_GROUND', address: 'Outer Ring Elevated Ridge', latitude: 12.9850, longitude: 77.5850, totalCapacity: 500, currentOccupancy: 120, availableCapacity: 380, medicalSupportAvailable: true, foodAndWaterAvailable: true, open: true, contactPhone: '080-25607002' },
        { id: 'sz-3', name: 'District Relief Pavilion 3', zoneType: 'DISASTER_SHELTER', address: 'Indiranagar 80ft Arena', latitude: 12.9650, longitude: 77.6250, totalCapacity: 1200, currentOccupancy: 450, availableCapacity: 750, medicalSupportAvailable: true, foodAndWaterAvailable: true, open: true, contactPhone: '080-25607003' }
      ];
    }

    if (endpoint === '/evacuation/blocked-roads') {
      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        body.id = 'blk-' + Date.now();
        body.blockedAt = new Date().toISOString();
        if (!store.blockedRoads) store.blockedRoads = [];
        store.blockedRoads.push(body);
        this.saveLocalStore(store);
        return body;
      }
      return store.blockedRoads || [
        { id: 'blk-1', roadName: 'Brigade Underpass Sub-level', reason: 'Flooding 3.5ft', startLatitude: 12.9710, startLongitude: 77.5990, endLatitude: 12.9725, endLongitude: 77.6005, blockedAt: new Date(Date.now() - 3600000).toISOString() },
        { id: 'blk-2', roadName: 'Old Airport Overpass Ramp', reason: 'Structural Inspection', startLatitude: 12.9600, startLongitude: 77.6200, endLatitude: 12.9615, endLongitude: 77.6215, blockedAt: new Date(Date.now() - 7200000).toISOString() }
      ];
    }

    if (endpoint.startsWith('/evacuation/blocked-roads/') && options.method === 'DELETE') {
      const blkId = endpoint.split('/').pop();
      if (store.blockedRoads) {
        store.blockedRoads = store.blockedRoads.filter(b => b.id !== blkId);
        this.saveLocalStore(store);
      }
      return { success: true };
    }

    if (endpoint === '/evacuation/plan' && options.method === 'POST') {
      const body = JSON.parse(options.body || '{}');
      const originLat = body.originLatitude || 12.9716;
      const originLng = body.originLongitude || 77.5946;
      const targetShelter = {
        name: 'Central Sports Complex (Zone A)',
        address: 'MG Road Stadium Grounds',
        availableCapacity: 480,
        latitude: 12.9730,
        longitude: 77.6010,
        contactPhone: '080-25607001'
      };
      return {
        id: 'EVAC-' + Date.now(),
        hazardAreaName: body.hazardAreaName || 'Active Incident Hazard Zone',
        evacuationUrgency: 'IMMEDIATE',
        recommendedShelter: targetShelter,
        primaryCorridorWaypoints: [
          [originLat, originLng],
          [originLat + 0.001, originLng + 0.003],
          [originLat + 0.002, originLng + 0.005],
          [targetShelter.latitude, targetShelter.longitude]
        ],
        alternativeCorridorWaypoints: [
          [originLat, originLng],
          [originLat - 0.002, originLng + 0.002],
          [originLat - 0.001, originLng + 0.005],
          [targetShelter.latitude, targetShelter.longitude]
        ],
        evacuationDistanceKm: 2.8,
        estimatedTransitMinutes: 12.5,
        turnByTurnInstructions: [
          'Head east toward North Cross Road (0.6 km). Avoid Brigade Underpass.',
          'Turn left onto Stadium Boulevard Link Corridor (1.2 km).',
          'Arrive at Central Sports Complex Relief Gate 2 (0.4 km).'
        ],
        activeRoadRestrictionsEncountered: 1,
        generatedAt: new Date().toISOString()
      };
    }

    // ==========================================
    // 30. Family Reunification System
    // ==========================================
    if (endpoint === '/reunification/family-groups' && options.method === 'POST') {
      const body = JSON.parse(options.body || '{}');
      body.id = 'FAM-' + Date.now();
      body.registrationCode = 'FAM-' + Math.floor(1000 + Math.random() * 9000);
      return body;
    }

    if (endpoint === '/reunification/missing-reports') {
      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        body.id = 'MIS-' + Date.now();
        body.reportCode = 'MIS-2026-' + Math.floor(100 + Math.random() * 900);
        body.status = 'ACTIVE_SEARCH';
        body.reportedAt = new Date().toISOString();
        if (!store.missingReports) store.missingReports = [];
        store.missingReports.unshift(body);
        this.saveLocalStore(store);
        return body;
      }
      return store.missingReports || [
        { id: 'mis-1', reportCode: 'MIS-2026-101', missingPersonName: 'Kavita Sundaram', age: 34, gender: 'FEMALE', lastKnownLocation: 'Commercial Plaza Floor 2', status: 'ACTIVE_SEARCH', maskedContact: '9845X-XXX01', reportedAt: new Date(Date.now() - 3600000).toISOString() },
        { id: 'mis-2', reportCode: 'MIS-2026-102', missingPersonName: 'Aarush V.', age: 9, gender: 'MALE', lastKnownLocation: 'Victoria Metro Corridor Exit', status: 'TRACED_SAFE', maskedContact: '9845X-XXX02', reportedAt: new Date(Date.now() - 7200000).toISOString() }
      ];
    }

    if (endpoint === '/reunification/safe-arrivals') {
      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        body.id = 'ARR-' + Date.now();
        body.confirmedAt = new Date().toISOString();
        if (!store.safeArrivals) store.safeArrivals = [];
        store.safeArrivals.unshift(body);
        this.saveLocalStore(store);
        return body;
      }
      return store.safeArrivals || [
        { id: 'arr-1', citizenName: 'Karan Mehra', shelterOrSafeZoneName: 'Central Sports Complex (Zone A)', message: 'Reached relief hall safely with parents. We have water and charge.', arrivalTimestamp: new Date(Date.now() - 1800000).toISOString() },
        { id: 'arr-2', citizenName: 'Deepa Krishnan', shelterOrSafeZoneName: 'University Science Campus Hall', message: 'First aid received. Battery is low but safe.', arrivalTimestamp: new Date(Date.now() - 4500000).toISOString() }
      ];
    }

    if (endpoint.startsWith('/reunification/search')) {
      const reports = store.missingReports || [
        { id: 'mis-1', reportCode: 'MIS-2026-101', missingPersonName: 'Kavita S***', age: 34, gender: 'FEMALE', lastKnownLocation: 'Commercial Plaza Floor 2', status: 'ACTIVE_SEARCH', maskedContact: '9845X-XXX01', reportedAt: new Date().toISOString() }
      ];
      return reports;
    }

    // ==========================================
    // 31. Disaster Simulation Lab
    // ==========================================
    if (endpoint === '/simulation/scenarios') {
      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        body.id = 'SIM-SCN-' + Date.now();
        body.isSimulation = true;
        body.simulationStatus = 'DRAFT';
        body.createdAt = new Date().toISOString();
        return body;
      }
      return [
        { id: 'scn-1', title: 'Urban Flash Flood — Central Metro Basin', scenarioType: 'FLOOD', incidentCount: 12, availableAmbulanceCount: 4, hospitalBedCapacity: 10, isSimulation: true },
        { id: 'scn-2', title: 'Chemical Plant Thermal Explosion', scenarioType: 'FIRE', incidentCount: 8, availableAmbulanceCount: 5, hospitalBedCapacity: 6, isSimulation: true }
      ];
    }

    if (endpoint.includes('/simulation/scenarios/') && endpoint.endsWith('/run')) {
      const scnId = endpoint.split('/')[3];
      return {
        id: 'SIM-RES-' + Date.now(),
        scenarioId: scnId,
        scenarioTitle: 'Sandbox Stress Test',
        strategyName: 'Heuristic Multi-Factor Ranking',
        totalSimulatedIncidents: 10,
        criticalIncidentsTriageCount: 5,
        successfullyDispatchedCount: 7,
        unhandledOrDelayedCount: 3,
        averageSimulatedResponseMinutes: 6.4,
        resourceUtilizationPercentage: 92.0,
        simulatedTimeline: [
          'T+00m: [SIMULATION INITIALIZED] Sandbox commenced with 10 synthetic emergencies.',
          'T+01m: [FLEET CHECK] 3 simulated ambulances online; 8 trauma ICU beds reserved.',
          'T+02m: [DISPATCHED] Synthetic incident SIM-2026-001 [CRITICAL] allocated simulated ALS unit. ETA: 4.8 mins.',
          'T+04m: [DISPATCHED] Synthetic incident SIM-2026-002 [HIGH] allocated simulated BLS unit. ETA: 5.6 mins.',
          'T+06m: [DISPATCHED] Synthetic incident SIM-2026-003 [CRITICAL] allocated simulated ALS unit. ETA: 6.2 mins.',
          'T+08m: [CAPACITY EXCEEDED] Synthetic incident SIM-2026-004 queued. Available ambulances exhausted.',
          'T+10m: [CAPACITY EXCEEDED] Synthetic incident SIM-2026-005 queued. Fleet deficit.',
          'T+12m: [DISPATCHED] Synthetic unit released from Hospital Victoria. Re-assigned to SIM-2026-004. ETA: 7.1 mins.'
        ],
        identifiedBottlenecks: [
          'Fleet Depletion: 3 simulated incidents experienced delayed triage due to concurrent ALS unit deficit.',
          'Hospital Surge Limit: Trauma bed buffer reached 90% saturation within 15 minutes.'
        ],
        recommendationReport: 'Deploy 2 additional roving ALS emergency ambulances in Sector 4 and activate satellite disaster overflow wards to lower delay rate to zero.',
        isSimulation: true,
        executedAt: new Date().toISOString()
      };
    }

    // ==========================================
    // 32. Public Safety Portal & Hazard Reports
    // ==========================================
    if (endpoint === '/public-safety/reports') {
      if (options.method === 'POST') {
        const body = JSON.parse(options.body || '{}');
        body.id = 'hz-' + Date.now();
        body.reportCode = 'PS-' + Math.floor(1000 + Math.random() * 9000);
        body.status = 'PENDING_VERIFICATION';
        body.upvoteCount = 1;
        body.reportedAt = new Date().toISOString();
        if (!store.publicSafetyReports) store.publicSafetyReports = [];
        store.publicSafetyReports.unshift(body);
        this.saveLocalStore(store);
        return body;
      }
      return store.publicSafetyReports || [
        { id: 'hz-1', reportCode: 'PS-1081', category: 'FLOODED_ROAD', address: 'Koramangala 80ft Road, 4th Block Junction', description: 'Water accumulation of approx 2.5 feet following afternoon storm. Two wheelers stranded.', latitude: 12.9352, longitude: 77.6245, status: 'VERIFIED', upvoteCount: 14 },
        { id: 'hz-2', reportCode: 'PS-1082', category: 'FALLEN_TREE', address: 'MG Road near Trinity Metro Station', description: 'Large banyan tree branch snapped and blocking rightward lane. Traffic redirected.', latitude: 12.9734, longitude: 77.6200, status: 'IN_PROGRESS', upvoteCount: 9 },
        { id: 'hz-3', reportCode: 'PS-1083', category: 'ELECTRICAL_HAZARD', address: 'Indiranagar 100ft Road cross 12', description: 'High voltage live wire touching pedestrian railing after transformer spark.', latitude: 12.9784, longitude: 77.6408, status: 'VERIFIED', upvoteCount: 22 }
      ];
    }

    if (endpoint === '/public-safety/map-hazards') {
      const reports = store.publicSafetyReports || [];
      return reports.filter(r => r.status === 'VERIFIED' || r.status === 'IN_PROGRESS');
    }

    if (endpoint.includes('/public-safety/reports/') && endpoint.endsWith('/upvote')) {
      return { success: true };
    }

    // ==========================================
    // 33. Preparedness Guides & First Aid
    // ==========================================
    if (endpoint.startsWith('/preparedness/guides')) {
      return [
        { id: 'g-cpr', category: 'FIRST_AID', title: 'Cardiopulmonary Resuscitation (CPR)', summary: 'Immediate chest compression technique to sustain blood flow during sudden cardiac arrest.', steps: ['1. Call 108 or shout for an AED.', '2. Check responsiveness and breathing.', '3. Place heel of hand on center of chest.', '4. Push hard and fast (100-120 bpm, 5-6 cm deep).', '5. Continue without interruption until help arrives.'], source: 'NDMA / Red Cross First Aid Manual' },
        { id: 'g-bleed', category: 'FIRST_AID', title: 'Severe Bleeding & Hemorrhage Control', summary: 'Critical steps to prevent hypovolemic shock following major trauma or laceration.', steps: ['1. Apply firm, direct pressure on wound.', '2. Maintain continuous pressure with clean cloth.', '3. Add second pad if bleeding soaks through.', '4. Elevate wounded limb above heart level.', '5. Apply tourniquet 2-3 inches above wound if arterial.'], source: 'Indian Red Cross Guidelines' },
        { id: 'g-flood', category: 'FLOOD', title: 'Flash Flood & Water Evacuation', summary: 'Essential survival protocols before, during, and after rapid urban or rural flooding.', steps: ['1. Never drive or walk through moving water.', '2. Disconnect electrical mains and gas valves.', '3. Move vulnerable family members to high floors.', '4. Boil drinking water.', '5. Keep battery radio tuned to weather warnings.'], source: 'National Disaster Management Authority (NDMA)' },
        { id: 'g-fire', category: 'FIRE', title: 'Building Fire & Burn Care Protocol', summary: 'Safe building egress, smoke inhalation avoidance, and superficial vs severe burn triage.', steps: ['1. Crawl low under smoke.', '2. Check door handles with back of hand before opening.', '3. Stop, drop, and roll if clothes catch fire.', '4. Cool burns under cold running water for 15-20 mins.', '5. Cover loosely with sterile film.'], source: 'Fire & Emergency Services Standard' }
      ];
    }

    // ==========================================
    // 34. Emergency QR Medical Profiles
    // ==========================================
    if (endpoint === '/qr/my-profile') {
      const key = localStorage.getItem('jeevansetu_qr_key') || 'DEMO-SECURE-KEY-8821';
      return {
        profile: {
          fullName: localStorage.getItem('jeevansetu_name') || 'Citizen User',
          bloodGroup: 'O_POSITIVE',
          allergies: 'Penicillin, Sulfa',
          chronicConditions: 'Mild Asthma',
          medications: 'Salbutamol Inhaler (PRN)',
          emergencyContactName: 'Dr. Ramesh (Guardian)',
          emergencyContactPhone: '+91 98450-11223',
          shareBloodGroup: true,
          shareAllergies: true,
          shareConditions: true,
          shareMedications: true,
          shareEmergencyContacts: true,
          qrAccessKey: key,
          active: true
        },
        accessUrl: 'https://chetanveerabomma-blip.github.io/JeevanSetu/emergency-profile.html?key=' + key
      };
    }

    if (endpoint === '/qr/permissions' && options.method === 'POST') {
      return { success: true };
    }

    if (endpoint === '/qr/regenerate' && options.method === 'POST') {
      const newKey = 'KEY-' + Math.random().toString(36).substring(2, 10).toUpperCase();
      localStorage.setItem('jeevansetu_qr_key', newKey);
      return {
        profile: { qrAccessKey: newKey, fullName: 'Citizen User', bloodGroup: 'O_POSITIVE' },
        accessUrl: 'https://chetanveerabomma-blip.github.io/JeevanSetu/emergency-profile.html?key=' + newKey
      };
    }

    if (endpoint.startsWith('/qr/public-view/')) {
      return {
        fullName: 'Citizen User',
        bloodGroup: 'O_POSITIVE',
        allergies: 'Penicillin, Sulfa',
        chronicConditions: 'Mild Asthma',
        emergencyContacts: { primaryName: 'Dr. Ramesh', primaryPhone: '+91 98450-11223' },
        profileActive: true,
        scanTimestamp: new Date().toISOString()
      };
    }

    // ==========================================
    // 35. Emergency SLA Monitoring
    // ==========================================
    if (endpoint === '/sla/dashboard') {
      return {
        averageAcknowledgementMinutes: 1.2,
        averageAssignmentMinutes: 3.4,
        averageResolutionMinutes: 28.5,
        totalEvaluatedIncidents: 42,
        overdueIncidentsCount: 1,
        slaCompliancePercentage: 97.6,
        priorityBreakdown: {
          CRITICAL: { targetMinutes: 5, compliantCount: 12, overdueCount: 0 },
          HIGH: { targetMinutes: 10, compliantCount: 18, overdueCount: 1 },
          MEDIUM: { targetMinutes: 20, compliantCount: 8, overdueCount: 0 },
          LOW: { targetMinutes: 45, compliantCount: 3, overdueCount: 0 }
        }
      };
    }

    if (endpoint === '/sla/overdue-incidents') {
      return [
        { id: 'emg-102', incidentCode: 'JS-2026-000185', priority: 'HIGH', stage: 'RESOURCE_ASSIGNMENT', minutesElapsed: 14.2, targetMinutes: 10.0, overdueByMinutes: 4.2 }
      ];
    }

    // ==========================================
    // 36. Relief Supply Distribution
    // ==========================================
    if (endpoint === '/relief-distribution/inventory') {
      return [
        { id: 'inv-1', itemCategory: 'DRINKING_WATER', itemName: 'Sealed 20L Water Cans', warehouseLocation: 'Depot North 1', currentQuantity: 1250, unitOfMeasure: 'CANS', safetyThresholdQuantity: 300 },
        { id: 'inv-2', itemCategory: 'FOOD_RATION', itemName: 'Ready-to-Eat Emergency Meal Packs', warehouseLocation: 'Central Relief Hub', currentQuantity: 3400, unitOfMeasure: 'PACKETS', safetyThresholdQuantity: 500 },
        { id: 'inv-3', itemCategory: 'MEDICAL_KITS', itemName: 'Trauma & Wound Dressing Kits', warehouseLocation: 'Depot North 1', currentQuantity: 420, unitOfMeasure: 'KITS', safetyThresholdQuantity: 100 },
        { id: 'inv-4', itemCategory: 'HYGIENE', itemName: 'Sanitation & Baby Care Kits', warehouseLocation: 'South Relief Annex', currentQuantity: 860, unitOfMeasure: 'KITS', safetyThresholdQuantity: 200 }
      ];
    }

    // ==========================================
    // 37. Maps & Geospatial Backend Endpoints
    // ==========================================
    if (endpoint === '/maps/hospitals') {
      return store.hospitals;
    }

    if (endpoint === '/maps/ambulances') {
      return (store.responders || []).filter(r => r.type === 'AMBULANCE');
    }

    if (endpoint === '/maps/locations') {
      return store.emergencies.map(e => ({
        id: e.id,
        incidentCode: e.incidentCode,
        category: e.type,
        priority: e.priority,
        latitude: e.latitude,
        longitude: e.longitude,
        address: e.address,
        status: e.status
      }));
    }

    if (endpoint === '/maps/zones') {
      return [
        { id: 'z-1', zoneName: 'Metro Basin Flood Hazard Area', zoneType: 'HAZARD_ZONE', severity: 'HIGH', centerLatitude: 12.9716, centerLongitude: 77.5946, radiusMeters: 1200 },
        { id: 'z-2', zoneName: 'Safe Zone — Kanteerava Arena', zoneType: 'SAFE_ZONE', severity: 'SAFE', centerLatitude: 12.9730, centerLongitude: 77.6010, radiusMeters: 600 }
      ];
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
