package nf;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f42118a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f42119b;

    public c1(List cues, String str) {
        Intrinsics.checkNotNullParameter(cues, "cues");
        this.f42118a = cues;
        this.f42119b = str;
    }

    public final String a() {
        return this.f42119b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c1)) {
            return false;
        }
        c1 c1Var = (c1) obj;
        return Intrinsics.areEqual(this.f42118a, c1Var.f42118a) && Intrinsics.areEqual(this.f42119b, c1Var.f42119b);
    }

    public final int hashCode() {
        int iHashCode = this.f42118a.hashCode() * 31;
        String str = this.f42119b;
        return iHashCode + (str == null ? 0 : str.hashCode());
    }

    public final String toString() {
        return "LoadedSubtitle(cues=" + this.f42118a + ", rawBody=" + this.f42119b + ")";
    }
}
