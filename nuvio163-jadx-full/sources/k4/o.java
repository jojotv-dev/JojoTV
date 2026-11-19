package k4;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Runnable f27816a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CopyOnWriteArrayList f27817b = new CopyOnWriteArrayList();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HashMap f27818c = new HashMap();

    public o(Runnable runnable) {
        this.f27816a = runnable;
    }

    public final void a(p pVar) {
        this.f27817b.remove(pVar);
        n nVar = (n) this.f27818c.remove(pVar);
        if (nVar != null) {
            nVar.a();
        }
        this.f27816a.run();
    }
}
