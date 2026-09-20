/**
 * JeevanSetu - Real-Time STOMP WebSocket Client
 */
class RealtimeManager {
  constructor() {
    this.handlers = {};
    this.connected = false;
    this.socket = null;
    this.stompClient = null;
  }

  init() {
    // If SockJS and Stomp are loaded on the page
    if (typeof SockJS !== 'undefined' && typeof Stomp !== 'undefined') {
      try {
        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null; // suppress verbose console debug
        this.stompClient.connect({}, () => {
          this.connected = true;
          console.log('[WebSocket] Connected to JeevanSetu STOMP broker.');
          
          this.stompClient.subscribe('/topic/emergencies', (msg) => {
            this.emit('EMERGENCY_UPDATE', JSON.parse(msg.body));
          });
          this.stompClient.subscribe('/topic/dispatches', (msg) => {
            this.emit('DISPATCH_UPDATE', JSON.parse(msg.body));
          });
          this.stompClient.subscribe('/topic/hospitals', (msg) => {
            this.emit('HOSPITAL_UPDATE', JSON.parse(msg.body));
          });
          this.stompClient.subscribe('/topic/notifications', (msg) => {
            this.emit('NOTIFICATION', JSON.parse(msg.body));
          });
        }, (err) => {
          console.log('[WebSocket] Live broker not active, running local sync.');
          this.connected = false;
        });
      } catch (e) {
        console.log('[WebSocket] Falling back to local events');
      }
    }
  }

  on(event, callback) {
    if (!this.handlers[event]) this.handlers[event] = [];
    this.handlers[event].push(callback);
  }

  emit(event, data) {
    if (this.handlers[event]) {
      this.handlers[event].forEach(cb => cb(data));
    }
  }
}

window.realtime = new RealtimeManager();
document.addEventListener('DOMContentLoaded', () => window.realtime.init());
