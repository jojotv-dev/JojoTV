package se;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54272a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f54273b;

    public b(int i10, Set played) {
        Intrinsics.checkNotNullParameter(played, "played");
        this.f54272a = i10;
        this.f54273b = played;
    }

    public static b a(b bVar, int i10, Set played, int i11) {
        if ((i11 & 1) != 0) {
            i10 = bVar.f54272a;
        }
        if ((i11 & 2) != 0) {
            played = bVar.f54273b;
        }
        Intrinsics.checkNotNullParameter(played, "played");
        return new b(i10, played);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return this.f54272a == bVar.f54272a && Intrinsics.areEqual(this.f54273b, bVar.f54273b);
    }

    public final int hashCode() {
        return this.f54273b.hashCode() + (Integer.hashCode(this.f54272a) * 31);
    }

    public final String toString() {
        return "ListChannelState(cursor=" + this.f54272a + ", played=" + this.f54273b + ")";
    }
}
