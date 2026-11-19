package nf;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f42446a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f42447b;

    public o2(long j10, Map tracks) {
        Intrinsics.checkNotNullParameter(tracks, "tracks");
        this.f42446a = j10;
        this.f42447b = tracks;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o2)) {
            return false;
        }
        o2 o2Var = (o2) obj;
        return this.f42446a == o2Var.f42446a && Intrinsics.areEqual(this.f42447b, o2Var.f42447b);
    }

    public final int hashCode() {
        return this.f42447b.hashCode() + (Long.hashCode(this.f42446a) * 31);
    }

    public final String toString() {
        return "RetainedGeneration(generation=" + this.f42446a + ", tracks=" + this.f42447b + ")";
    }
}
