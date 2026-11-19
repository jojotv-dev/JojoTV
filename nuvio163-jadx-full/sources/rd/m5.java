package rd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class m5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f52360a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f52361b;

    public m5(String lookupKey, Integer num) {
        Intrinsics.checkNotNullParameter(lookupKey, "lookupKey");
        this.f52360a = lookupKey;
        this.f52361b = num;
    }

    public final Integer a() {
        return this.f52361b;
    }

    public final String b() {
        return this.f52360a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof m5)) {
            return false;
        }
        m5 m5Var = (m5) obj;
        return Intrinsics.areEqual(this.f52360a, m5Var.f52360a) && Intrinsics.areEqual(this.f52361b, m5Var.f52361b);
    }

    public final int hashCode() {
        int iHashCode = this.f52360a.hashCode() * 31;
        Integer num = this.f52361b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "SimklProjectionVideoId(lookupKey=" + this.f52360a + ", episode=" + this.f52361b + ")";
    }
}
