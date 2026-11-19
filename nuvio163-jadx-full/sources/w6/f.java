package w6;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

/* JADX INFO: loaded from: classes.dex */
public final class f implements e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ConnectivityManager f62042b;

    public f(ConnectivityManager connectivityManager) {
        this.f62042b = connectivityManager;
    }

    @Override // w6.e
    public final boolean a() {
        ConnectivityManager connectivityManager = this.f62042b;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities != null && networkCapabilities.hasCapability(12);
    }
}
