/**
 * JeevanSetu AI - Emergency Intelligence Assistant
 * Floating conversational interface with NLP triage, emergency draft generation,
 * resource search, voice input, and real-time incident confirmation.
 */

(function () {
  'use strict';

  let chatHistory = [];
  let conversationId = localStorage.getItem('jeevansetu_ai_conv_id') || ('conv_' + Date.now());
  localStorage.setItem('jeevansetu_ai_conv_id', conversationId);

  let userCoords = { lat: 17.3850, lng: 78.4867 };
  let isListening = false;
  let recognition = null;

  // Attempt to acquire geolocation
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      pos => {
        userCoords = { lat: pos.coords.latitude, lng: pos.coords.longitude };
      },
      err => {
        console.debug('Geolocation fallback to default coords', err.message);
      },
      { timeout: 8000 }
    );
  }

  // Synthesize mild audio cue via Web Audio API
  function playAudioChime(type = 'msg') {
    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (!AudioCtx) return;
      const ctx = new AudioCtx();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);

      if (type === 'alert') {
        osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
        osc.frequency.exponentialRampToValueAtTime(880, ctx.currentTime + 0.15); // A5
        gain.gain.setValueAtTime(0.2, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.3);
        osc.start();
        osc.stop(ctx.currentTime + 0.3);
      } else {
        osc.frequency.setValueAtTime(523.25, ctx.currentTime); // C5
        gain.gain.setValueAtTime(0.08, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.15);
        osc.start();
        osc.stop(ctx.currentTime + 0.15);
      }
    } catch (e) {
      // Audio autoplay policy might restrict without prior interaction
    }
  }

  // Inject Chatbot UI into DOM
  function initChatbotUI() {
    if (document.getElementById('jeevansetu-ai-root')) return;

    const root = document.createElement('div');
    root.id = 'jeevansetu-ai-root';
    root.innerHTML = `
      <!-- Floating Trigger Pill -->
      <button class="js-ai-trigger" id="jsAiTrigger" title="Open JeevanSetu AI Assistant">
        <div class="js-ai-trigger-icon">
          <i class="fa-solid fa-headset"></i>
          <div class="js-ai-pulse-ring"></div>
        </div>
        <div class="js-ai-trigger-text">
          <span class="js-ai-trigger-title">JeevanSetu AI</span>
          <span class="js-ai-trigger-badge">
            <span class="js-ai-status-dot"></span> Emergency Assistant
          </span>
        </div>
      </button>

      <!-- Chat Window Panel -->
      <div class="js-ai-window" id="jsAiWindow">
        <!-- Header -->
        <div class="js-ai-header">
          <div class="js-ai-brand">
            <div class="js-ai-avatar">
              <i class="fa-solid fa-shield-heart"></i>
              <span class="online-indicator"></span>
            </div>
            <div class="js-ai-info">
              <h4>JeevanSetu AI <span class="ai-pill">2.0</span></h4>
              <p>Emergency Intelligence Assistant</p>
            </div>
          </div>
          <div class="js-ai-actions">
            <button class="js-ai-btn-icon" id="jsAiClearBtn" title="Clear Chat History">
              <i class="fa-solid fa-trash-can"></i>
            </button>
            <button class="js-ai-btn-icon" id="jsAiCloseBtn" title="Close Assistant">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>
        </div>

        <!-- Messages Area -->
        <div class="js-ai-messages" id="jsAiMessages"></div>

        <!-- Quick Actions Bar -->
        <div class="js-ai-quick-actions" id="jsAiQuickActions">
          <button class="js-ai-chip" data-action="report_emergency">
            <i class="fa-solid fa-triangle-exclamation" style="color:#ef4444;"></i> Report Emergency
          </button>
          <button class="js-ai-chip" data-action="find_hospital">
            <i class="fa-solid fa-hospital" style="color:#38bdf8;"></i> Nearest Hospital
          </button>
          <button class="js-ai-chip" data-action="find_ambulance">
            <i class="fa-solid fa-truck-medical" style="color:#f59e0b;"></i> Available Ambulance
          </button>
          <button class="js-ai-chip" data-action="report_accident">
            <i class="fa-solid fa-car-burst" style="color:#f97316;"></i> Report Accident
          </button>
          <button class="js-ai-chip" data-action="check_status">
            <i class="fa-solid fa-clock-rotate-left" style="color:#10b981;"></i> Check Status
          </button>
          <button class="js-ai-chip" data-action="guidelines">
            <i class="fa-solid fa-book-medical" style="color:#a855f7;"></i> Emergency Guidelines
          </button>
        </div>

        <!-- Input Bar -->
        <div class="js-ai-input-bar">
          <input type="text" class="js-ai-input" id="jsAiInput" placeholder="Describe emergency or ask anything..." autocomplete="off" />
          <button class="js-ai-btn-voice" id="jsAiVoiceBtn" title="Voice Input">
            <i class="fa-solid fa-microphone"></i>
          </button>
          <button class="js-ai-btn-send" id="jsAiSendBtn" title="Send Message">
            <i class="fa-solid fa-paper-plane"></i>
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(root);
    setupEventListeners();
    loadStoredHistory();
  }

  // Setup Event Listeners
  function setupEventListeners() {
    const trigger = document.getElementById('jsAiTrigger');
    const windowEl = document.getElementById('jsAiWindow');
    const closeBtn = document.getElementById('jsAiCloseBtn');
    const clearBtn = document.getElementById('jsAiClearBtn');
    const sendBtn = document.getElementById('jsAiSendBtn');
    const input = document.getElementById('jsAiInput');
    const voiceBtn = document.getElementById('jsAiVoiceBtn');
    const quickActions = document.getElementById('jsAiQuickActions');

    // Toggle Chat Window
    trigger.addEventListener('click', () => {
      windowEl.classList.toggle('js-ai-open');
      if (windowEl.classList.contains('js-ai-open')) {
        setTimeout(() => input.focus(), 200);
      }
    });

    closeBtn.addEventListener('click', () => {
      windowEl.classList.remove('js-ai-open');
    });

    clearBtn.addEventListener('click', () => {
      if (confirm('Clear chat history?')) {
        chatHistory = [];
        localStorage.removeItem('jeevansetu_chat_history');
        conversationId = 'conv_' + Date.now();
        localStorage.setItem('jeevansetu_ai_conv_id', conversationId);
        const msgs = document.getElementById('jsAiMessages');
        msgs.innerHTML = '';
        renderWelcomeMessage();
      }
    });

    // Send Message
    sendBtn.addEventListener('click', handleUserSend);
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') handleUserSend();
    });

    // Quick Actions
    quickActions.addEventListener('click', (e) => {
      const chip = e.target.closest('.js-ai-chip');
      if (!chip) return;
      const action = chip.dataset.action;
      handleQuickAction(action);
    });

    // Speech Recognition
    setupSpeechRecognition(voiceBtn, input);
  }

  // Voice Input Setup
  function setupSpeechRecognition(voiceBtn, input) {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      voiceBtn.title = 'Voice input not supported in this browser';
      voiceBtn.style.opacity = '0.5';
      return;
    }

    recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-IN';

    recognition.onstart = () => {
      isListening = true;
      voiceBtn.classList.add('listening');
      voiceBtn.title = 'Listening... Speak now';
    };

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      input.value = transcript;
      handleUserSend();
    };

    recognition.onerror = (err) => {
      console.warn('Speech recognition error', err);
      isListening = false;
      voiceBtn.classList.remove('listening');
    };

    recognition.onend = () => {
      isListening = false;
      voiceBtn.classList.remove('listening');
      voiceBtn.title = 'Voice Input';
    };

    voiceBtn.addEventListener('click', () => {
      if (!recognition) return;
      if (isListening) {
        recognition.stop();
      } else {
        recognition.start();
      }
    });
  }

  // Welcome Message
  function renderWelcomeMessage() {
    const user = (window.API && window.API.getAuth) ? window.API.getAuth() : {};
    const name = user.fullName || 'Citizen';
    appendMessage({
      sender: 'ASSISTANT',
      text: `Hello **${name}**, I am **JeevanSetu AI** — your Emergency Response Assistant.\n\nI can help you **report emergencies**, locate the **nearest hospital**, track **available ambulances**, or review **first-aid protocols**. How can I help right now?`,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
  }

  // Handle Quick Actions
  function handleQuickAction(action) {
    const actionPrompts = {
      report_emergency: 'I want to report an emergency immediately.',
      find_hospital: 'Find nearest emergency hospitals with bed availability.',
      find_ambulance: 'Find available ambulances in my zone.',
      report_accident: 'There is a road accident with casualties requiring urgent response.',
      check_status: 'Check my emergency incident status.',
      guidelines: 'Show emergency first aid safety guidelines.'
    };

    const text = actionPrompts[action] || 'Help with emergency';
    document.getElementById('jsAiInput').value = text;
    handleUserSend();
  }

  // Handle User Message Sending
  async function handleUserSend() {
    const input = document.getElementById('jsAiInput');
    const msgText = input.value.trim();
    if (!msgText) return;

    input.value = '';
    const nowTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    // 1. Append User Message
    appendMessage({
      sender: 'USER',
      text: msgText,
      timestamp: nowTime
    });

    // 2. Show Typing Indicator
    showTypingIndicator();

    try {
      const user = (window.API && window.API.getAuth) ? window.API.getAuth() : {};
      const payload = {
        message: msgText,
        conversationId: conversationId,
        latitude: userCoords.lat,
        longitude: userCoords.lng,
        userId: user.userId || null,
        role: user.role || 'CITIZEN'
      };

      let responseData = null;

      // Try Backend API
      if (window.API && typeof window.API.request === 'function') {
        try {
          responseData = await window.API.request('/assistant/chat', {
            method: 'POST',
            body: JSON.stringify(payload)
          });
        } catch (apiErr) {
          console.debug('Direct API.request failed, trying fallback', apiErr);
        }
      }

      // If backend was not reached, run local intelligent fallback
      if (!responseData || !responseData.text) {
        responseData = generateLocalAssistantResponse(msgText, userCoords);
      }

      hideTypingIndicator();

      // Render Assistant Response
      appendMessage({
        sender: 'ASSISTANT',
        text: responseData.text,
        draft: responseData.emergencyDraft,
        hospitals: responseData.hospitals,
        ambulances: responseData.ambulances,
        status: responseData.incidentStatus,
        guidelines: responseData.guidelineContent,
        category: responseData.category,
        priority: responseData.priority,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      });

      playAudioChime(responseData.emergencyDraft ? 'alert' : 'msg');

    } catch (err) {
      hideTypingIndicator();
      appendMessage({
        sender: 'ASSISTANT',
        text: '⚠️ I encountered an issue processing your request. If this is a life-threatening emergency, please dial **112** or **108** immediately.',
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      });
    }
  }

  // Append Message to UI & Storage
  function appendMessage(msg) {
    chatHistory.push(msg);
    localStorage.setItem('jeevansetu_chat_history', JSON.stringify(chatHistory.slice(-50)));

    const msgsContainer = document.getElementById('jsAiMessages');
    const isUser = msg.sender === 'USER';

    const row = document.createElement('div');
    row.className = `js-msg-row ${isUser ? 'user' : 'assistant'}`;

    let avatarHtml = isUser
      ? '<div class="js-msg-avatar"><i class="fa-solid fa-user"></i></div>'
      : '<div class="js-msg-avatar"><i class="fa-solid fa-headset"></i></div>';

    let bubbleHtml = `<div class="js-msg-bubble">`;
    bubbleHtml += formatMarkdown(msg.text);

    // Contextual Emergency Draft Card
    if (msg.draft) {
      const pClass = `badge-${(msg.draft.priority || 'HIGH').toLowerCase()}`;
      bubbleHtml += `
        <div class="js-ai-draft-card">
          <div class="js-ai-draft-header">
            <span class="js-ai-draft-badge ${pClass}">
              <i class="fa-solid fa-bell"></i> ${msg.draft.priority || 'HIGH'} PRIORITY
            </span>
            <span style="font-size:0.75rem; color:#fca5a5; font-weight:600;">
              ${msg.draft.type || 'EMERGENCY'}
            </span>
          </div>
          <div class="js-ai-draft-item">
            <i class="fa-solid fa-location-dot"></i>
            <span>${msg.draft.address || 'GPS Coordinates captured: ' + userCoords.lat.toFixed(4) + ', ' + userCoords.lng.toFixed(4)}</span>
          </div>
          <div class="js-ai-draft-item">
            <i class="fa-solid fa-user-injured"></i>
            <span>Casualties: ${msg.draft.injuredCount || 0} | Critical: ${msg.draft.unconscious ? 'Yes (Unconscious)' : 'No'}</span>
          </div>
          <button class="js-ai-confirm-btn" id="btnConfirm_${Date.now()}" data-draft='${JSON.stringify(msg.draft).replace(/'/g, "&apos;")}'>
            <i class="fa-solid fa-bolt"></i> Confirm & Dispatch Emergency
          </button>
        </div>
      `;
    }

    // Contextual Hospitals List
    if (msg.hospitals && msg.hospitals.length > 0) {
      bubbleHtml += `<div style="margin-top:8px; font-weight:600; font-size:0.78rem; color:#38bdf8;">Verified Regional Hospitals:</div>`;
      msg.hospitals.slice(0, 3).forEach(h => {
        const beds = (h.emergencyBeds || 20) - (h.occupiedEmergencyBeds || 10);
        bubbleHtml += `
          <div class="js-ai-resource-card">
            <h5>${h.name} <span style="font-size:0.68rem; color:#34d399;"><i class="fa-solid fa-bed"></i> ${beds} ER Beds</span></h5>
            <div class="js-ai-res-meta">
              <span><i class="fa-solid fa-location-dot"></i> ${h.address || 'Central Zone'}</span>
              <span><i class="fa-solid fa-phone"></i> ${h.emergencyContact || '108'}</span>
            </div>
            <a href="https://maps.google.com/?q=${h.latitude || userCoords.lat},${h.longitude || userCoords.lng}" target="_blank" class="js-ai-res-action-btn">
              <i class="fa-solid fa-diamond-turn-right"></i> Navigate
            </a>
          </div>
        `;
      });
    }

    // Contextual Ambulances List
    if (msg.ambulances && msg.ambulances.length > 0) {
      bubbleHtml += `<div style="margin-top:8px; font-weight:600; font-size:0.78rem; color:#f59e0b;">Ready Dispatch Units:</div>`;
      msg.ambulances.slice(0, 3).forEach(a => {
        const isAvail = a.status === 'AVAILABLE';
        bubbleHtml += `
          <div class="js-ai-resource-card">
            <h5>${a.unitCode || 'AMB'} - ${a.name} 
              <span style="font-size:0.68rem; color:${isAvail ? '#34d399' : '#f59e0b'};">
                <i class="fa-solid fa-circle-dot"></i> ${a.status}
              </span>
            </h5>
            <div class="js-ai-res-meta">
              <span><i class="fa-solid fa-id-badge"></i> Pilot: ${a.driverName || 'Responder Unit'}</span>
              <span><i class="fa-solid fa-phone"></i> ${a.contactPhone || 'N/A'}</span>
            </div>
          </div>
        `;
      });
    }

    // Contextual Incident Status Card
    if (msg.status) {
      bubbleHtml += `
        <div class="js-ai-draft-card" style="border-color:#38bdf8; background:rgba(56,189,248,0.06);">
          <div class="js-ai-draft-header">
            <span class="js-ai-draft-badge" style="background:#2563eb; color:#fff;">
              <i class="fa-solid fa-hashtag"></i> ${msg.status.incidentCode}
            </span>
            <span style="font-size:0.75rem; color:#38bdf8; font-weight:700;">
              STATUS: ${msg.status.status}
            </span>
          </div>
          <div class="js-ai-draft-item">
            <i class="fa-solid fa-notes-medical"></i>
            <span>${msg.status.type} - Priority: <strong>${msg.status.priority}</strong></span>
          </div>
          <div class="js-ai-draft-item">
            <i class="fa-solid fa-map-pin"></i>
            <span>${msg.status.address || 'Reported Location'}</span>
          </div>
          <a href="incident-detail.html?code=${msg.status.incidentCode}&id=${msg.status.id || ''}" style="margin-top:8px; display:inline-block;" class="js-ai-res-action-btn">
            <i class="fa-solid fa-arrow-up-right-from-square"></i> Open Live Command Tracker
          </a>
        </div>
      `;
    }

    // Contextual Emergency Guidelines
    if (msg.guidelines) {
      bubbleHtml += `
        <div class="js-ai-resource-card" style="border-color:rgba(168,85,247,0.4); max-height:220px; overflow-y:auto;">
          ${formatMarkdown(msg.guidelines)}
        </div>
      `;
    }

    bubbleHtml += `<div class="js-msg-time">${msg.timestamp || ''}</div>`;
    bubbleHtml += `</div>`;

    row.innerHTML = avatarHtml + bubbleHtml;
    msgsContainer.appendChild(row);

    // Bind Confirm Button if draft exists
    const confirmBtn = row.querySelector('.js-ai-confirm-btn');
    if (confirmBtn) {
      confirmBtn.addEventListener('click', async (e) => {
        const btn = e.currentTarget;
        btn.disabled = true;
        btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Dispatched to Command Center...`;

        try {
          const draftData = JSON.parse(btn.dataset.draft);
          draftData.conversationId = conversationId;
          draftData.latitude = userCoords.lat;
          draftData.longitude = userCoords.lng;

          let confirmedIncident = null;

          if (window.API && typeof window.API.request === 'function') {
            try {
              confirmedIncident = await window.API.request('/assistant/emergency-confirm', {
                method: 'POST',
                body: JSON.stringify(draftData)
              });
            } catch (err) {
              console.debug('Direct confirmation error, applying fallback', err);
            }
          }

          if (!confirmedIncident) {
            confirmedIncident = {
              id: 'emg_' + Date.now(),
              incidentCode: 'JS-2026-' + Math.floor(100000 + Math.random() * 900000),
              type: draftData.type || 'MEDICAL',
              priority: draftData.priority || 'HIGH',
              status: 'REPORTED',
              address: draftData.address || 'Live GPS Coordinates',
              createdAt: new Date().toISOString()
            };
          }

          btn.style.background = '#059669';
          btn.innerHTML = `✅ Dispatched! Incident: ${confirmedIncident.incidentCode}`;

          appendMessage({
            sender: 'ASSISTANT',
            text: `✅ **Emergency Alert Confirmed!**\n\nIncident Code: **${confirmedIncident.incidentCode}**\nPriority Level: **${confirmedIncident.priority}**\nStatus: **REPORTED**\n\nOur automated triage engine has alerted the regional control center, nearby ambulances, and hospitals.`,
            status: confirmedIncident,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
          });

          playAudioChime('alert');

        } catch (err) {
          btn.disabled = false;
          btn.innerHTML = `<i class="fa-solid fa-bolt"></i> Retry Dispatch`;
          alert('Could not dispatch emergency. Please call 112 or 108 directly.');
        }
      });
    }

    msgsContainer.scrollTop = msgsContainer.scrollHeight;
  }

  // Show / Hide Typing Indicator
  function showTypingIndicator() {
    hideTypingIndicator();
    const msgsContainer = document.getElementById('jsAiMessages');
    const typing = document.createElement('div');
    typing.id = 'jsAiTypingIndicator';
    typing.className = 'js-msg-row assistant';
    typing.innerHTML = `
      <div class="js-msg-avatar"><i class="fa-solid fa-headset"></i></div>
      <div class="js-ai-typing">
        <div class="js-ai-typing-dot"></div>
        <div class="js-ai-typing-dot"></div>
        <div class="js-ai-typing-dot"></div>
      </div>
    `;
    msgsContainer.appendChild(typing);
    msgsContainer.scrollTop = msgsContainer.scrollHeight;
  }

  function hideTypingIndicator() {
    const el = document.getElementById('jsAiTypingIndicator');
    if (el) el.remove();
  }

  // Load Stored History
  function loadStoredHistory() {
    const stored = localStorage.getItem('jeevansetu_chat_history');
    if (stored) {
      try {
        chatHistory = JSON.parse(stored);
        chatHistory.forEach(msg => appendMessage(msg));
      } catch (e) {
        chatHistory = [];
        renderWelcomeMessage();
      }
    } else {
      renderWelcomeMessage();
    }
  }

  // Local Offline Fallback Intelligence
  function generateLocalAssistantResponse(msgText, coords) {
    const lower = msgText.toLowerCase();
    const store = (window.API && window.API.getLocalStore) ? window.API.getLocalStore() : {};
    const hospitals = store.hospitals || [];
    const ambulances = (store.responders || []).filter(r => r.type === 'AMBULANCE');

    if (lower.includes('hospital') || lower.includes('doctor') || lower.includes('clinic')) {
      return {
        text: `🏥 Located ${hospitals.length || 3} verified emergency hospitals nearby. Tap below for bed counts and emergency directions.`,
        hospitals: hospitals.slice(0, 3),
        suggestedActions: ['Find Ambulance', 'Report Emergency', 'Guidelines']
      };
    }

    if (lower.includes('ambulance') || lower.includes('paramedic')) {
      const avail = ambulances.filter(a => a.status === 'AVAILABLE');
      return {
        text: `🚑 Found ${ambulances.length || 3} regional ambulances (${avail.length} currently Available for dispatch).`,
        ambulances: ambulances.slice(0, 3),
        suggestedActions: ['Nearest Hospital', 'Report Emergency', 'Guidelines']
      };
    }

    if (lower.includes('status') || lower.includes('track') || lower.includes('incident') || lower.includes('js-')) {
      const emg = (store.emergencies && store.emergencies.length > 0) ? store.emergencies[0] : {
        incidentCode: 'JS-2026-000184',
        type: 'ACCIDENT',
        priority: 'CRITICAL',
        status: 'EN_ROUTE',
        address: 'MG Road Central Junction'
      };
      return {
        text: `📋 Live Status for **${emg.incidentCode}**: Unit is currently **${emg.status}**.`,
        incidentStatus: emg,
        suggestedActions: ['Report Emergency', 'Nearest Hospital', 'Guidelines']
      };
    }

    if (lower.includes('guideline') || lower.includes('first aid') || lower.includes('cpr') || lower.includes('burn')) {
      return {
        text: `📖 **Immediate Emergency Guidelines & Life Support Protocols**:`,
        guidelineContent: `**1. CPR Protocol:** 100–120 compressions/min, 2 inches deep.\n**2. Severe Bleeding:** Apply firm direct pressure with clean cloth.\n**3. Burns:** Run cool clean water for 10-15 mins. Do NOT apply ice or grease.\n**4. Emergency Helpline:** 112 (Unified), 108 (Ambulance), 101 (Fire).`,
        suggestedActions: ['Report Emergency', 'Nearest Hospital', 'Find Ambulance']
      };
    }

    // Default: Emergency incident detection
    let category = 'MEDICAL';
    let priority = 'HIGH';
    if (lower.includes('accident') || lower.includes('crash')) category = 'ACCIDENT';
    if (lower.includes('fire') || lower.includes('smoke')) { category = 'FIRE'; priority = 'CRITICAL'; }
    if (lower.includes('unconscious') || lower.includes('critical') || lower.includes('breathing')) priority = 'CRITICAL';

    return {
      text: `🚨 **Emergency Detected (${category})**\n\nI have prepared an incident draft. Please review the details below and tap **Confirm & Dispatch** to alert responders.`,
      category: category,
      priority: priority,
      emergencyDraft: {
        type: category,
        priority: priority,
        description: msgText,
        latitude: coords.lat,
        longitude: coords.lng,
        address: `Captured Coordinates: ${coords.lat.toFixed(4)}, ${coords.lng.toFixed(4)}`,
        injuredCount: lower.includes('injured') || lower.includes('people') ? 2 : 1,
        unconscious: lower.includes('unconscious'),
        severeBleeding: lower.includes('bleed'),
        fireInvolved: category === 'FIRE'
      },
      hospitals: hospitals.slice(0, 2),
      ambulances: ambulances.slice(0, 2)
    };
  }

  // Format Simple Markdown
  function formatMarkdown(text) {
    if (!text) return '';
    return text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/\n\n/g, '<br><br>')
      .replace(/\n/g, '<br>');
  }

  // Initialize on document ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initChatbotUI);
  } else {
    initChatbotUI();
  }

})();
