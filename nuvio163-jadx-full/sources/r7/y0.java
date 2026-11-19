package r7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes4.dex */
public final class y0 extends r implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f51379b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f51380c;

    public y0(Object obj, Object obj2) {
        this.f51379b = obj;
        this.f51380c = obj2;
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f51379b;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f51380c;
    }

    @Override // r7.r, java.util.Map.Entry
    public final Object setValue(Object obj) {
        throw new UnsupportedOperationException();
    }
}
