package re;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class t1 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final t1 f53260c = new t1(s1.f53244b, 0);

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final t1 f53261d = new t1(s1.f53245c, 0);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final t1 f53262e = new t1(s1.f53247i, 0);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final s1 f53263a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f53264b;

    public t1(s1 state, int i10) {
        Intrinsics.checkNotNullParameter(state, "state");
        this.f53263a = state;
        this.f53264b = i10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof t1)) {
            return false;
        }
        t1 t1Var = (t1) obj;
        return this.f53263a == t1Var.f53263a && this.f53264b == t1Var.f53264b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f53264b) + (this.f53263a.hashCode() * 31);
    }

    public final String toString() {
        return "LiveTvSourceGuide(state=" + this.f53263a + ", channels=" + this.f53264b + ")";
    }
}
