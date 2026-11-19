package pf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class y implements z {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f45058a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f45059b;

    public y(String scraperId, boolean z2) {
        Intrinsics.checkNotNullParameter(scraperId, "scraperId");
        this.f45058a = scraperId;
        this.f45059b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof y)) {
            return false;
        }
        y yVar = (y) obj;
        return Intrinsics.areEqual(this.f45058a, yVar.f45058a) && this.f45059b == yVar.f45059b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f45059b) + (this.f45058a.hashCode() * 31);
    }

    public final String toString() {
        return "ToggleScraper(scraperId=" + this.f45058a + ", enabled=" + this.f45059b + ")";
    }
}
