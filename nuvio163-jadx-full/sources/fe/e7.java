package fe;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e7 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f16797a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f16798b;

    public e7(ArrayList entries, List epgUrls) {
        Intrinsics.checkNotNullParameter(epgUrls, "epgUrls");
        Intrinsics.checkNotNullParameter(entries, "entries");
        this.f16797a = epgUrls;
        this.f16798b = entries;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e7)) {
            return false;
        }
        e7 e7Var = (e7) obj;
        return Intrinsics.areEqual(this.f16797a, e7Var.f16797a) && Intrinsics.areEqual(this.f16798b, e7Var.f16798b);
    }

    public final int hashCode() {
        return this.f16798b.hashCode() + (this.f16797a.hashCode() * 31);
    }

    public final String toString() {
        return "M3uPlaylist(epgUrls=" + this.f16797a + ", entries=" + this.f16798b + ")";
    }
}
