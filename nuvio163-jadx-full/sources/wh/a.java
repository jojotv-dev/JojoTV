package wh;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes5.dex */
public class a implements Map.Entry, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f62531b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f62532c;

    public a(Object obj, Object obj2) {
        this.f62531b = obj;
        this.f62532c = obj2;
    }

    @Override // java.util.Map.Entry
    public final boolean equals(Object obj) {
        Map.Entry entry = obj instanceof Map.Entry ? (Map.Entry) obj : null;
        return entry != null && Intrinsics.areEqual(entry.getKey(), this.f62531b) && Intrinsics.areEqual(entry.getValue(), getValue());
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        return this.f62531b;
    }

    @Override // java.util.Map.Entry
    public Object getValue() {
        return this.f62532c;
    }

    @Override // java.util.Map.Entry
    public final int hashCode() {
        Object obj = this.f62531b;
        int iHashCode = obj != null ? obj.hashCode() : 0;
        Object value = getValue();
        return (value != null ? value.hashCode() : 0) ^ iHashCode;
    }

    @Override // java.util.Map.Entry
    public Object setValue(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.f62531b);
        sb2.append('=');
        sb2.append(getValue());
        return sb2.toString();
    }
}
