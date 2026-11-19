package he;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f21041a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f21042b;

    public t(String id2, boolean z2) {
        Intrinsics.checkNotNullParameter(id2, "id");
        this.f21041a = id2;
        this.f21042b = z2;
    }

    public static t a(t tVar, boolean z2) {
        String id2 = tVar.f21041a;
        Intrinsics.checkNotNullParameter(id2, "id");
        return new t(id2, z2);
    }

    public final String b() {
        return this.f21041a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof t)) {
            return false;
        }
        t tVar = (t) obj;
        return Intrinsics.areEqual(this.f21041a, tVar.f21041a) && this.f21042b == tVar.f21042b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f21042b) + (this.f21041a.hashCode() * 31);
    }

    public final String toString() {
        return "PanelRowList(id=" + this.f21041a + ", on=" + this.f21042b + ")";
    }
}
