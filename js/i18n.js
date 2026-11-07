/**
 * JeevanSetu - Global Internationalization (i18n) Engine
 * Seamless multi-language switching across all pages & dashboards:
 * English (en), Hindi (hi), Telugu (te), Tamil (ta)
 */

(function () {
  'use strict';

  const TRANSLATIONS = {
    en: {
      lang_name: 'English',
      nav_home: 'Home',
      nav_how_it_works: 'How It Works',
      nav_services: 'Emergency Services',
      nav_command_center: 'Command Center',
      nav_command_grid: 'Command Grid',
      nav_analytics: 'Analytics',
      nav_login: 'LOGIN',
      nav_report_emergency: 'REPORT EMERGENCY',
      nav_logout: 'Logout',
      system_operational: 'SYSTEM OPERATIONAL',

      hero_badge: 'Emergency Response Network',
      hero_title_1: 'When every second matters,',
      hero_title_2: 'JeevanSetu connects help to you.',
      hero_subtitle: 'An intelligent emergency coordination platform connecting citizens, responders, hospitals and essential emergency resources.',
      hero_btn_emergency: '🚨 GET EMERGENCY HELP',
      hero_btn_explore: 'EXPLORE SERVICES',

      kpi_active_incidents: 'Active Incidents',
      kpi_critical_trauma: 'Critical Trauma',
      kpi_ambulances_ready: 'Ambulances Ready',
      kpi_police_fire: 'Police / Fire Units',
      kpi_icu_beds: 'ICU Beds Available',
      kpi_er_beds: 'ER Beds Available',
      kpi_avg_response: 'Avg Response Time',

      cc_priority_queue: 'Priority Triage Queue',
      cc_resource_engine: 'Resource Allocation Engine',
      cc_operational_stream: 'Operational Event Stream',
      cc_mark_resolved: 'Mark Incident Resolved',
      cc_dispatcher_summary: 'AI Dispatcher Incident Summary',
      cc_multi_agency: 'Multi-Agency Tactical Channel',
      cc_layer_visibility: 'Layer Visibility',
      cc_center_map: 'Center Map',
      cc_incident_room: 'Incident Room',
      cc_confirmed_facts: 'Confirmed Extracted Facts',
      cc_uncertain_info: 'Uncertain Information',

      portal_citizen: 'CITIZEN PORTAL',
      portal_responder: 'RESPONDER TERMINAL',
      portal_hospital: 'HOSPITAL TRAUMA CENTER',
      portal_bloodbank: 'BLOOD TRANSFUSION LOGISTICS',
      portal_shelter: 'CIVIL DISASTER & SHELTERS',
      portal_analytics: 'Emergency Analytics & Density Heatmap',

      citizen_sos_instant: 'INSTANT SOS BROADCAST',
      citizen_nearby_facilities: 'Nearby Emergency Facilities',
      citizen_guidelines: 'Emergency Life Support Guidelines',
      citizen_my_incidents: 'My Reported Incidents',
      citizen_report_modal_title: 'Report Emergency Incident',

      hosp_er_active: 'ER ACTIVE',
      hosp_icu_avail: 'Available ICU Beds',
      hosp_general_beds: 'General Emergency Beds',
      hosp_oxygen_ready: 'Oxygen Supply Ready',
      blood_stock_inventory: 'Blood Stock Inventory',
      shelter_active: 'Active Relief Shelters',

      login_portal_title: 'Portal Sign In',
      login_portal_subtitle: 'Access role-specific emergency dashboard and resources',
      login_email_label: 'Email / Phone',
      login_pass_label: 'Password',
      login_quick_roles: 'Quick Demo Portals (1-Click Switch)',

      btn_sos: 'EMERGENCY SOS',
      btn_submit: 'SUBMIT EMERGENCY',
      btn_cancel: 'Cancel',
      btn_confirm: 'Confirm & Dispatch',
      btn_navigate: 'Navigate',
      btn_broadcast: 'Broadcast Capacity',
      btn_dispatch_unit: 'Dispatch Unit',
      btn_refresh: 'Refresh',
      btn_sign_in: 'Sign In',

      status_available: 'AVAILABLE',
      status_busy: 'BUSY',
      status_en_route: 'EN ROUTE',
      status_arrived: 'ARRIVED',
      status_resolved: 'RESOLVED',
      status_reported: 'REPORTED',
      status_critical: 'CRITICAL',
      status_high: 'HIGH',
      status_medium: 'MEDIUM',
      status_low: 'LOW'
    },
    hi: {
      lang_name: 'हिन्दी',
      nav_home: 'होम',
      nav_how_it_works: 'यह कैसे काम करता है',
      nav_services: 'आपातकालीन सेवाएं',
      nav_command_center: 'कमांड सेंटर',
      nav_command_grid: 'कमांड ग्रिड',
      nav_analytics: 'एनालिटिक्स',
      nav_login: 'लॉगिन',
      nav_report_emergency: 'आपातकाल दर्ज करें',
      nav_logout: 'लॉगआउट',
      system_operational: 'सिस्टम सक्रिय है',

      hero_badge: 'आपातकालीन प्रतिक्रिया नेटवर्क',
      hero_title_1: 'जब हर सेकंड मायने रखता है,',
      hero_title_2: 'जीवनसेतु आप तक मदद पहुँचाता है।',
      hero_subtitle: 'नागरिकों, रिस्पॉन्डर्स, अस्पतालों और जीवन रक्षक संसाधनों को वास्तविक समय में जोड़ने वाला स्मार्ट आपातकालीन प्लेटफॉर्म।',
      hero_btn_emergency: '🚨 आपातकालीन सहायता पाएं',
      hero_btn_explore: 'सेवाओं की जानकारी',

      kpi_active_incidents: 'सक्रिय घटनाएं',
      kpi_critical_trauma: 'गंभीर मामले',
      kpi_ambulances_ready: 'एम्बुलेंस तैयार',
      kpi_police_fire: 'पुलिस / फायर यूनिट्स',
      kpi_icu_beds: 'उपलब्ध आईसीयू बेड',
      kpi_er_beds: 'उपलब्ध इमरजेंसी बेड',
      kpi_avg_response: 'औसत प्रतिक्रिया समय',

      cc_priority_queue: 'प्राथमिकता ट्राइएज कतार',
      cc_resource_engine: 'संसाधन आवंटन इंजन',
      cc_operational_stream: 'परिचालन इवेंट स्ट्रीम',
      cc_mark_resolved: 'घटना का समाधान चिह्नित करें',
      cc_dispatcher_summary: 'एआई डिस्पैचर सारांश',
      cc_multi_agency: 'बहु-एजेंसी सामरिक चैनल',
      cc_layer_visibility: 'नक्शा परतें',
      cc_center_map: 'नक्शा केंद्रित करें',
      cc_incident_room: 'घटना कक्ष',
      cc_confirmed_facts: 'पुष्ट तथ्य',
      cc_uncertain_info: 'अपुष्ट जानकारी (सत्यापन आवश्यक)',

      portal_citizen: 'नागरिक पोर्टल',
      portal_responder: 'रिस्पॉन्डर टर्मिनल',
      portal_hospital: 'अस्पताल ट्रॉमा सेंटर',
      portal_bloodbank: 'ब्लड बैंक लॉजिस्टिक्स',
      portal_shelter: 'आपदा प्रबंधन एवं आश्रय',
      portal_analytics: 'आपातकालीन एनालिटिक्स और हीटमैप',

      citizen_sos_instant: 'तत्काल SOS प्रसारण',
      citizen_nearby_facilities: 'निकटतम आपातकालीन सुविधाएं',
      citizen_guidelines: 'आपातकालीन जीवन रक्षा दिशानिर्देश',
      citizen_my_incidents: 'मेरी दर्ज की गई घटनाएं',
      citizen_report_modal_title: 'आपातकालीन घटना दर्ज करें',

      hosp_er_active: 'इमरजेंसी सक्रिय',
      hosp_icu_avail: 'उपलब्ध आईसीयू बेड',
      hosp_general_beds: 'सामान्य आपातकालीन बेड',
      hosp_oxygen_ready: 'ऑक्सीजन आपूर्ति तैयार',
      blood_stock_inventory: 'रक्त भंडार सूची',
      shelter_active: 'सक्रिय राहत शिविर',

      login_portal_title: 'पोर्टल साइन इन',
      login_portal_subtitle: 'भूमिका-विशिष्ट आपातकालीन डैशबोर्ड और संसाधनों तक पहुंचें',
      login_email_label: 'ईमेल / फोन',
      login_pass_label: 'पासवर्ड',
      login_quick_roles: 'त्वरित डेमो पोर्टल्स (एक-क्लिक स्विच)',

      btn_sos: 'आपातकालीन SOS',
      btn_submit: 'आपातकाल भेजें',
      btn_cancel: 'रद्द करें',
      btn_confirm: 'पुष्टि करें और भेजें',
      btn_navigate: 'दिशा-निर्देश',
      btn_broadcast: 'क्षमता अपडेट करें',
      btn_dispatch_unit: 'यूनिट भेजें',
      btn_refresh: 'रीफ्रेश',
      btn_sign_in: 'साइन इन करें',

      status_available: 'उपलब्ध',
      status_busy: 'व्यस्त',
      status_en_route: 'रास्ते में',
      status_arrived: 'पहुंच गया',
      status_resolved: 'हल हो गया',
      status_reported: 'दर्ज किया गया',
      status_critical: 'अत्यंत गंभीर',
      status_high: 'गंभीर',
      status_medium: 'मध्यम',
      status_low: 'सामान्य'
    },
    te: {
      lang_name: 'తెలుగు',
      nav_home: 'హోమ్',
      nav_how_it_works: 'ఎలా పనిచేస్తుంది',
      nav_services: 'అత్యవసర సేవలు',
      nav_command_center: 'కమాండ్ సెంటర్',
      nav_command_grid: 'కమాండ్ గ్రిడ్',
      nav_analytics: 'విశ్లేషణలు',
      nav_login: 'లాగిన్',
      nav_report_emergency: 'అత్యవసర రిపోర్ట్',
      nav_logout: 'లాగౌట్',
      system_operational: 'సిస్టమ్ పనిచేస్తోంది',

      hero_badge: 'ఎమర్జెన్సీ రెస్పాన్స్ నెట్‌వర్క్',
      hero_title_1: 'ప్రతి క్షణం అమూల్యమైనప్పుడు,',
      hero_title_2: 'జీవన్‌సేతు మీకు సాయాన్ని అందిస్తుంది.',
      hero_subtitle: 'పౌరులు, అత్యవసర రెస్పాండర్లు, ఆసుపత్రులు మరియు అత్యవసర వనరులను రియల్ టైంలో అనుసంధానించే అత్యాధునిక వేదిక.',
      hero_btn_emergency: '🚨 అత్యవసర సహాయం పొందండి',
      hero_btn_explore: 'సేవలను అన్వేషించండి',

      kpi_active_incidents: 'యాక్టివ్ కేసులు',
      kpi_critical_trauma: 'క్రిటికల్ కేసులు',
      kpi_ambulances_ready: 'అంబులెన్స్ సిద్ధం',
      kpi_police_fire: 'పోలీస్ / ఫైర్ యూనిట్లు',
      kpi_icu_beds: 'అందుబాటులో ఐసీయూ',
      kpi_er_beds: 'ఎమర్జెన్సీ బెడ్స్',
      kpi_avg_response: 'సగటు స్పందన సమయం',

      cc_priority_queue: 'ప్రాధాన్యత క్యూ',
      cc_resource_engine: 'వనరుల కేటాయింపు ఇంజిన్',
      cc_operational_stream: 'కార్యాచరణ ఈవెంట్ స్ట్రీమ్',
      cc_mark_resolved: 'పరిష్కరించబడింది',
      cc_dispatcher_summary: 'ఏఐ డిస్పాచర్ సారాంశం',
      cc_multi_agency: 'మల్టీ-ఏజెన్సీ ఛానల్',
      cc_layer_visibility: 'మ్యాప్ లేయర్లు',
      cc_center_map: 'మ్యాప్ కేంద్రీకరించు',
      cc_incident_room: 'కేసు రూమ్',
      cc_confirmed_facts: 'ధృవీకరించబడిన వాస్తవాలు',
      cc_uncertain_info: 'ధృవీకరించని సమాచారం',

      portal_citizen: 'పౌర పోర్టల్',
      portal_responder: 'రెస్పాండర్ టెర్మినల్',
      portal_hospital: 'హాస్పిటల్ ట్రామా సెంటర్',
      portal_bloodbank: 'బ్లడ్ బ్యాంక్ లాజిస్టిక్స్',
      portal_shelter: 'విపత్తు సహాయం & పునరావాసం',
      portal_analytics: 'ఎమర్జెన్సీ విశ్లేషణలు & హీట్‌మ్యాప్',

      citizen_sos_instant: 'తక్షణ SOS ప్రసారం',
      citizen_nearby_facilities: 'సమీప అత్యవసర కేంద్రాలు',
      citizen_guidelines: 'అత్యవసర జీవన రక్షణ మార్గదర్శకాలు',
      citizen_my_incidents: 'నేను రిపోర్ట్ చేసిన కేసులు',
      citizen_report_modal_title: 'అత్యవసర సంఘటనను నివేదించండి',

      hosp_er_active: 'ఈఆర్ యాక్టివ్',
      hosp_icu_avail: 'అందుబాటులో ఐసీయూ బెడ్స్',
      hosp_general_beds: 'జనరల్ ఎమర్జెన్సీ బెడ్స్',
      hosp_oxygen_ready: 'ఆక్సిజన్ నిల్వ సిద్ధం',
      blood_stock_inventory: 'రక్త నిల్వల జాబితా',
      shelter_active: 'సహాయ శిబిరాలు',

      login_portal_title: 'పోర్టల్ లాగిన్',
      login_portal_subtitle: 'అత్యవసర డాష్‌బోర్డ్ మరియు వనరులను యాక్సెస్ చేయండి',
      login_email_label: 'ఈమెయిల్ / ఫోన్',
      login_pass_label: 'పాస్‌వర్డ్',
      login_quick_roles: 'త్వరిత డెమో పోర్టల్స్',

      btn_sos: 'అత్యవసర SOS',
      btn_submit: 'సమర్పించండి',
      btn_cancel: 'రద్దు చేయండి',
      btn_confirm: 'ధృవీకరించి పంపండి',
      btn_navigate: 'దారి చూపు',
      btn_broadcast: 'బెడ్స్ అప్‌డేట్ చేయండి',
      btn_dispatch_unit: 'యూనిట్ పంపు',
      btn_refresh: 'రిఫ్రెష్',
      btn_sign_in: 'సైన్ ఇన్',

      status_available: 'అందుబాటులో ఉంది',
      status_busy: 'బిజీ',
      status_en_route: 'మార్గంలో ఉంది',
      status_arrived: 'చేరుకుంది',
      status_resolved: 'పరిష్కరించబడింది',
      status_reported: 'నమోదైంది',
      status_critical: 'చాలా తీవ్రం',
      status_high: 'తీవ్రం',
      status_medium: 'మధ్యస్థం',
      status_low: 'సాధారణ'
    },
    ta: {
      lang_name: 'தமிழ்',
      nav_home: 'முகப்பு',
      nav_how_it_works: 'செயல்படும் முறை',
      nav_services: 'அவசர சேவைகள்',
      nav_command_center: 'கட்டளை மையம்',
      nav_command_grid: 'கட்டளை கிரிட்',
      nav_analytics: 'பகுப்பாய்வு',
      nav_login: 'உள்நுழை',
      nav_report_emergency: 'அவசர அறிக்கை',
      nav_logout: 'வெளியேறு',
      system_operational: 'அமைப்பு செயல்பாட்டில் உள்ளது',

      hero_badge: 'அவசர உதவி நெட்வொர்க்',
      hero_title_1: 'ஒவ்வொரு நொடியும் மிக முக்கியம்,',
      hero_title_2: 'ஜீவன்சேது உங்களுக்கான உதவியை இணைக்கிறது.',
      hero_subtitle: 'பொதுமக்கள், மீட்புப் படைகள், மருத்துவமனைகள் மற்றும் உயிர்காக்கும் வளங்களை நிகழ்நேரத்தில் இணைக்கும் ஒருங்கிணைந்த தளம்.',
      hero_btn_emergency: '🚨 அவசர உதவி பெறுங்கள்',
      hero_btn_explore: 'சேவைகளை அறிக',

      kpi_active_incidents: 'நடப்பு சம்பவங்கள்',
      kpi_critical_trauma: 'தீவிர அவசரநிலை',
      kpi_ambulances_ready: 'தயார் ஆம்புலன்ஸ்',
      kpi_police_fire: 'காவல்துறை / தீயணைப்பு',
      kpi_icu_beds: 'ஐசியூ படுக்கைகள்',
      kpi_er_beds: 'அவசர படுக்கைகள்',
      kpi_avg_response: 'சராசரி பதிலளிப்பு நேரம்',

      cc_priority_queue: 'முன்னுரிமை வரிசை',
      cc_resource_engine: 'வள ஒதுக்கீட்டு அமைப்பு',
      cc_operational_stream: 'செயல்பாட்டு நிகழ்வுகள்',
      cc_mark_resolved: 'தீர்க்கப்பட்டதாகக் குறிக்கவும்',
      cc_dispatcher_summary: 'AI கட்டுப்பாட்டு சுருக்கம்',
      cc_multi_agency: 'துறைசார் தொடர்பு வரிசை',
      cc_layer_visibility: 'வரைபட அடுக்குகள்',
      cc_center_map: 'வரைபட மையம்',
      cc_incident_room: 'சம்பவ அறை',
      cc_confirmed_facts: 'உறுதிப்படுத்தப்பட்ட விவரங்கள்',
      cc_uncertain_info: 'உறுதிப்படுத்தப்படாத தகவல்',

      portal_citizen: 'குடிமக்கள் தளம்',
      portal_responder: 'மீட்புக் குழு தளம்',
      portal_hospital: 'மருத்துவமனை மையம்',
      portal_bloodbank: 'இரத்த வங்கி தளம்',
      portal_shelter: 'பேரிடர் காப்பகங்கள்',
      portal_analytics: 'அவசரநிலை பகுப்பாய்வு & வரைபடம்',

      citizen_sos_instant: 'உடனடி SOS அறிவிப்பு',
      citizen_nearby_facilities: 'அருகிலுள்ள அவசர வசதிகள்',
      citizen_guidelines: 'அவசர உயிர்காக்கும் நெறிமுறைகள்',
      citizen_my_incidents: 'எனது புகார்கள்',
      citizen_report_modal_title: 'அவசர சம்பவத்தைப் பதிவு செய்',

      hosp_er_active: 'ER தயார் நிலை',
      hosp_icu_avail: 'கிடைக்கும் ICU படுக்கைகள்',
      hosp_general_beds: 'பொது அவசர படுக்கைகள்',
      hosp_oxygen_ready: 'ஆக்சிஜன் இருப்பு தயார்',
      blood_stock_inventory: 'இரத்த இருப்பு பட்டியல்',
      shelter_active: 'நிவாரண முகாம்கள்',

      login_portal_title: 'உள்நுழைவு',
      login_portal_subtitle: 'அவசரநிலை கட்டுப்பாட்டு தளத்தை அணுகவும்',
      login_email_label: 'மின்னஞ்சல் / தொலைபேசி',
      login_pass_label: 'கடவுச்சொல்',
      login_quick_roles: 'விரைவு சோதனை தளங்கள்',

      btn_sos: 'அவசர SOS',
      btn_submit: 'அவசர உதவி கோரவும்',
      btn_cancel: 'ரத்து செய்',
      btn_confirm: 'உறுதிசெய்து அனுப்பவும்',
      btn_navigate: 'வழிசெலுத்து',
      btn_broadcast: 'இருப்பு புதுப்பி',
      btn_dispatch_unit: 'பிரிவை அனுப்பு',
      btn_refresh: 'புதுப்பி',
      btn_sign_in: 'உள்நுழைக',

      status_available: 'கிடைக்கிறது',
      status_busy: 'பணிபுரிகிறது',
      status_en_route: 'வழியில் உள்ளது',
      status_arrived: 'வந்துவிட்டது',
      status_resolved: 'தீர்க்கப்பட்டது',
      status_reported: 'பதிவானது',
      status_critical: 'மிகத் தீவிரம்',
      status_high: 'தீவிரம்',
      status_medium: 'நடுத்தரம்',
      status_low: 'சாதாரண'
    }
  };

  const I18n = {
    currentLang: localStorage.getItem('jeevansetu_lang') || 'en',

    t(key) {
      const dict = TRANSLATIONS[this.currentLang] || TRANSLATIONS.en;
      return dict[key] || TRANSLATIONS.en[key] || key;
    },

    setLanguage(lang) {
      if (!TRANSLATIONS[lang]) lang = 'en';
      this.currentLang = lang;
      localStorage.setItem('jeevansetu_lang', lang);

      // Keep header switcher select in sync
      const globalSelect = document.getElementById('globalLangSelect');
      if (globalSelect && globalSelect.value !== lang) {
        globalSelect.value = lang;
      }

      // Synchronize chatbot language selector if chatbot is present
      const chatLangSelect = document.getElementById('jsAiLangSelect');
      if (chatLangSelect && chatLangSelect.value !== lang) {
        chatLangSelect.value = lang;
      }

      this.applyTranslations();
      window.dispatchEvent(new CustomEvent('languageChanged', { detail: { lang } }));
    },

    applyTranslations() {
      // 1. Explicit data-i18n attributes
      document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        const text = this.t(key);
        if (text) {
          if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA') {
            if (el.placeholder) el.placeholder = text;
          } else {
            el.innerText = text;
          }
        }
      });

      // 2. Intelligent scan and translate for standard navigation and header elements
      this.translateNavAndHeader();
      this.translateKpiCards();
      this.translatePortalBadges();
      this.translateCommonButtons();
    },

    translateNavAndHeader() {
      // Desktop & Mobile Navigation Links
      const navLinks = document.querySelectorAll('nav a, header a');
      navLinks.forEach(link => {
        const text = link.innerText.trim();
        if (text.includes('Home') || this.isMatch(text, 'nav_home')) {
          link.innerText = this.t('nav_home');
        } else if (text.includes('How It Works') || this.isMatch(text, 'nav_how_it_works')) {
          link.innerText = this.t('nav_how_it_works');
        } else if (text.includes('Emergency Services') || this.isMatch(text, 'nav_services')) {
          link.innerText = this.t('nav_services');
        } else if (text.includes('Command Center') || this.isMatch(text, 'nav_command_center')) {
          link.innerHTML = `<i class="fa-solid fa-satellite-dish animate-pulse"></i> ${this.t('nav_command_center')}`;
        } else if (text.includes('Command Grid') || this.isMatch(text, 'nav_command_grid')) {
          link.innerText = this.t('nav_command_grid');
        } else if (text.includes('Analytics') || this.isMatch(text, 'nav_analytics')) {
          link.innerText = this.t('nav_analytics');
        } else if (text.includes('LOGIN') || this.isMatch(text, 'nav_login')) {
          link.innerText = this.t('nav_login');
        } else if (text.includes('REPORT EMERGENCY') || this.isMatch(text, 'nav_report_emergency')) {
          link.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> <span>${this.t('nav_report_emergency')}</span>`;
        }
      });

      // Hero Elements
      const heroTitle = document.querySelector('h1');
      if (heroTitle && (heroTitle.innerText.includes('When every second matters') || this.isMatch(heroTitle.innerText, 'hero_title_1'))) {
        heroTitle.innerHTML = `${this.t('hero_title_1')}<br><span class="text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-orange-400 to-amber-300">${this.t('hero_title_2')}</span>`;
      }

      const heroSubtitle = document.querySelector('p.text-base.sm\\:text-lg');
      if (heroSubtitle && (heroSubtitle.innerText.includes('intelligent emergency coordination platform') || this.isMatch(heroSubtitle.innerText, 'hero_subtitle'))) {
        heroSubtitle.innerText = this.t('hero_subtitle');
      }

      // Hero button
      const btnHelp = document.querySelector('.btn-emergency span');
      if (btnHelp && (btnHelp.innerText.includes('GET EMERGENCY HELP') || this.isMatch(btnHelp.innerText, 'hero_btn_emergency'))) {
        btnHelp.innerText = this.t('hero_btn_emergency');
      }

      // SOS Floating Label
      const sosLabel = document.getElementById('jsAiSosLabel');
      if (sosLabel) {
        sosLabel.innerText = this.currentLang === 'en' ? 'SOS' : this.t('btn_sos');
      }
    },

    translatePortalBadges() {
      // Badges in headers
      document.querySelectorAll('header span, header div').forEach(el => {
        const text = el.innerText.trim();
        if (text === 'CITIZEN PORTAL' || this.isMatch(text, 'portal_citizen')) {
          el.innerText = this.t('portal_citizen');
        } else if (text === 'COMMAND CENTER' || this.isMatch(text, 'nav_command_center')) {
          el.innerText = this.t('nav_command_center');
        } else if (text === 'SYSTEM OPERATIONAL' || this.isMatch(text, 'system_operational')) {
          el.innerText = this.t('system_operational');
        } else if (text === 'ER ACTIVE' || this.isMatch(text, 'hosp_er_active')) {
          el.innerText = this.t('hosp_er_active');
        } else if (text === 'Emergency Analytics & Density Heatmap' || this.isMatch(text, 'portal_analytics')) {
          el.innerText = this.t('portal_analytics');
        }
      });
    },

    translateCommonButtons() {
      document.querySelectorAll('button, a.btn-emergency').forEach(btn => {
        const text = btn.innerText.trim();
        if (text.includes('REPORT EMERGENCY') || this.isMatch(text, 'nav_report_emergency')) {
          const icon = btn.querySelector('i');
          const iconHtml = icon ? icon.outerHTML + ' ' : '';
          btn.innerHTML = `${iconHtml}${this.t('nav_report_emergency')}`;
        } else if (text.includes('EMERGENCY SOS') || this.isMatch(text, 'btn_sos')) {
          const icon = btn.querySelector('i');
          const iconHtml = icon ? icon.outerHTML + ' ' : '';
          btn.innerHTML = `${iconHtml}${this.t('btn_sos')}`;
        } else if (text.includes('Broadcast Capacity') || this.isMatch(text, 'btn_broadcast')) {
          btn.innerText = this.t('btn_broadcast');
        }
      });
    },

    translateKpiCards() {
      const kpiLabels = {
        'kpi-active': 'kpi_active_incidents',
        'kpi-critical': 'kpi_critical_trauma',
        'kpi-amb': 'kpi_ambulances_ready',
        'kpi-police-fire': 'kpi_police_fire',
        'kpi-icu': 'kpi_icu_beds'
      };

      for (const [id, key] of Object.entries(kpiLabels)) {
        const el = document.getElementById(id);
        if (el && el.parentElement) {
          const label = el.parentElement.querySelector('.uppercase, .text-slate-400');
          if (label && !label.id) {
            label.innerText = this.t(key);
          }
        }
      }
    },

    isMatch(text, key) {
      if (!text) return false;
      return Object.values(TRANSLATIONS).some(dict => dict[key] && (dict[key] === text || text.includes(dict[key])));
    },

    // Inject Global Language Switcher into Navbars / Headers
    injectLanguageSwitcher() {
      if (document.getElementById('jeevansetu-global-lang-switcher')) return;

      let navContainer = null;
      // In index.html, it's inside nav > div > div.flex.items-center.space-x-4
      const navRight = document.querySelector('nav .flex.items-center.space-x-4');
      if (navRight) {
        navContainer = navRight;
      } else {
        // In dashboards, header usually has two child divs: left (logo) and right (profile, actions)
        const headerChildren = document.querySelectorAll('header > div');
        if (headerChildren.length >= 2) {
          navContainer = headerChildren[headerChildren.length - 1];
        } else {
          navContainer = document.querySelector('header .flex.items-center.space-x-4, header .flex.items-center.space-x-3');
        }
      }

      // Fallback for pages without standard header like login.html
      let isFixedFallback = false;
      if (!navContainer) {
        const topBar = document.createElement('div');
        topBar.className = 'fixed top-4 right-4 z-50';
        document.body.appendChild(topBar);
        navContainer = topBar;
        isFixedFallback = true;
      }

      const switcher = document.createElement('div');
      switcher.id = 'jeevansetu-global-lang-switcher';
      switcher.className = 'relative inline-flex items-center';
      switcher.innerHTML = `
        <div class="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg bg-slate-900/90 border border-slate-700 hover:border-sky-400 text-xs font-semibold text-slate-200 transition shadow-md shadow-black/40">
          <i class="fa-solid fa-globe text-sky-400"></i>
          <select id="globalLangSelect" class="bg-transparent text-slate-200 outline-none cursor-pointer text-xs font-bold font-sans">
            <option value="en" class="bg-slate-900 text-white" ${this.currentLang === 'en' ? 'selected' : ''}>English</option>
            <option value="hi" class="bg-slate-900 text-white" ${this.currentLang === 'hi' ? 'selected' : ''}>हिन्दी (Hindi)</option>
            <option value="te" class="bg-slate-900 text-white" ${this.currentLang === 'te' ? 'selected' : ''}>తెలుగు (Telugu)</option>
            <option value="ta" class="bg-slate-900 text-white" ${this.currentLang === 'ta' ? 'selected' : ''}>தமிழ் (Tamil)</option>
          </select>
        </div>
      `;

      if (isFixedFallback) {
        navContainer.appendChild(switcher);
      } else {
        navContainer.insertBefore(switcher, navContainer.firstChild);
      }

      const selectEl = document.getElementById('globalLangSelect');
      selectEl.addEventListener('change', (e) => {
        this.setLanguage(e.target.value);
      });
    }
  };

  window.I18n = I18n;

  // Initialize on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
      I18n.injectLanguageSwitcher();
      I18n.applyTranslations();
    });
  } else {
    I18n.injectLanguageSwitcher();
    I18n.applyTranslations();
  }

})();
