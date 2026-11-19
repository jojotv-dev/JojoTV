package bj;

import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public b f6250a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public b f6251b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f6252c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public Object f6253d;

    public final Object a() {
        switch (this.f6252c) {
            case 0:
                return this.f6253d;
            default:
                return ((WeakReference) this.f6253d).get();
        }
    }

    public b(b bVar, int i10) {
        this.f6252c = i10;
        this.f6250a = bVar;
        bVar.f6251b = this;
    }
}
