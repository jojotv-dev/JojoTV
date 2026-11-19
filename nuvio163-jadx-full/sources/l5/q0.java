package l5;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class q0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f32053a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f32054b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f32055c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f32056d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f32057e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f32058f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f32059g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public String f32060h;

    public q0(boolean z2, boolean z10, int i10, boolean z11, boolean z12, int i11, int i12) {
        this.f32053a = z2;
        this.f32054b = z10;
        this.f32055c = i10;
        this.f32056d = z11;
        this.f32057e = z12;
        this.f32058f = i11;
        this.f32059g = i12;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof q0)) {
            return false;
        }
        q0 q0Var = (q0) obj;
        return this.f32053a == q0Var.f32053a && this.f32054b == q0Var.f32054b && this.f32055c == q0Var.f32055c && Intrinsics.areEqual(this.f32060h, q0Var.f32060h) && Intrinsics.areEqual((Object) null, (Object) null) && Intrinsics.areEqual((Object) null, (Object) null) && this.f32056d == q0Var.f32056d && this.f32057e == q0Var.f32057e && this.f32058f == q0Var.f32058f && this.f32059g == q0Var.f32059g;
    }

    public final int hashCode() {
        int i10 = (((((this.f32053a ? 1 : 0) * 31) + (this.f32054b ? 1 : 0)) * 31) + this.f32055c) * 31;
        String str = this.f32060h;
        return ((((((((((((i10 + (str != null ? str.hashCode() : 0)) * 29791) + (this.f32056d ? 1 : 0)) * 31) + (this.f32057e ? 1 : 0)) * 31) + this.f32058f) * 31) + this.f32059g) * 31) - 1) * 31) - 1;
    }

    public final String toString() {
        String str = this.f32060h;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(q0.class.getSimpleName());
        sb2.append("(");
        if (this.f32053a) {
            sb2.append("launchSingleTop ");
        }
        if (this.f32054b) {
            sb2.append("restoreState ");
        }
        if ((str != null || this.f32055c != -1) && str != null) {
            sb2.append("popUpTo(");
            sb2.append(str);
            if (this.f32056d) {
                sb2.append(" inclusive");
            }
            if (this.f32057e) {
                sb2.append(" saveState");
            }
            sb2.append(")");
        }
        int i10 = this.f32059g;
        int i11 = this.f32058f;
        if (i11 != -1 || i10 != -1) {
            sb2.append("anim(enterAnim=0x");
            sb2.append(Integer.toHexString(i11));
            sb2.append(" exitAnim=0x");
            sb2.append(Integer.toHexString(i10));
            sb2.append(" popEnterAnim=0x");
            sb2.append(Integer.toHexString(-1));
            sb2.append(" popExitAnim=0x");
            sb2.append(Integer.toHexString(-1));
            sb2.append(")");
        }
        String string = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string, "sb.toString()");
        return string;
    }
}
