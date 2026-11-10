/**
 * JeevanSetu - Offline Emergency Reporting & Synchronization Module
 * Module 8: Transparent offline caching, network detection, and server sync with idempotency
 */

const OfflineSync = {
  STORAGE_KEY: 'jeevansetu_offline_reports',

  init() {
    this.createStatusBanner();
    this.updateNetworkStatus();

    window.addEventListener('online', () => {
      this.updateNetworkStatus();
      this.syncPendingReports();
    });

    window.addEventListener('offline', () => {
      this.updateNetworkStatus();
    });

    // Attempt initial sync on load if online
    if (navigator.onLine) {
      this.syncPendingReports();
    }
  },

  isOnline() {
    return navigator.onLine;
  },

  getQueue() {
    try {
      return JSON.parse(localStorage.getItem(this.STORAGE_KEY) || '[]');
    } catch (e) {
      return [];
    }
  },

  saveQueue(queue) {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(queue));
    this.updateBadge();
  },

  saveReportOffline(reportData) {
    const queue = this.getQueue();
    const clientGeneratedId = reportData.clientGeneratedId || ('OFFLINE-' + crypto.randomUUID());
    const reportItem = {
      ...reportData,
      clientGeneratedId,
      offlineSavedAt: new Date().toISOString(),
      syncStatus: 'PENDING_SYNC'
    };

    queue.push(reportItem);
    this.saveQueue(queue);

    this.showToast('⚠️ Offline Mode: Emergency report saved locally. It will automatically submit once internet is restored.', 'amber');
    return reportItem;
  },

  async submitOrQueue(reportData) {
    if (!reportData.clientGeneratedId) {
      reportData.clientGeneratedId = 'CLIENT-' + crypto.randomUUID();
    }

    if (!this.isOnline()) {
      return this.saveReportOffline(reportData);
    }

    try {
      const result = await API.request('/emergencies/report', {
        method: 'POST',
        body: JSON.stringify(reportData)
      });
      return result;
    } catch (err) {
      console.warn('[OfflineSync] Server unreachable. Saving report to offline queue:', err);
      return this.saveReportOffline(reportData);
    }
  },

  async syncPendingReports() {
    const queue = this.getQueue();
    if (queue.length === 0) return;

    this.showToast(`🔄 Synchronizing ${queue.length} pending offline emergency report(s)...`, 'sky');

    const remaining = [];
    let syncedCount = 0;

    for (const item of queue) {
      try {
        await API.request('/emergencies/report', {
          method: 'POST',
          body: JSON.stringify(item)
        });
        syncedCount++;
      } catch (err) {
        console.error('[OfflineSync] Sync failed for item:', item, err);
        remaining.push(item);
      }
    }

    this.saveQueue(remaining);

    if (syncedCount > 0) {
      this.showToast(`✅ Successfully synchronized ${syncedCount} offline report(s) with JeevanSetu Command Center.`, 'emerald');
      // Trigger live refresh if on dashboard
      if (typeof window.refreshAll === 'function') {
        window.refreshAll();
      }
    }
  },

  createStatusBanner() {
    if (document.getElementById('offline-sync-indicator')) return;

    const banner = document.createElement('div');
    banner.id = 'offline-sync-indicator';
    banner.className = 'fixed bottom-4 left-4 z-50 transition-all duration-300';
    banner.innerHTML = `
      <div id="offline-pill" class="hidden items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold shadow-lg backdrop-blur border">
        <span class="w-2 h-2 rounded-full animate-ping" id="offline-dot"></span>
        <span id="offline-text">Online</span>
        <span id="offline-badge" class="hidden px-1.5 py-0.2 rounded-full text-[10px] font-bold bg-amber-500 text-slate-900">0</span>
      </div>
    `;
    document.body.appendChild(banner);
  },

  updateNetworkStatus() {
    const pill = document.getElementById('offline-pill');
    const dot = document.getElementById('offline-dot');
    const text = document.getElementById('offline-text');
    if (!pill || !text || !dot) return;

    const queue = this.getQueue();

    if (!this.isOnline()) {
      pill.className = 'flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-bold shadow-lg bg-red-950/90 border border-red-500/60 text-red-200';
      dot.className = 'w-2 h-2 rounded-full bg-red-400';
      text.innerText = 'OFFLINE MODE (Drafts Queued)';
      pill.classList.remove('hidden');
    } else if (queue.length > 0) {
      pill.className = 'flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-bold shadow-lg bg-amber-950/90 border border-amber-500/60 text-amber-200';
      dot.className = 'w-2 h-2 rounded-full bg-amber-400 animate-pulse';
      text.innerText = `${queue.length} Pending Sync`;
      pill.classList.remove('hidden');
    } else {
      pill.classList.add('hidden');
    }

    this.updateBadge();
  },

  updateBadge() {
    const badge = document.getElementById('offline-badge');
    const queue = this.getQueue();
    if (badge) {
      if (queue.length > 0) {
        badge.innerText = queue.length;
        badge.classList.remove('hidden');
      } else {
        badge.classList.add('hidden');
      }
    }
  },

  showToast(msg, color = 'sky') {
    const toast = document.createElement('div');
    const bg = color === 'emerald' ? 'bg-emerald-950 border-emerald-500 text-emerald-200' :
               color === 'amber' ? 'bg-amber-950 border-amber-500 text-amber-200' :
               'bg-sky-950 border-sky-500 text-sky-200';

    toast.className = `fixed top-20 right-6 z-50 px-4 py-2.5 rounded-xl border text-xs font-semibold shadow-2xl transition-all duration-500 transform translate-y-2 opacity-0 flex items-center gap-2 ${bg}`;
    toast.innerHTML = `<span>${msg}</span>`;

    document.body.appendChild(toast);
    setTimeout(() => {
      toast.classList.remove('translate-y-2', 'opacity-0');
    }, 50);

    setTimeout(() => {
      toast.classList.add('opacity-0', 'translate-y-2');
      setTimeout(() => toast.remove(), 500);
    }, 4500);
  }
};

document.addEventListener('DOMContentLoaded', () => {
  OfflineSync.init();
});
