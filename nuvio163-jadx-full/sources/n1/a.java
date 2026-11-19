package n1;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public class a implements Map.Entry, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f39415b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f39416c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f39417e;

    public /* synthetic */ a(Object obj, Object obj2, int i10) {
        this.f39415b = i10;
        this.f39416c = obj;
        this.f39417e = obj2;
    }

    @Override // java.util.Map.Entry
    public boolean equals(Object obj) {
        switch (this.f39415b) {
            case 0:
                Map.Entry entry = obj instanceof Map.Entry ? (Map.Entry) obj : null;
                return entry != null && Intrinsics.areEqual(entry.getKey(), this.f39416c) && Intrinsics.areEqual(entry.getValue(), getValue());
            default:
                return super.equals(obj);
        }
    }

    @Override // java.util.Map.Entry
    public final Object getKey() {
        switch (this.f39415b) {
            case 0:
                break;
        }
        return this.f39416c;
    }

    @Override // java.util.Map.Entry
    public Object getValue() {
        switch (this.f39415b) {
            case 0:
                break;
        }
        return this.f39417e;
    }

    @Override // java.util.Map.Entry
    public int hashCode() {
        switch (this.f39415b) {
            case 0:
                Object obj = this.f39416c;
                int iHashCode = obj != null ? obj.hashCode() : 0;
                Object value = getValue();
                return (value != null ? value.hashCode() : 0) ^ iHashCode;
            default:
                return super.hashCode();
        }
    }

    @Override // java.util.Map.Entry
    public Object setValue(Object obj) {
        switch (this.f39415b) {
            case 0:
                throw new UnsupportedOperationException("Operation is not supported for read-only collection");
            default:
                throw new UnsupportedOperationException("Operation is not supported for read-only collection");
        }
    }

    public String toString() {
        switch (this.f39415b) {
            case 0:
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.f39416c);
                sb2.append('=');
                sb2.append(getValue());
                return sb2.toString();
            default:
                return super.toString();
        }
    }
}
