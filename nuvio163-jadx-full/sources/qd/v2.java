package qd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class v2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final fa f47958a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f47959b;

    public v2(fa type, String pathId) {
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(pathId, "pathId");
        this.f47958a = type;
        this.f47959b = pathId;
    }

    public final String a() {
        return this.f47959b;
    }

    public final fa b() {
        return this.f47958a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof v2)) {
            return false;
        }
        v2 v2Var = (v2) obj;
        return this.f47958a == v2Var.f47958a && Intrinsics.areEqual(this.f47959b, v2Var.f47959b);
    }

    public final int hashCode() {
        return this.f47959b.hashCode() + (this.f47958a.hashCode() * 31);
    }

    public final String toString() {
        return "ResolvedRelatedTarget(type=" + this.f47958a + ", pathId=" + this.f47959b + ")";
    }
}
