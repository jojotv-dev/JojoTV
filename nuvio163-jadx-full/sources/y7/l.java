package y7;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class l implements Map.Entry {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public l f65122b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public l f65123c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public l f65124e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public l f65125i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public l f65126m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final Object f65127n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final boolean f65128o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public Object f65129p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public int f65130q;

    public l(boolean z2) {
        this.f65127n = null;
        this.f65128o = z2;
        this.f65126m = this;
        this.f65125i = this;
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        if (obj instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry) obj;
            Object obj2 = this.f65127n;
            if (obj2 != null ? obj2.equals(entry.getKey()) : entry.getKey() == null) {
                Object obj3 = this.f65129p;
                if (obj3 == null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                } else if (obj3.equals(entry.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f65127n;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f65129p;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        Object obj = this.f65127n;
        int iHashCode = obj == null ? 0 : obj.hashCode();
        Object obj2 = this.f65129p;
        return (obj2 != null ? obj2.hashCode() : 0) ^ iHashCode;
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        if (obj == null && !this.f65128o) {
            throw new NullPointerException("value == null");
        }
        Object obj2 = this.f65129p;
        this.f65129p = obj;
        return obj2;
    }

    public final String toString() {
        return this.f65127n + "=" + this.f65129p;
    }

    public l(boolean z2, l lVar, Object obj, l lVar2, l lVar3) {
        this.f65122b = lVar;
        this.f65127n = obj;
        this.f65128o = z2;
        this.f65130q = 1;
        this.f65125i = lVar2;
        this.f65126m = lVar3;
        lVar3.f65125i = this;
        lVar2.f65126m = this;
    }
}
