package e0;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class w1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public float f13618a = 0.0f;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f13619b = true;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public e0 f13620c = null;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof w1)) {
            return false;
        }
        w1 w1Var = (w1) obj;
        return Float.compare(this.f13618a, w1Var.f13618a) == 0 && this.f13619b == w1Var.f13619b && Intrinsics.areEqual(this.f13620c, w1Var.f13620c) && Intrinsics.areEqual((Object) null, (Object) null);
    }

    public final int hashCode() {
        int iD = o2.a.d(Float.hashCode(this.f13618a) * 31, 31, this.f13619b);
        e0 e0Var = this.f13620c;
        return (iD + (e0Var == null ? 0 : e0Var.f13459c.hashCode())) * 31;
    }

    public final String toString() {
        return "RowColumnParentData(weight=" + this.f13618a + ", fill=" + this.f13619b + ", crossAxisAlignment=" + this.f13620c + ", flowLayoutData=null)";
    }
}
