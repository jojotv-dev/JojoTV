package f2;

import d2.m;
import kotlin.jvm.internal.Intrinsics;
import w.h0;

/* JADX INFO: loaded from: classes.dex */
public final class h extends e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f16138a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f16139b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f16140c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f16141d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final m f16142e;

    public h(float f10, float f11, int i10, int i11, m mVar, int i12) {
        f11 = (i12 & 2) != 0 ? 4.0f : f11;
        i10 = (i12 & 4) != 0 ? 0 : i10;
        i11 = (i12 & 8) != 0 ? 0 : i11;
        mVar = (i12 & 16) != 0 ? null : mVar;
        this.f16138a = f10;
        this.f16139b = f11;
        this.f16140c = i10;
        this.f16141d = i11;
        this.f16142e = mVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof h)) {
            return false;
        }
        h hVar = (h) obj;
        return this.f16138a == hVar.f16138a && this.f16139b == hVar.f16139b && this.f16140c == hVar.f16140c && this.f16141d == hVar.f16141d && Intrinsics.areEqual(this.f16142e, hVar.f16142e);
    }

    public final int hashCode() {
        int iA = h0.a(this.f16141d, h0.a(this.f16140c, o2.a.b(this.f16139b, Float.hashCode(this.f16138a) * 31, 31), 31), 31);
        m mVar = this.f16142e;
        return iA + (mVar != null ? mVar.hashCode() : 0);
    }

    public final String toString() {
        String str;
        StringBuilder sb2 = new StringBuilder("Stroke(width=");
        sb2.append(this.f16138a);
        sb2.append(", miter=");
        sb2.append(this.f16139b);
        sb2.append(", cap=");
        String str2 = "Unknown";
        int i10 = this.f16140c;
        if (i10 == 0) {
            str = "Butt";
        } else if (i10 == 1) {
            str = "Round";
        } else {
            str = i10 == 2 ? "Square" : "Unknown";
        }
        sb2.append((Object) str);
        sb2.append(", join=");
        int i11 = this.f16141d;
        if (i11 == 0) {
            str2 = "Miter";
        } else if (i11 == 1) {
            str2 = "Round";
        } else if (i11 == 2) {
            str2 = "Bevel";
        }
        sb2.append((Object) str2);
        sb2.append(", pathEffect=");
        sb2.append(this.f16142e);
        sb2.append(')');
        return sb2.toString();
    }
}
