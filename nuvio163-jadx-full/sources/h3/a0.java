package h3;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class a0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final z f20607a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final y f20608b;

    public a0(z zVar, y yVar) {
        this.f20607a = zVar;
        this.f20608b = yVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof a0)) {
            return false;
        }
        a0 a0Var = (a0) obj;
        return Intrinsics.areEqual(this.f20608b, a0Var.f20608b) && Intrinsics.areEqual(this.f20607a, a0Var.f20607a);
    }

    public final int hashCode() {
        z zVar = this.f20607a;
        int iHashCode = (zVar != null ? zVar.hashCode() : 0) * 31;
        y yVar = this.f20608b;
        return iHashCode + (yVar != null ? yVar.hashCode() : 0);
    }

    public final String toString() {
        return "PlatformTextStyle(spanStyle=" + this.f20607a + ", paragraphSyle=" + this.f20608b + ')';
    }

    public a0() {
        this(null, new y());
    }
}
