package ah;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements NsdManager.DiscoveryListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final c f1717a;

    public a(c cVar) {
        this.f1717a = cVar;
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onDiscoveryStarted(String str) {
        this.f1717a.f1724f = true;
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onDiscoveryStopped(String str) {
        this.f1717a.f1724f = false;
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onServiceFound(NsdServiceInfo nsdServiceInfo) {
        c cVar = this.f1717a;
        cVar.f1723e.resolveService(nsdServiceInfo, new b(cVar));
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onServiceLost(NsdServiceInfo nsdServiceInfo) {
        c cVar = this.f1717a;
        String str = cVar.f1726h;
        if (str == null || !str.equals(nsdServiceInfo.getServiceName())) {
            return;
        }
        cVar.f1721c.a(nsdServiceInfo.getHost(), -1);
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onStartDiscoveryFailed(String str, int i10) {
    }

    @Override // android.net.nsd.NsdManager.DiscoveryListener
    public final void onStopDiscoveryFailed(String str, int i10) {
    }
}
