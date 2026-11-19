package r7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public final class g0 extends f2 implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final androidx.media3.exoplayer.trackselection.d f51258b;

    public g0(androidx.media3.exoplayer.trackselection.d dVar) {
        this.f51258b = dVar;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return this.f51258b.compare(obj, obj2);
    }

    @Override // java.util.Comparator
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof g0) {
            return this.f51258b.equals(((g0) obj).f51258b);
        }
        return false;
    }

    public final int hashCode() {
        return this.f51258b.hashCode();
    }

    public final String toString() {
        return this.f51258b.toString();
    }
}
