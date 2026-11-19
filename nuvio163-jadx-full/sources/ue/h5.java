package ue;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class h5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f58847a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final i5 f58848b;

    public h5(String name, i5 status) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(status, "status");
        this.f58847a = name;
        this.f58848b = status;
    }

    public static h5 a(h5 h5Var, i5 status) {
        String name = h5Var.f58847a;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(status, "status");
        return new h5(name, status);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof h5)) {
            return false;
        }
        h5 h5Var = (h5) obj;
        return Intrinsics.areEqual(this.f58847a, h5Var.f58847a) && this.f58848b == h5Var.f58848b;
    }

    public final int hashCode() {
        return this.f58848b.hashCode() + (this.f58847a.hashCode() * 31);
    }

    public final String toString() {
        return "SourceChipItem(name=" + this.f58847a + ", status=" + this.f58848b + ")";
    }
}
