package le;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class l0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f33516a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f33517b;

    public l0(String recordingId, boolean z2) {
        Intrinsics.checkNotNullParameter(recordingId, "recordingId");
        this.f33516a = recordingId;
        this.f33517b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof l0)) {
            return false;
        }
        l0 l0Var = (l0) obj;
        return Intrinsics.areEqual(this.f33516a, l0Var.f33516a) && this.f33517b == l0Var.f33517b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f33517b) + (this.f33516a.hashCode() * 31);
    }

    public final String toString() {
        return "Request(recordingId=" + this.f33516a + ", fromStart=" + this.f33517b + ")";
    }
}
