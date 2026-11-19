package androidx.datastore.preferences.protobuf;

import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class g1 implements Map.Entry, Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Comparable f2471b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f2472c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ c1 f2473e;

    public g1(c1 c1Var, Map.Entry entry) {
        this(c1Var, (Comparable) entry.getKey(), entry.getValue());
    }

    public final Comparable a() {
        return this.f2471b;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return this.f2471b.compareTo(((g1) obj).f2471b);
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        boolean zEquals;
        boolean zEquals2;
        if (obj != this) {
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                Object key = entry.getKey();
                Comparable comparable = this.f2471b;
                if (comparable == null) {
                    zEquals = key == null;
                } else {
                    zEquals = comparable.equals(key);
                }
                if (zEquals) {
                    Object obj2 = this.f2472c;
                    Object value = entry.getValue();
                    if (obj2 == null) {
                        zEquals2 = value == null;
                    } else {
                        zEquals2 = obj2.equals(value);
                    }
                    if (zEquals2) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f2471b;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f2472c;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        Comparable comparable = this.f2471b;
        int iHashCode = comparable == null ? 0 : comparable.hashCode();
        Object obj = this.f2472c;
        return (obj != null ? obj.hashCode() : 0) ^ iHashCode;
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        this.f2473e.b();
        Object obj2 = this.f2472c;
        this.f2472c = obj;
        return obj2;
    }

    public final String toString() {
        return this.f2471b + "=" + this.f2472c;
    }

    public g1(c1 c1Var, Comparable comparable, Object obj) {
        this.f2473e = c1Var;
        this.f2471b = comparable;
        this.f2472c = obj;
    }
}
