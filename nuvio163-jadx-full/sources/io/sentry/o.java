package io.sentry;

import java.util.Iterator;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes5.dex */
public final class o extends TimerTask {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f25782b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f25783c;

    public /* synthetic */ o(Object obj, int i10) {
        this.f25782b = i10;
        this.f25783c = obj;
    }

    @Override // java.util.TimerTask, java.lang.Runnable
    public final void run() {
        switch (this.f25782b) {
            case 0:
                Iterator it = ((r) this.f25783c).f26089d.iterator();
                while (it.hasNext()) {
                    ((w0) it.next()).a();
                }
                break;
            default:
                io.sentry.transport.p pVar = (io.sentry.transport.p) this.f25783c;
                Iterator it2 = pVar.f26216i.iterator();
                while (it2.hasNext()) {
                    ((io.sentry.transport.o) it2.next()).H(pVar);
                }
                break;
        }
    }
}
