package pf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class x implements z {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f45056a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f45057b;

    public x(String repoId, boolean z2) {
        Intrinsics.checkNotNullParameter(repoId, "repoId");
        this.f45056a = repoId;
        this.f45057b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof x)) {
            return false;
        }
        x xVar = (x) obj;
        return Intrinsics.areEqual(this.f45056a, xVar.f45056a) && this.f45057b == xVar.f45057b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f45057b) + (this.f45056a.hashCode() * 31);
    }

    public final String toString() {
        return "ToggleAllScrapersForRepo(repoId=" + this.f45056a + ", enabled=" + this.f45057b + ")";
    }
}
