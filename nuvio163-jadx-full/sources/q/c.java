package q;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class c implements Map.Entry {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f45268b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f45269c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public c f45270e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public c f45271i;

    public c(Object obj, Object obj2) {
        this.f45268b = obj;
        this.f45269c = obj2;
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return this.f45268b.equals(cVar.f45268b) && this.f45269c.equals(cVar.f45269c);
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f45268b;
    }

    @Override // java.util.Map.Entry
    public final Object getValue() {
        return this.f45269c;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        return this.f45268b.hashCode() ^ this.f45269c.hashCode();
    }

    @Override // java.util.Map.Entry
    public final Object setValue(Object obj) {
        throw new UnsupportedOperationException("An entry modification is not supported");
    }

    public final String toString() {
        return this.f45268b + "=" + this.f45269c;
    }
}
