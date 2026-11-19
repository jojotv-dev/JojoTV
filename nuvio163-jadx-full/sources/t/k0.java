package t;

import kotlin.collections.ArraysKt___ArraysJvmKt;

/* JADX INFO: loaded from: classes.dex */
public final class k0 extends x {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f57022e;

    public k0(int i10) {
        this.f57095a = e1.f56985a;
        this.f57096b = y.f57100a;
        if (i10 >= 0) {
            c(e1.d(i10));
        } else {
            u.a.c("Capacity must be a positive value.");
            throw null;
        }
    }

    public final int b(int i10) {
        int i11 = this.f57097c;
        int i12 = i10 & i11;
        int i13 = 0;
        while (true) {
            long[] jArr = this.f57095a;
            int i14 = i12 >> 3;
            int i15 = (i12 & 7) << 3;
            long j10 = ((jArr[i14 + 1] << (64 - i15)) & ((-i15) >> 63)) | (jArr[i14] >>> i15);
            long j11 = j10 & ((~j10) << 7) & (-9187201950435737472L);
            if (j11 != 0) {
                return (i12 + (Long.numberOfTrailingZeros(j11) >> 3)) & i11;
            }
            i13 += 8;
            i12 = (i12 + i13) & i11;
        }
    }

    public final void c(int i10) {
        long[] jArr;
        int iMax = i10 > 0 ? Math.max(7, e1.c(i10)) : 0;
        this.f57097c = iMax;
        if (iMax == 0) {
            jArr = e1.f56985a;
        } else {
            jArr = new long[((iMax + 15) & (-8)) >> 3];
            ArraysKt___ArraysJvmKt.fill$default(jArr, -9187201950435737472L, 0, 0, 6, (Object) null);
        }
        this.f57095a = jArr;
        int i11 = iMax >> 3;
        long j10 = 255 << ((iMax & 7) << 3);
        jArr[i11] = (jArr[i11] & (~j10)) | j10;
        this.f57022e = e1.a(this.f57097c) - this.f57098d;
        this.f57096b = new long[iMax];
    }
}
