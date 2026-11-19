package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f42225a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final g f42226b;

    public i(String referenceKey, g alignment) {
        Intrinsics.checkNotNullParameter(referenceKey, "referenceKey");
        Intrinsics.checkNotNullParameter(alignment, "alignment");
        this.f42225a = referenceKey;
        this.f42226b = alignment;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof i)) {
            return false;
        }
        i iVar = (i) obj;
        return Intrinsics.areEqual(this.f42225a, iVar.f42225a) && Intrinsics.areEqual(this.f42226b, iVar.f42226b);
    }

    public final int hashCode() {
        return this.f42226b.hashCode() + (this.f42225a.hashCode() * 31);
    }

    public final String toString() {
        return "Match(referenceKey=" + this.f42225a + ", alignment=" + this.f42226b + ")";
    }
}
