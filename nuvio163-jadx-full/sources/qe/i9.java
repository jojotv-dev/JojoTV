package qe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class i9 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f49184a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f49185b;

    public i9(String text, boolean z2) {
        Intrinsics.checkNotNullParameter(text, "text");
        this.f49184a = text;
        this.f49185b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof i9)) {
            return false;
        }
        i9 i9Var = (i9) obj;
        return Intrinsics.areEqual(this.f49184a, i9Var.f49184a) && this.f49185b == i9Var.f49185b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f49185b) + (this.f49184a.hashCode() * 31);
    }

    public final String toString() {
        return "Entry(text=" + this.f49184a + ", highlight=" + this.f49185b + ")";
    }
}
