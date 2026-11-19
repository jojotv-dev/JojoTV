package v;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class x0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final w.x f60542a;

    public x0(w.x xVar) {
        this.f60542a = xVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof x0) {
            return Float.compare(0.0f, 0.0f) == 0 && Intrinsics.areEqual(this.f60542a, ((x0) obj).f60542a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f60542a.hashCode() + (Float.hashCode(0.0f) * 31);
    }

    public final String toString() {
        return "Fade(alpha=0.0, animationSpec=" + this.f60542a + ')';
    }
}
