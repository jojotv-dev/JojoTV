package ie;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class w {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f24135a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f24136b;

    public w(boolean z2, String str) {
        this.f24135a = z2;
        this.f24136b = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof w)) {
            return false;
        }
        w wVar = (w) obj;
        return this.f24135a == wVar.f24135a && Intrinsics.areEqual(this.f24136b, wVar.f24136b);
    }

    public final int hashCode() {
        int iHashCode = Boolean.hashCode(this.f24135a) * 31;
        String str = this.f24136b;
        return iHashCode + (str == null ? 0 : str.hashCode());
    }

    public final String toString() {
        return "OnDemandStatus(loading=" + this.f24135a + ", message=" + this.f24136b + ")";
    }
}
