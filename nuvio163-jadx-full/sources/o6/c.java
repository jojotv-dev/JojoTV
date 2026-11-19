package o6;

import kotlin.Unit;

/* JADX INFO: loaded from: classes.dex */
public final class c implements AutoCloseable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final b f43499b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f43500c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ e f43501e;

    public c(e eVar, b bVar) {
        this.f43501e = eVar;
        this.f43499b = bVar;
    }

    @Override // java.lang.AutoCloseable
    public final void close() {
        if (this.f43500c) {
            return;
        }
        this.f43500c = true;
        e eVar = this.f43501e;
        synchronized (eVar.f43510p) {
            try {
                b bVar = this.f43499b;
                int i10 = bVar.f43497h - 1;
                bVar.f43497h = i10;
                if (i10 == 0 && bVar.f43495f) {
                    eVar.U(bVar);
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }
}
