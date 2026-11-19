package kf;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f30099a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f30100b;

    public o0(List continueWatchingItems, List upcomingItems) {
        Intrinsics.checkNotNullParameter(continueWatchingItems, "continueWatchingItems");
        Intrinsics.checkNotNullParameter(upcomingItems, "upcomingItems");
        this.f30099a = continueWatchingItems;
        this.f30100b = upcomingItems;
    }

    public final List a() {
        return this.f30099a;
    }

    public final List b() {
        return this.f30100b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o0)) {
            return false;
        }
        o0 o0Var = (o0) obj;
        return Intrinsics.areEqual(this.f30099a, o0Var.f30099a) && Intrinsics.areEqual(this.f30100b, o0Var.f30100b);
    }

    public final int hashCode() {
        return this.f30100b.hashCode() + (this.f30099a.hashCode() * 31);
    }

    public final String toString() {
        return "ContinueWatchingPosterOverlay(continueWatchingItems=" + this.f30099a + ", upcomingItems=" + this.f30100b + ")";
    }
}
