package t;

import kotlin.collections.ArraysKt;
import kotlin.collections.ArraysKt___ArraysJvmKt;

/* JADX INFO: loaded from: classes.dex */
public final class g0 extends q {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f56997e;

    public g0(int i10) {
        this.f57049a = e1.f56985a;
        this.f57050b = r.f57057a;
        if (i10 >= 0) {
            d(e1.d(i10));
        } else {
            u.a.c("Capacity must be a positive value.");
            throw null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v9, types: [int] */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v4, types: [int] */
    /* JADX WARN: Type inference failed for: r9v5 */
    public final boolean b(int i10) {
        long j10;
        boolean z2;
        long j11;
        int iNumberOfTrailingZeros;
        long[] jArr;
        long j12;
        long[] jArr2;
        int i11 = this.f57052d;
        int i12 = -862048943;
        int iHashCode = Integer.hashCode(i10) * (-862048943);
        int i13 = iHashCode ^ (iHashCode << 16);
        int i14 = i13 >>> 7;
        int i15 = i13 & 127;
        int i16 = this.f57051c;
        int i17 = i14 & i16;
        int i18 = 0;
        loop0: while (true) {
            long[] jArr3 = this.f57049a;
            int i19 = i17 >> 3;
            int i20 = (i17 & 7) << 3;
            boolean z10 = true;
            int i21 = i18;
            long j13 = (((-i20) >> 63) & (jArr3[i19 + 1] << (64 - i20))) | (jArr3[i19] >>> i20);
            long j14 = i15;
            long j15 = j13 ^ (j14 * 72340172838076673L);
            long j16 = (j15 - 72340172838076673L) & (~j15) & (-9187201950435737472L);
            while (j16 != 0) {
                iNumberOfTrailingZeros = ((Long.numberOfTrailingZeros(j16) >> 3) + i17) & i16;
                int i22 = i12;
                if (this.f57050b[iNumberOfTrailingZeros] == i10) {
                    z2 = true;
                    break loop0;
                }
                j16 &= j16 - 1;
                i12 = i22;
            }
            int i23 = i12;
            if ((j13 & ((~j13) << 6) & (-9187201950435737472L)) != 0) {
                int iC = c(i14);
                long j17 = 255;
                if (this.f56997e != 0 || ((this.f57049a[iC >> 3] >> ((iC & 7) << 3)) & 255) == 254) {
                    j10 = 255;
                    z2 = true;
                    j11 = 128;
                } else {
                    if (this.f57051c > 8) {
                        j11 = 128;
                        char c5 = '\b';
                        if (Long.compare(o2.a.f(this.f57052d, 32L) ^ Long.MIN_VALUE, o2.a.f(this.f57051c, 25L) ^ Long.MIN_VALUE) <= 0) {
                            long[] jArr4 = this.f57049a;
                            int i24 = this.f57051c;
                            int[] iArr = this.f57050b;
                            int i25 = (i24 + 7) >> 3;
                            int i26 = 0;
                            while (i26 < i25) {
                                long j18 = j17;
                                long j19 = jArr4[i26] & (-9187201950435737472L);
                                jArr4[i26] = (-72340172838076674L) & ((~j19) + (j19 >>> 7));
                                i26++;
                                z10 = z10;
                                c5 = c5;
                                j17 = j18;
                            }
                            j10 = j17;
                            boolean z11 = z10;
                            int lastIndex = ArraysKt.getLastIndex(jArr4);
                            int i27 = lastIndex - 1;
                            long j20 = 72057594037927935L;
                            jArr4[i27] = (jArr4[i27] & 72057594037927935L) | (-72057594037927936L);
                            jArr4[lastIndex] = jArr4[0];
                            int i28 = 0;
                            while (i28 != i24) {
                                int i29 = i28 >> 3;
                                int i30 = (i28 & 7) << 3;
                                long j21 = (jArr4[i29] >> i30) & j10;
                                if (j21 != 128 && j21 == 254) {
                                    int iHashCode2 = Integer.hashCode(iArr[i28]) * i23;
                                    int i31 = iHashCode2 ^ (iHashCode2 << 16);
                                    int i32 = i31 >>> 7;
                                    int iC2 = c(i32);
                                    int i33 = i32 & i24;
                                    z11 = z11;
                                    if (((iC2 - i33) & i24) / 8 == ((i28 - i33) & i24) / 8) {
                                        j12 = j20;
                                        jArr4[i29] = (((long) (i31 & 127)) << i30) | (jArr4[i29] & (~(j10 << i30)));
                                        jArr4[ArraysKt.getLastIndex(jArr4)] = (jArr4[0] & j12) | Long.MIN_VALUE;
                                        i28++;
                                    } else {
                                        j12 = j20;
                                        int i34 = iC2 >> 3;
                                        long j22 = jArr4[i34];
                                        int i35 = (iC2 & 7) << 3;
                                        if (((j22 >> i35) & j10) == 128) {
                                            jArr2 = jArr4;
                                            jArr2[i34] = ((~(j10 << i35)) & j22) | (((long) (i31 & 127)) << i35);
                                            jArr2[i29] = (jArr2[i29] & (~(j10 << i30))) | (128 << i30);
                                            iArr[iC2] = iArr[i28];
                                            iArr[i28] = 0;
                                        } else {
                                            jArr2 = jArr4;
                                            jArr2[i34] = ((~(j10 << i35)) & j22) | (((long) (i31 & 127)) << i35);
                                            int i36 = iArr[iC2];
                                            iArr[iC2] = iArr[i28];
                                            iArr[i28] = i36;
                                            i28--;
                                        }
                                        jArr2[ArraysKt.getLastIndex(jArr2)] = (jArr2[0] & j12) | Long.MIN_VALUE;
                                        i28++;
                                        jArr4 = jArr2;
                                        i24 = i24;
                                    }
                                    j20 = j12;
                                } else {
                                    i28++;
                                }
                            }
                            z2 = z11;
                            this.f56997e = e1.a(this.f57051c) - this.f57052d;
                        }
                        iC = c(i14);
                    } else {
                        j11 = 128;
                    }
                    j10 = 255;
                    z2 = true;
                    int iB = e1.b(this.f57051c);
                    long[] jArr5 = this.f57049a;
                    int[] iArr2 = this.f57050b;
                    int i37 = this.f57051c;
                    d(iB);
                    long[] jArr6 = this.f57049a;
                    int[] iArr3 = this.f57050b;
                    int i38 = this.f57051c;
                    int i39 = 0;
                    while (i39 < i37) {
                        if (((jArr5[i39 >> 3] >> ((i39 & 7) << 3)) & 255) < j11) {
                            int i40 = iArr2[i39];
                            int iHashCode3 = Integer.hashCode(i40) * i23;
                            int i41 = iHashCode3 ^ (iHashCode3 << 16);
                            int iC3 = c(i41 >>> 7);
                            jArr = jArr6;
                            long j23 = i41 & 127;
                            int i42 = iC3 >> 3;
                            int i43 = (iC3 & 7) << 3;
                            long j24 = (jArr[i42] & (~(255 << i43))) | (j23 << i43);
                            jArr[i42] = j24;
                            jArr[(((iC3 - 7) & i38) + (i38 & 7)) >> 3] = j24;
                            iArr3[iC3] = i40;
                        } else {
                            jArr = jArr6;
                        }
                        i39++;
                        jArr5 = jArr5;
                        jArr6 = jArr;
                    }
                    iC = c(i14);
                }
                iNumberOfTrailingZeros = iC;
                this.f57052d++;
                int i44 = this.f56997e;
                long[] jArr7 = this.f57049a;
                int i45 = iNumberOfTrailingZeros >> 3;
                long j25 = jArr7[i45];
                int i46 = (iNumberOfTrailingZeros & 7) << 3;
                this.f56997e = i44 - (((j25 >> i46) & j10) == j11 ? z2 : 0);
                int i47 = this.f57051c;
                long j26 = (j25 & (~(j10 << i46))) | (j14 << i46);
                jArr7[i45] = j26;
                jArr7[(((iNumberOfTrailingZeros - 7) & i47) + (i47 & 7)) >> 3] = j26;
                break;
            }
            i18 = i21 + 8;
            i17 = (i17 + i18) & i16;
            i12 = i23;
        }
        this.f57050b[iNumberOfTrailingZeros] = i10;
        if (this.f57052d != i11) {
            return z2;
        }
        return false;
    }

    public final int c(int i10) {
        int i11 = this.f57051c;
        int i12 = i10 & i11;
        int i13 = 0;
        while (true) {
            long[] jArr = this.f57049a;
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

    public final void d(int i10) {
        long[] jArr;
        int iMax = i10 > 0 ? Math.max(7, e1.c(i10)) : 0;
        this.f57051c = iMax;
        if (iMax == 0) {
            jArr = e1.f56985a;
        } else {
            jArr = new long[((iMax + 15) & (-8)) >> 3];
            ArraysKt___ArraysJvmKt.fill$default(jArr, -9187201950435737472L, 0, 0, 6, (Object) null);
        }
        this.f57049a = jArr;
        int i11 = iMax >> 3;
        long j10 = 255 << ((iMax & 7) << 3);
        jArr[i11] = (jArr[i11] & (~j10)) | j10;
        this.f56997e = e1.a(this.f57051c) - this.f57052d;
        this.f57050b = new int[iMax];
    }

    public final boolean e(int i10) {
        int iNumberOfTrailingZeros;
        int iHashCode = Integer.hashCode(i10) * (-862048943);
        int i11 = iHashCode ^ (iHashCode << 16);
        int i12 = i11 & 127;
        int i13 = this.f57051c;
        int i14 = (i11 >>> 7) & i13;
        int i15 = 0;
        loop0: while (true) {
            long[] jArr = this.f57049a;
            int i16 = i14 >> 3;
            int i17 = (i14 & 7) << 3;
            long j10 = ((jArr[i16 + 1] << (64 - i17)) & ((-i17) >> 63)) | (jArr[i16] >>> i17);
            long j11 = (((long) i12) * 72340172838076673L) ^ j10;
            for (long j12 = (~j11) & (j11 - 72340172838076673L) & (-9187201950435737472L); j12 != 0; j12 &= j12 - 1) {
                iNumberOfTrailingZeros = ((Long.numberOfTrailingZeros(j12) >> 3) + i14) & i13;
                if (this.f57050b[iNumberOfTrailingZeros] == i10) {
                    break loop0;
                }
            }
            if ((j10 & ((~j10) << 6) & (-9187201950435737472L)) != 0) {
                iNumberOfTrailingZeros = -1;
                break;
            }
            i15 += 8;
            i14 = (i14 + i15) & i13;
        }
        boolean z2 = iNumberOfTrailingZeros >= 0;
        if (z2) {
            f(iNumberOfTrailingZeros);
        }
        return z2;
    }

    public final void f(int i10) {
        this.f57052d--;
        long[] jArr = this.f57049a;
        int i11 = this.f57051c;
        int i12 = i10 >> 3;
        int i13 = (i10 & 7) << 3;
        long j10 = (jArr[i12] & (~(255 << i13))) | (254 << i13);
        jArr[i12] = j10;
        jArr[(((i10 - 7) & i11) + (i11 & 7)) >> 3] = j10;
    }

    public /* synthetic */ g0() {
        this(6);
    }
}
