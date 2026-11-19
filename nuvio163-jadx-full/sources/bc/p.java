package bc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p implements r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final n f4442a;

    public p(n referenceLinkHandler) {
        Intrinsics.checkNotNullParameter(referenceLinkHandler, "referenceLinkHandler");
        this.f4442a = referenceLinkHandler;
    }

    @Override // bc.r
    public final n a() {
        return this.f4442a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof p) && Intrinsics.areEqual(this.f4442a, ((p) obj).f4442a);
    }

    public final int hashCode() {
        return this.f4442a.hashCode();
    }

    public final String toString() {
        return "Loading(referenceLinkHandler=" + this.f4442a + ")";
    }
}
