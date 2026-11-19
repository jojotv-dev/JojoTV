package w;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class f2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final p f61245a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final v f61246b;

    public f2(p pVar, v vVar) {
        this.f61245a = pVar;
        this.f61246b = vVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof f2)) {
            return false;
        }
        f2 f2Var = (f2) obj;
        return Intrinsics.areEqual(this.f61245a, f2Var.f61245a) && Intrinsics.areEqual(this.f61246b, f2Var.f61246b);
    }

    public final int hashCode() {
        return Integer.hashCode(0) + ((this.f61246b.hashCode() + (this.f61245a.hashCode() * 31)) * 31);
    }

    public final String toString() {
        return "VectorizedKeyframeSpecElementInfo(vectorValue=" + this.f61245a + ", easing=" + this.f61246b + ", arcMode=ArcMode(value=0))";
    }
}
