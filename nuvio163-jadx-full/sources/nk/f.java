package nk;

import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f42898a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f42899b;

    public f(byte[] bArr, int i10) {
        this.f42898a = rn.d.b(bArr);
        this.f42899b = i10;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof f)) {
            return false;
        }
        f fVar = (f) obj;
        if (fVar.f42899b != this.f42899b) {
            return false;
        }
        return Arrays.equals(this.f42898a, fVar.f42898a);
    }

    public final int hashCode() {
        return rn.d.l(this.f42898a) ^ this.f42899b;
    }
}
