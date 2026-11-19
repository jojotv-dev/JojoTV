package t;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class s {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public long[] f57060a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long[] f57061b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int[] f57062c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f57063d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f57064e;

    public final int a(long j10) {
        int iHashCode = Long.hashCode(j10) * (-862048943);
        int i10 = iHashCode ^ (iHashCode << 16);
        int i11 = i10 & 127;
        int i12 = this.f57063d;
        int i13 = (i10 >>> 7) & i12;
        int i14 = 0;
        while (true) {
            long[] jArr = this.f57060a;
            int i15 = i13 >> 3;
            int i16 = (i13 & 7) << 3;
            long j11 = ((jArr[i15 + 1] << (64 - i16)) & ((-i16) >> 63)) | (jArr[i15] >>> i16);
            long j12 = (((long) i11) * 72340172838076673L) ^ j11;
            for (long j13 = (~j12) & (j12 - 72340172838076673L) & (-9187201950435737472L); j13 != 0; j13 &= j13 - 1) {
                int iNumberOfTrailingZeros = ((Long.numberOfTrailingZeros(j13) >> 3) + i13) & i12;
                if (this.f57061b[iNumberOfTrailingZeros] == j10) {
                    return iNumberOfTrailingZeros;
                }
            }
            if ((j11 & ((~j11) << 6) & (-9187201950435737472L)) != 0) {
                return -1;
            }
            i14 += 8;
            i13 = (i13 + i14) & i12;
        }
    }

    public final int b(long j10) {
        int iA = a(j10);
        if (iA >= 0) {
            return this.f57062c[iA];
        }
        u.a.e("Cannot find value for key " + j10);
        throw null;
    }

    public final boolean equals(Object obj) {
        boolean z2;
        long[] jArr;
        long[] jArr2;
        boolean z10 = true;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof s)) {
            return false;
        }
        s sVar = (s) obj;
        if (sVar.f57064e != this.f57064e) {
            return false;
        }
        long[] jArr3 = this.f57061b;
        int[] iArr = this.f57062c;
        long[] jArr4 = this.f57060a;
        int length = jArr4.length - 2;
        if (length < 0) {
            return true;
        }
        int i10 = 0;
        loop0: while (true) {
            long j10 = jArr4[i10];
            if ((((~j10) << 7) & j10 & (-9187201950435737472L)) != -9187201950435737472L) {
                int i11 = 8 - ((~(i10 - length)) >>> 31);
                int i12 = 0;
                while (i12 < i11) {
                    if ((255 & j10) < 128) {
                        int i13 = (i10 << 3) + i12;
                        jArr2 = jArr3;
                        long j11 = jArr2[i13];
                        int i14 = iArr[i13];
                        int iA = sVar.a(j11);
                        if (iA < 0 || i14 != sVar.f57062c[iA]) {
                            break loop0;
                        }
                    } else {
                        jArr2 = jArr3;
                    }
                    j10 >>= 8;
                    i12++;
                    z10 = z10;
                    jArr3 = jArr2;
                }
                z2 = z10;
                jArr = jArr3;
                if (i11 != 8) {
                    return z2;
                }
            } else {
                z2 = z10;
                jArr = jArr3;
            }
            if (i10 == length) {
                return z2;
            }
            i10++;
            z10 = z2;
            jArr3 = jArr;
        }
        return false;
    }

    public final int hashCode() {
        long[] jArr = this.f57061b;
        int[] iArr = this.f57062c;
        long[] jArr2 = this.f57060a;
        int length = jArr2.length - 2;
        if (length < 0) {
            return 0;
        }
        int i10 = 0;
        int iHashCode = 0;
        while (true) {
            long j10 = jArr2[i10];
            if ((((~j10) << 7) & j10 & (-9187201950435737472L)) != -9187201950435737472L) {
                int i11 = 8 - ((~(i10 - length)) >>> 31);
                for (int i12 = 0; i12 < i11; i12++) {
                    if ((255 & j10) < 128) {
                        int i13 = (i10 << 3) + i12;
                        long j11 = jArr[i13];
                        iHashCode += Integer.hashCode(iArr[i13]) ^ Long.hashCode(j11);
                    }
                    j10 >>= 8;
                }
                if (i11 != 8) {
                    return iHashCode;
                }
            }
            if (i10 == length) {
                return iHashCode;
            }
            i10++;
        }
    }

    public final String toString() {
        int i10;
        int i11;
        if (this.f57064e == 0) {
            return "{}";
        }
        StringBuilder sb2 = new StringBuilder("{");
        long[] jArr = this.f57061b;
        int[] iArr = this.f57062c;
        long[] jArr2 = this.f57060a;
        int length = jArr2.length - 2;
        if (length >= 0) {
            int i12 = 0;
            int i13 = 0;
            while (true) {
                long j10 = jArr2[i12];
                if ((((~j10) << 7) & j10 & (-9187201950435737472L)) != -9187201950435737472L) {
                    int i14 = 8 - ((~(i12 - length)) >>> 31);
                    int i15 = 0;
                    while (i15 < i14) {
                        if ((255 & j10) < 128) {
                            int i16 = (i12 << 3) + i15;
                            i11 = i12;
                            long j11 = jArr[i16];
                            int i17 = iArr[i16];
                            sb2.append(j11);
                            sb2.append("=");
                            sb2.append(i17);
                            i13++;
                            if (i13 < this.f57064e) {
                                sb2.append(", ");
                            }
                        } else {
                            i11 = i12;
                        }
                        j10 >>= 8;
                        i15++;
                        i12 = i11;
                    }
                    int i18 = i12;
                    if (i14 != 8) {
                        break;
                    }
                    i10 = i18;
                } else {
                    i10 = i12;
                }
                if (i10 == length) {
                    break;
                }
                i12 = i10 + 1;
            }
        }
        sb2.append('}');
        String string = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
