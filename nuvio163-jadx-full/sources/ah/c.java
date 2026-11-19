package ah;

import a0.i;
import android.content.Context;
import android.net.nsd.NsdManager;
import java.util.Objects;

/* JADX INFO: loaded from: classes5.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f1719a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f1720b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final zg.a f1721c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final a f1722d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final NsdManager f1723e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f1724f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public boolean f1725g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public String f1726h;

    public c(Context context, String str, zg.a aVar) {
        Objects.requireNonNull(context);
        this.f1719a = context;
        this.f1720b = i.n("_", str, "._tcp");
        this.f1721c = aVar;
        this.f1723e = (NsdManager) context.getSystemService("servicediscovery");
        this.f1722d = new a(this);
    }

    public final void a() {
        if (this.f1725g) {
            this.f1725g = false;
            if (this.f1724f) {
                this.f1723e.stopServiceDiscovery(this.f1722d);
            }
        }
    }
}
