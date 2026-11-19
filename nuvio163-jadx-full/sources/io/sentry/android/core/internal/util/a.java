package io.sentry.android.core.internal.util;

import android.net.ConnectivityManager;
import io.sentry.l0;
import io.sentry.m0;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24762b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ c f24763c;

    public /* synthetic */ a(c cVar, int i10) {
        this.f24762b = i10;
        this.f24763c = cVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f24762b) {
            case 0:
                this.f24763c.A();
                return;
            case 1:
                this.f24763c.W(false);
                return;
            default:
                c cVar = this.f24763c;
                cVar.X(null);
                l0 l0VarE = cVar.E();
                if (l0VarE == l0.DISCONNECTED) {
                    cVar.f24781s.set(false);
                    io.sentry.util.a aVar = c.f24767v;
                    aVar.g();
                    try {
                        Iterator it = c.f24768w.iterator();
                        while (it.hasNext()) {
                            ((ConnectivityManager.NetworkCallback) it.next()).onLost(null);
                        }
                        aVar.close();
                    } catch (Throwable th2) {
                        try {
                            aVar.close();
                            break;
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                        throw th2;
                    }
                }
                io.sentry.util.a aVar2 = cVar.f24776n;
                aVar2.g();
                try {
                    Iterator it2 = cVar.f24775m.iterator();
                    while (it2.hasNext()) {
                        ((m0) it2.next()).g(l0VarE);
                    }
                    aVar2.close();
                    cVar.A();
                    return;
                } catch (Throwable th4) {
                    try {
                        aVar2.close();
                        break;
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                    }
                    throw th4;
                }
        }
    }
}
