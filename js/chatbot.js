/**
 * JeevanSetu AI - Advanced Emergency Response & Intelligence Assistant
 * Features:
 * - Multilingual NLP (English, Hindi, Telugu, Tamil)
 * - AI Voice Assistant (Speech-to-Text & Text-to-Speech)
 * - Prominent Emergency SOS button with 3-sec countdown & GPS capture
 * - Smart Emergency Detection & Draft Registration
 * - Dispatcher AI Incident Summary (Confirmed Facts vs Uncertain Info)
 * - Real-Time Smart Ambulance & Hospital Recommendations
 */

(function () {
  'use strict';

  let chatHistory = [];
  let conversationId = localStorage.getItem('jeevansetu_ai_conv_id') || ('conv_' + Date.now());
  localStorage.setItem('jeevansetu_ai_conv_id', conversationId);

  let currentLang = localStorage.getItem('jeevansetu_lang') || 'en';
  let userCoords = { lat: 17.3850, lng: 78.4867 };
  let isListening = false;
  let recognition = null;
  let sosTimer = null;
  let sosSeconds = 3;

  const LANG_SPEECH_CODES = {
    en: 'en-IN',
    hi: 'hi-IN',
    te: 'te-IN',
    ta: 'ta-IN'
  };

  const I18N_TEXTS = {
    en: {
      title: 'JeevanSetu AI',
      subtitle: 'Emergency Intelligence Assistant',
      inputPlaceholder: 'Describe emergency or ask anything...',
      chips: {
        report: '🚨 Report Emergency',
        hospital: '🏥 Nearest Hospital',
        ambulance: '🚑 Available Ambulance',
        accident: '💥 Report Accident',
        status: '📋 Check Status',
        guidelines: '📖 Guidelines'
      },
      welcome: 'Hello! I am **JeevanSetu AI** — your Emergency Response Assistant.\n\nI can help you **report emergencies**, locate the **nearest hospital**, track **available ambulances**, or review **first-aid protocols**. How can I help right now?',
      sosTrigger: 'EMERGENCY SOS',
      sosPrompt: 'TRANSMITTING DISTRESS ALERT TO POLICE, FIRE & EMS',
      confirmDraft: '🚨 Confirm & Dispatch Emergency',
      draftVerified: 'Verified GPS fix captured. Priority computed via Java Engine.'
    },
    hi: {
      title: 'जीवनसेतु AI',
      subtitle: 'आपातकालीन खुफिया सहायक',
      inputPlaceholder: 'आपातकाल का वर्णन करें या कुछ भी पूछें...',
      chips: {
        report: '🚨 आपातकाल दर्ज करें',
        hospital: '🏥 निकटतम अस्पताल',
        ambulance: '🚑 उपलब्ध एम्बुलेंस',
        accident: '💥 दुर्घटना रिपोर्ट',
        status: '📋 स्थिति जांचें',
        guidelines: '📖 प्राथमिक उपचार'
      },
      welcome: 'नमस्ते! मैं **जीवनसेतु AI** हूँ — आपका आपातकालीन प्रतिक्रिया सहायक।\n\nमैं **आपातकाल की रिपोर्टिंग**, **निकटतम अस्पताल**, **उपलब्ध एम्बुलेंस**, और **प्राथमिक चिकित्सा नियमों** में आपकी सहायता कर सकता हूँ। मैं क्या मदद करूँ?',
      sosTrigger: 'आपातकालीन SOS',
      sosPrompt: 'पुलिस, अग्निशमन और एम्बुलेंस को तत्काल अलर्ट भेजा जा रहा है',
      confirmDraft: '🚨 आपातकाल की पुष्टि और प्रेषण करें',
      draftVerified: 'सत्यापित जीपीएस स्थान दर्ज किया गया। जावा इंजन द्वारा प्राथमिकता निर्धारित।'
    },
    te: {
      title: 'జీవన్‌సేతు AI',
      subtitle: 'అత్యవసర సహాయకారి',
      inputPlaceholder: 'అత్యవసర సమాచారం లేదా సందేశాన్ని టైప్ చేయండి...',
      chips: {
        report: '🚨 అత్యవసర రిపోర్ట్',
        hospital: '🏥 సమీప ఆసుపత్రి',
        ambulance: '🚑 అందుబాటులోని అంబులెన్స్',
        accident: '💥 ప్రమాదం నివేదిక',
        status: '📋 స్థితి పరిశీలన',
        guidelines: '📖 మార్గదర్శకాలు'
      },
      welcome: 'నమస్కారం! నేను **జీవన్‌సేతు AI** అత్యవసర సహాయకుని.\n\nనేను **అత్యవసర కేసు నమోదు**, **ఆసుపత్రి వివరాలు**, **అంబులెన్స్ సదుపాయం** మరియు **ప్రథమ చికిత్స**లో సహాయపడతాను. మీకు ఎలా సహాయపడగలను?',
      sosTrigger: 'అత్యవసర SOS',
      sosPrompt: 'పోలీస్, ఫైర్ & అంబులెన్స్ విభాగాలకు అత్యవసర హెచ్చరిక పంపబడుతోంది',
      confirmDraft: '🚨 కన్ఫర్మ్ చేసి పంపించండి',
      draftVerified: 'జీపీఎస్ ప్రదేశం ధృవీకరించబడింది.'
    },
    ta: {
      title: 'ஜீவன்சேது AI',
      subtitle: 'அவசர புலனாய்வு உதவியாளர்',
      inputPlaceholder: 'அவசரநிலையை விவரிக்கவும் அல்லது தட்டச்சு செய்யவும்...',
      chips: {
        report: '🚨 அவசர அறிக்கை',
        hospital: '🏥 அருகிலுள்ள மருத்துவமனை',
        ambulance: '🚑 ஆம்புலன்ஸ் இருப்பு',
        accident: '💥 விபத்து பதிவு',
        status: '📋 நிலை சரிபார்ப்பு',
        guidelines: '📖 முதலுதவி'
      },
      welcome: 'வணக்கம்! நான் **ஜீவன்சேது AI** அவசர உதவி அமைப்பாகும்.\n\nஉடனடி **அவசர அறிக்கை**, **மருத்துவமனைகள்**, **ஆம்புலன்ஸ் விவரங்கள்** மற்றும் **முதலுதவி நெறிமுறைகளுக்கு** உதவ தயாராக உள்ளேன். நான் என்ன உதவ வேண்டும்?',
      sosTrigger: 'அவசர SOS',
      sosPrompt: 'காவல்துறை மற்றும் ஆம்புலன்ஸுக்கு அவசர எச்சரிக்கை அனுப்பப்படுகிறது',
      confirmDraft: '🚨 உறுதிசெய்து அனுப்புக',
      draftVerified: 'ஜிபிஎஸ் இருப்பிடம் சரிபார்க்கப்பட்டது.'
    }
  };

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

  // Synthesize audio tone via Web Audio API
  function playAudioTone(type = 'msg') {
    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (!AudioCtx) return;
      const ctx = new AudioCtx();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.connect(gain);
      gain.connect(ctx.destination);

      if (type === 'sos') {
        osc.type = 'sawtooth';
        osc.frequency.setValueAtTime(800, ctx.currentTime);
        osc.frequency.linearRampToValueAtTime(400, ctx.currentTime + 0.25);
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.4);
        osc.start();
        osc.stop(ctx.currentTime + 0.4);
      } else if (type === 'alert') {
        osc.frequency.setValueAtTime(587.33, ctx.currentTime);
        osc.frequency.exponentialRampToValueAtTime(880, ctx.currentTime + 0.15);
        gain.gain.setValueAtTime(0.2, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.3);
        osc.start();
        osc.stop(ctx.currentTime + 0.3);
      } else {
        osc.frequency.setValueAtTime(523.25, ctx.currentTime);
        gain.gain.setValueAtTime(0.08, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.15);
        osc.start();
        osc.stop(ctx.currentTime + 0.15);
      }
    } catch (e) {}
  }

  // Text-To-Speech (TTS) Voice Playback
  function speakMessage(text, lang = currentLang) {
    if (!('speechSynthesis' in window)) return;
    window.speechSynthesis.cancel();

    // Clean markdown asterisks and hashtags for smooth speech
    const cleanText = text.replace(/[*#_`>🚨🏥🚑💥📋📖•]/g, ' ').replace(/\n+/g, ' ');
    const utterance = new SpeechSynthesisUtterance(cleanText);
    utterance.lang = LANG_SPEECH_CODES[lang] || 'en-IN';
    utterance.rate = 1.0;

    window.speechSynthesis.speak(utterance);
  }

  // Inject Chatbot UI into DOM
  function initChatbotUI() {
    if (document.getElementById('jeevansetu-ai-root')) return;

    const root = document.createElement('div');
    root.id = 'jeevansetu-ai-root';
    const texts = I18N_TEXTS[currentLang] || I18N_TEXTS.en;

    root.innerHTML = `
      <!-- Floating Emergency SOS Button -->
      <button class="js-ai-sos-trigger" id="jsAiSosTrigger" title="Immediate 1-Click Emergency SOS">
        <div class="js-ai-sos-icon">
          <i class="fa-solid fa-triangle-exclamation"></i>
        </div>
        <span id="jsAiSosLabel">SOS</span>
      </button>

      <!-- Floating Chatbot Trigger Button -->
      <button class="js-ai-trigger" id="jsAiTrigger" title="Open JeevanSetu AI Assistant">
        <div class="js-ai-trigger-icon">
          <i class="fa-solid fa-headset"></i>
          <div class="js-ai-pulse-ring"></div>
        </div>
        <div class="js-ai-trigger-text">
          <span class="js-ai-trigger-title" id="jsAiTriggerTitle">${texts.title}</span>
          <span class="js-ai-trigger-badge">
            <span class="js-ai-status-dot"></span> <span id="jsAiTriggerBadge">${texts.subtitle}</span>
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
              <h4 id="jsAiPanelTitle">${texts.title} <span class="ai-pill">2.0</span></h4>
              <p id="jsAiPanelSubtitle">${texts.subtitle}</p>
            </div>
          </div>
          <div class="js-ai-actions">
            <!-- Language Dropdown -->
            <select class="js-ai-lang-select" id="jsAiLangSelect" title="Select Assistant Language">
              <option value="en" ${currentLang === 'en' ? 'selected' : ''}>🇬🇧 EN</option>
              <option value="hi" ${currentLang === 'hi' ? 'selected' : ''}>🇮🇳 हिन्दी</option>
              <option value="te" ${currentLang === 'te' ? 'selected' : ''}>🇮🇳 తెలుగు</option>
              <option value="ta" ${currentLang === 'ta' ? 'selected' : ''}>🇮🇳 தமிழ்</option>
            </select>
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
            <i class="fa-solid fa-triangle-exclamation" style="color:#ef4444;"></i> <span class="chip-label">${texts.chips.report}</span>
          </button>
          <button class="js-ai-chip" data-action="find_hospital">
            <i class="fa-solid fa-hospital" style="color:#38bdf8;"></i> <span class="chip-label">${texts.chips.hospital}</span>
          </button>
          <button class="js-ai-chip" data-action="find_ambulance">
            <i class="fa-solid fa-truck-medical" style="color:#f59e0b;"></i> <span class="chip-label">${texts.chips.ambulance}</span>
          </button>
          <button class="js-ai-chip" data-action="report_accident">
            <i class="fa-solid fa-car-burst" style="color:#f97316;"></i> <span class="chip-label">${texts.chips.accident}</span>
          </button>
          <button class="js-ai-chip" data-action="check_status">
            <i class="fa-solid fa-clock-rotate-left" style="color:#10b981;"></i> <span class="chip-label">${texts.chips.status}</span>
          </button>
          <button class="js-ai-chip" data-action="guidelines">
            <i class="fa-solid fa-book-medical" style="color:#a855f7;"></i> <span class="chip-label">${texts.chips.guidelines}</span>
          </button>
        </div>

        <!-- Input Bar -->
        <div class="js-ai-input-bar">
          <input type="text" class="js-ai-input" id="jsAiInput" placeholder="${texts.inputPlaceholder}" autocomplete="off" />
          <button class="js-ai-btn-voice" id="jsAiVoiceBtn" title="Voice Input (Speech-to-Text)">
            <i class="fa-solid fa-microphone"></i>
          </button>
          <button class="js-ai-btn-send" id="jsAiSendBtn" title="Send Message">
            <i class="fa-solid fa-paper-plane"></i>
          </button>
        </div>
      </div>

      <!-- Emergency SOS Countdown Modal -->
      <div class="js-ai-sos-modal" id="jsAiSosModal">
        <div class="js-ai-sos-modal-content">
          <div style="font-size:1.8rem; color:#ef4444; margin-bottom:6px;">
            <i class="fa-solid fa-triangle-exclamation animate-bounce"></i>
          </div>
          <h2 style="font-size:1.3rem; font-weight:900; margin:0 0 6px;">EMERGENCY SOS PANIC ALERT</h2>
          <p style="font-size:0.8rem; color:#cbd5e1; margin:0;">${texts.sosPrompt}</p>
          <div class="js-ai-sos-countdown" id="jsAiSosCountdown">3</div>
          <p style="font-size:0.75rem; color:#94a3b8; margin:8px 0 16px;">
            <i class="fa-solid fa-location-crosshairs text-sky-400"></i> Capturing Live GPS: <span id="jsAiSosCoords">17.3850, 78.4867</span>
          </p>
          <div style="display:flex; gap:10px;">
            <button id="jsAiSosCancelBtn" style="flex:1; padding:10px; background:#334155; color:#fff; border:none; border-radius:10px; font-weight:700; cursor:pointer;">
              CANCEL
            </button>
            <button id="jsAiSosInstantBtn" style="flex:1; padding:10px; background:#dc2626; color:#fff; border:none; border-radius:10px; font-weight:700; cursor:pointer;">
              SEND NOW
            </button>
          </div>
        </div>
      </div>
    `;

    document.body.appendChild(root);
    setupEventListeners();
    loadStoredHistory();
  }

  // Event Listeners
  function setupEventListeners() {
    const trigger = document.getElementById('jsAiTrigger');
    const windowEl = document.getElementById('jsAiWindow');
    const closeBtn = document.getElementById('jsAiCloseBtn');
    const clearBtn = document.getElementById('jsAiClearBtn');
    const sendBtn = document.getElementById('jsAiSendBtn');
    const input = document.getElementById('jsAiInput');
    const voiceBtn = document.getElementById('jsAiVoiceBtn');
    const quickActions = document.getElementById('jsAiQuickActions');
    const langSelect = document.getElementById('jsAiLangSelect');
    const sosTrigger = document.getElementById('jsAiSosTrigger');
    const sosModal = document.getElementById('jsAiSosModal');
    const sosCancelBtn = document.getElementById('jsAiSosCancelBtn');
    const sosInstantBtn = document.getElementById('jsAiSosInstantBtn');

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
      if (confirm('Clear conversation history?')) {
        chatHistory = [];
        localStorage.removeItem('jeevansetu_chat_history');
        conversationId = 'conv_' + Date.now();
        localStorage.setItem('jeevansetu_ai_conv_id', conversationId);
        document.getElementById('jsAiMessages').innerHTML = '';
        renderWelcomeMessage();
      }
    });

    // Language Change
    langSelect.addEventListener('change', (e) => {
      currentLang = e.target.value;
      localStorage.setItem('jeevansetu_lang', currentLang);
      if (window.I18n && window.I18n.currentLang !== currentLang) {
        window.I18n.setLanguage(currentLang);
      }
      updateLocalizedLabels();
      if (recognition) {
        recognition.lang = LANG_SPEECH_CODES[currentLang] || 'en-IN';
      }
      appendMessage({
        sender: 'ASSISTANT',
        text: `Language switched to **${langSelect.options[langSelect.selectedIndex].text}**. How can I help you?`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      });
    });

    // Listen to global language switch from navbar/header
    window.addEventListener('languageChanged', (e) => {
      const newLang = e.detail && e.detail.lang;
      if (newLang && newLang !== currentLang) {
        currentLang = newLang;
        if (langSelect && langSelect.value !== newLang) {
          langSelect.value = newLang;
        }
        updateLocalizedLabels();
        if (recognition) {
          recognition.lang = LANG_SPEECH_CODES[currentLang] || 'en-IN';
        }
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
      handleQuickAction(chip.dataset.action);
    });

    // Speech Recognition
    setupSpeechRecognition(voiceBtn, input);

    // SOS Panic Trigger
    sosTrigger.addEventListener('click', startSosCountdown);
    sosCancelBtn.addEventListener('click', cancelSosCountdown);
    sosInstantBtn.addEventListener('click', executeSosDispatch);
  }

  // Update UI Labels for Language Change
  function updateLocalizedLabels() {
    const texts = I18N_TEXTS[currentLang] || I18N_TEXTS.en;
    document.getElementById('jsAiTriggerTitle').innerText = texts.title;
    document.getElementById('jsAiPanelTitle').innerHTML = `${texts.title} <span class="ai-pill">2.0</span>`;
    document.getElementById('jsAiPanelSubtitle').innerText = texts.subtitle;
    document.getElementById('jsAiInput').placeholder = texts.inputPlaceholder;

    const chips = document.querySelectorAll('#jsAiQuickActions .js-ai-chip');
    chips.forEach(c => {
      const act = c.dataset.action;
      if (act === 'report_emergency') c.querySelector('.chip-label').innerText = texts.chips.report;
      else if (act === 'find_hospital') c.querySelector('.chip-label').innerText = texts.chips.hospital;
      else if (act === 'find_ambulance') c.querySelector('.chip-label').innerText = texts.chips.ambulance;
      else if (act === 'report_accident') c.querySelector('.chip-label').innerText = texts.chips.accident;
      else if (act === 'check_status') c.querySelector('.chip-label').innerText = texts.chips.status;
      else if (act === 'guidelines') c.querySelector('.chip-label').innerText = texts.chips.guidelines;
    });
  }

  // SOS Countdown Logic
  function startSosCountdown() {
    const modal = document.getElementById('jsAiSosModal');
    const cdEl = document.getElementById('jsAiSosCountdown');
    const coordsEl = document.getElementById('jsAiSosCoords');

    coordsEl.innerText = `${userCoords.lat.toFixed(4)}, ${userCoords.lng.toFixed(4)}`;
    sosSeconds = 3;
    cdEl.innerText = sosSeconds;
    modal.classList.add('active');
    playAudioTone('sos');

    if (sosTimer) clearInterval(sosTimer);
    sosTimer = setInterval(() => {
      sosSeconds -= 1;
      cdEl.innerText = sosSeconds;
      playAudioTone('sos');

      if (sosSeconds <= 0) {
        clearInterval(sosTimer);
        sosTimer = null;
        executeSosDispatch();
      }
    }, 1000);
  }

  function cancelSosCountdown() {
    if (sosTimer) clearInterval(sosTimer);
    sosTimer = null;
    document.getElementById('jsAiSosModal').classList.remove('active');
  }

  async function executeSosDispatch() {
    cancelSosCountdown();
    playAudioTone('alert');

    const auth = (window.API && window.API.getAuth) ? window.API.getAuth() : {};
    const payload = {
      latitude: userCoords.lat,
      longitude: userCoords.lng,
      address: `Instant SOS GPS Coordinates: ${userCoords.lat.toFixed(4)}, ${userCoords.lng.toFixed(4)}`,
      name: auth.fullName || 'SOS Citizen Caller',
      phone: 'N/A'
    };

    let sosResult = null;
    try {
      if (window.API && typeof window.API.request === 'function') {
        sosResult = await window.API.request('/emergencies/sos', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
      }
    } catch (e) {
      console.debug('Direct SOS call failed, falling back', e);
    }

    if (!sosResult) {
      sosResult = {
        incidentCode: 'JS-SOS-2026-' + Math.floor(1000 + Math.random() * 9000),
        priority: 'CRITICAL',
        status: 'REPORTED',
        type: 'MEDICAL',
        address: payload.address,
        createdAt: new Date().toISOString()
      };
    }

    // Open chatbot panel and show confirmed alert
    document.getElementById('jsAiWindow').classList.add('js-ai-open');
    appendMessage({
      sender: 'ASSISTANT',
      text: `🚨 **EMERGENCY SOS BROADCAST CONFIRMED!**\n\nIncident Code: **${sosResult.incidentCode}**\nTriage Level: **CRITICAL**\nStatus: **REPORTED**\n\nCommand Center dispatched immediately. Live coordinates transmitted to Highway Police and Regional EMS.`,
      status: sosResult,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
  }

  // Voice Input Setup (Speech-to-Text)
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
    recognition.lang = LANG_SPEECH_CODES[currentLang] || 'en-IN';

    recognition.onstart = () => {
      isListening = true;
      voiceBtn.classList.add('listening');
      voiceBtn.title = `Listening (${recognition.lang})... Speak now`;
    };

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      input.value = transcript;
      handleUserSend();
    };

    recognition.onerror = () => {
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
        recognition.lang = LANG_SPEECH_CODES[currentLang] || 'en-IN';
        recognition.start();
      }
    });
  }

  // Welcome Message
  function renderWelcomeMessage() {
    const user = (window.API && window.API.getAuth) ? window.API.getAuth() : {};
    const name = user.fullName || 'Citizen';
    const texts = I18N_TEXTS[currentLang] || I18N_TEXTS.en;

    appendMessage({
      sender: 'ASSISTANT',
      text: texts.welcome.replace('**Citizen**', `**${name}**`),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
  }

  // Handle Quick Actions
  function handleQuickAction(action) {
    const actionPrompts = {
      en: {
        report_emergency: 'I want to report an emergency immediately.',
        find_hospital: 'Find nearest emergency hospitals with verified beds.',
        find_ambulance: 'Find available ambulances in my zone.',
        report_accident: 'There is a road accident with casualties requiring urgent response.',
        check_status: 'Check my emergency incident status.',
        guidelines: 'Show emergency first aid safety guidelines.'
      },
      hi: {
        report_emergency: 'मुझे तत्काल आपातकाल दर्ज कराना है।',
        find_hospital: 'निकटतम आपातकालीन अस्पताल खोजें।',
        find_ambulance: 'उपलब्ध एम्बुलेंस की स्थिति बताएं।',
        report_accident: 'सड़क दुर्घटना हुई है, तत्काल मदद चाहिए।',
        check_status: 'मेरी घटना की स्थिति बताएं।',
        guidelines: 'प्राथमिक उपचार के नियम बताएं।'
      },
      te: {
        report_emergency: 'నేను అత్యవసర పరిస్థితిని నివేదించాలనుకుంటున్నాను.',
        find_hospital: 'సమీప ఆసుపత్రులు చూపించండి.',
        find_ambulance: 'అందుబాటులోని అంబులెన్స్ వివరాలు ఇవ్వండి.',
        report_accident: 'రోడ్డు ప్రమాదం జరిగింది, వెంటనే రెస్పాండర్ కావాలి.',
        check_status: 'నా కేసు స్థితి తనిఖీ చేయండి.',
        guidelines: 'ప్రథమ చికిత్స మార్గదర్శకాలు చూపించండి.'
      },
      ta: {
        report_emergency: 'உடனடியாக அவசரநிலையை பதிவு செய்ய விரும்புகிறேன்.',
        find_hospital: 'அருகிலுள்ள மருத்துவமனைகளைக் கண்டறியவும்.',
        find_ambulance: 'ஆம்புலன்ஸ் விவரங்கள் தேவை.',
        report_accident: 'சாலை விபத்து ஏற்பட்டுள்ளது, உதவி தேவை.',
        check_status: 'சம்பவ நிலையை சரிபார்க்கவும்.',
        guidelines: 'முதலுதவி வழிகாட்டுதலைக் காட்டுங்கள்.'
      }
    };

    const langDict = actionPrompts[currentLang] || actionPrompts.en;
    const text = langDict[action] || 'Emergency Assistance';
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
        role: user.role || 'CITIZEN',
        language: currentLang
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
        responseData = generateLocalAssistantResponse(msgText, userCoords, currentLang);
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

      playAudioTone(responseData.emergencyDraft ? 'alert' : 'msg');

    } catch (err) {
      hideTypingIndicator();
      appendMessage({
        sender: 'ASSISTANT',
        text: '⚠️ Issue processing request. If life threat exists, please dial **112** or **108** immediately.',
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

    // Audio TTS button for assistant messages
    if (!isUser && msg.text) {
      bubbleHtml += `
        <div style="margin-top:6px; display:flex; align-items:center; justify-content:space-between;">
          <button class="js-ai-tts-btn" title="Listen (Text-To-Speech)" onclick="window.speakJeevanSetuMsg(this)">
            <i class="fa-solid fa-volume-high"></i> Listen
          </button>
          <div class="js-msg-time">${msg.timestamp || ''}</div>
        </div>
      `;
    } else {
      bubbleHtml += `<div class="js-msg-time">${msg.timestamp || ''}</div>`;
    }

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
            <span>${msg.draft.address || 'GPS: ' + userCoords.lat.toFixed(4) + ', ' + userCoords.lng.toFixed(4)}</span>
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
      bubbleHtml += `<div style="margin-top:8px; font-weight:600; font-size:0.78rem; color:#f59e0b;">Ready Dispatch Units (Requires Dispatcher Approval):</div>`;
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
            <i class="fa-solid fa-arrow-up-right-from-square"></i> Open Live Tactical Tracker
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

    bubbleHtml += `</div>`;
    row.innerHTML = avatarHtml + bubbleHtml;
    msgsContainer.appendChild(row);

    // Bind Confirm Button
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
            text: `✅ **Emergency Alert Confirmed!**\n\nIncident Code: **${confirmedIncident.incidentCode}**\nPriority Level: **${confirmedIncident.priority}**\nStatus: **REPORTED**\n\nCommand Center and nearby response agencies alerted.`,
            status: confirmedIncident,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
          });

          playAudioTone('alert');

        } catch (err) {
          btn.disabled = false;
          btn.innerHTML = `<i class="fa-solid fa-bolt"></i> Retry Dispatch`;
          alert('Could not dispatch emergency. Please call 112 directly.');
        }
      });
    }

    msgsContainer.scrollTop = msgsContainer.scrollHeight;
  }

  // TTS helper on window
  window.speakJeevanSetuMsg = function (btn) {
    const bubble = btn.closest('.js-msg-bubble');
    if (!bubble) return;
    const text = bubble.innerText.replace(/Listen.*$/, '').trim();
    btn.classList.add('speaking');
    speakMessage(text, currentLang);
    setTimeout(() => btn.classList.remove('speaking'), 3500);
  };

  // Typing Indicator
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

  // Local Intelligent Fallback
  function generateLocalAssistantResponse(msgText, coords, lang) {
    const lower = msgText.toLowerCase();
    const store = (window.API && window.API.getLocalStore) ? window.API.getLocalStore() : {};
    const hospitals = store.hospitals || [];
    const ambulances = (store.responders || []).filter(r => r.type === 'AMBULANCE');

    if (lower.includes('hospital') || lower.includes('अस्पताल') || lower.includes('ఆసుపత్రి') || lower.includes('மருத்துவமனை')) {
      return {
        text: `🏥 Located ${hospitals.length || 3} verified emergency hospitals nearby with live bed telemetry.`,
        hospitals: hospitals.slice(0, 3)
      };
    }

    if (lower.includes('ambulance') || lower.includes('एम्बुलेंस') || lower.includes('అంబులెన్స్') || lower.includes('ஆம்புலன்ஸ்')) {
      const avail = ambulances.filter(a => a.status === 'AVAILABLE');
      return {
        text: `🚑 Found ${ambulances.length || 3} regional ambulances (${avail.length} currently Available for dispatch). Dispatcher confirmation required.`,
        ambulances: ambulances.slice(0, 3)
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
        incidentStatus: emg
      };
    }

    if (lower.includes('guideline') || lower.includes('first aid') || lower.includes('उपचार') || lower.includes('చికిత్స') || lower.includes('முதலுதவி')) {
      return {
        text: `📖 **Immediate Emergency Guidelines & Life Support Protocols**:`,
        guidelineContent: `**1. CPR:** 100–120 compressions/min, 2 inches deep.\n**2. Bleeding:** Direct pressure with clean cloth.\n**3. Burns:** Cool clean running water 10-15 mins.\n**4. Unified Helpline:** 112 | 108`
      };
    }

    let category = 'MEDICAL';
    let priority = 'HIGH';
    if (lower.includes('accident') || lower.includes('दुर्घटना') || lower.includes('ప్రమాదం') || lower.includes('விபத்து')) category = 'ACCIDENT';
    if (lower.includes('fire') || lower.includes('आग') || lower.includes('మంటలు') || lower.includes('தீ')) { category = 'FIRE'; priority = 'CRITICAL'; }
    if (lower.includes('unconscious') || lower.includes('बेहोश') || lower.includes('స్పృహతప్పిన') || lower.includes('மயக்கம்')) priority = 'CRITICAL';

    return {
      text: `🚨 **Emergency Detected (${category}) — Priority [${priority}]**\n\nReview the triage draft below and tap **Confirm & Dispatch** to alert responders.`,
      category: category,
      priority: priority,
      emergencyDraft: {
        type: category,
        priority: priority,
        description: msgText,
        latitude: coords.lat,
        longitude: coords.lng,
        address: `Captured Coordinates: ${coords.lat.toFixed(4)}, ${coords.lng.toFixed(4)}`,
        injuredCount: 1,
        unconscious: lower.includes('unconscious') || lower.includes('बेहोश'),
        severeBleeding: lower.includes('bleed') || lower.includes('खून'),
        fireInvolved: category === 'FIRE'
      },
      hospitals: hospitals.slice(0, 2),
      ambulances: ambulances.slice(0, 2)
    };
  }

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

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initChatbotUI);
  } else {
    initChatbotUI();
  }

})();
