package pg;

import android.os.Looper;
import java.util.HashSet;
import java.util.Iterator;
import w.h0;

/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashSet f45067a = new HashSet();

    public final void a() {
        if (pa.a.f44382d == null) {
            pa.a.f44382d = Looper.getMainLooper().getThread();
        }
        if (Thread.currentThread() != pa.a.f44382d) {
            throw new IllegalStateException("Must be called on the Main thread.");
        }
        Iterator it = this.f45067a.iterator();
        if (it.hasNext()) {
            throw h0.i(it);
        }
    }
}
