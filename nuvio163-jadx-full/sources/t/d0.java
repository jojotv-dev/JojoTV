package t;

import kotlin.collections.ArraysKt;
import kotlin.collections.ArraysKt___ArraysJvmKt;

/* JADX INFO: loaded from: classes.dex */
public final class d0 extends j {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f56978f;

    public d0(int i10) {
        this.f57013a = e1.f56985a;
        int[] iArr = r.f57057a;
        this.f57014b = iArr;
        this.f57015c = iArr;
        if (i10 >= 0) {
            e(e1.d(i10));
        } else {
            u.a.c("Capacity must be a positive value.");
            throw null;
        }
    }

    public final void c() {
        this.f57017e = 0;
        long[] jArr = this.f57013a;
        if (jArr != e1.f56985a) {
            ArraysKt___ArraysJvmKt.fill$default(jArr, -9187201950435737472L, 0, 0, 6, (Object) null);
            long[] jArr2 = this.f57013a;
            int i10 = this.f57016d;
            int i11 = i10 >> 3;
            long j10 = 255 << ((i10 & 7) << 3);
            jArr2[i11] = (jArr2[i11] & (~j10)) | j10;
        }
        this.f56978f = e1.a(this.f57016d) - this.f57017e;
    }

    public final int d(int i10) {
        int i11 = this.f57016d;
        int i12 = i10 & i11;
        int i13 = 0;
        while (true) {
            long[] jArr = this.f57013a;
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

    public final void e(int i10) {
        long[] jArr;
        int iMax = i10 > 0 ? Math.max(7, e1.c(i10)) : 0;
        this.f57016d = iMax;
        if (iMax == 0) {
            jArr = e1.f56985a;
        } else {
            jArr = new long[((iMax + 15) & (-8)) >> 3];
            ArraysKt___ArraysJvmKt.fill$default(jArr, -9187201950435737472L, 0, 0, 6, (Object) null);
        }
        this.f57013a = jArr;
        int i11 = iMax >> 3;
        long j10 = 255 << ((iMax & 7) << 3);
        jArr[i11] = (jArr[i11] & (~j10)) | j10;
        this.f56978f = e1.a(this.f57016d) - this.f57017e;
        this.f57014b = new int[iMax];
        this.f57015c = new int[iMax];
    }

    public final void f(int i10, int i11) {
        long j10;
        int i12;
        long j11;
        int iNumberOfTrailingZeros;
        long[] jArr;
        long[] jArr2;
        int i13 = i10;
        int i14 = -862048943;
        int iHashCode = Integer.hashCode(i13) * (-862048943);
        int i15 = iHashCode ^ (iHashCode << 16);
        int i16 = i15 >>> 7;
        int i17 = i15 & 127;
        int i18 = this.f57016d;
        int i19 = i16 & i18;
        int i20 = 0;
        loop0: while (true) {
            long[] jArr3 = this.f57013a;
            int i21 = i19 >> 3;
            int i22 = (i19 & 7) << 3;
            int i23 = 1;
            int i24 = i20;
            long j12 = (((-i22) >> 63) & (jArr3[i21 + 1] << (64 - i22))) | (jArr3[i21] >>> i22);
            long j13 = i17;
            long j14 = j12 ^ (j13 * 72340172838076673L);
            long j15 = (j14 - 72340172838076673L) & (~j14) & (-9187201950435737472L);
            while (j15 != 0) {
                iNumberOfTrailingZeros = ((Long.numberOfTrailingZeros(j15) >> 3) + i19) & i18;
                int i25 = i14;
                if (this.f57014b[iNumberOfTrailingZeros] == i13) {
                    break loop0;
                }
                j15 &= j15 - 1;
                i14 = i25;
            }
            int i26 = i14;
            if ((j12 & ((~j12) << 6) & (-9187201950435737472L)) != 0) {
                int iD = d(i16);
                long j16 = 255;
                if (this.f56978f != 0 || ((this.f57013a[iD >> 3] >> ((iD & 7) << 3)) & 255) == 254) {
                    j10 = 255;
                    i12 = 1;
                    j11 = 128;
                } else {
                    if (this.f57016d > 8) {
                        j11 = 128;
                        char c5 = '\b';
                        if (Long.compare(o2.a.f(this.f57017e, 32L) ^ Long.MIN_VALUE, o2.a.f(this.f57016d, 25L) ^ Long.MIN_VALUE) <= 0) {
                            long[] jArr4 = this.f57013a;
                            int i27 = this.f57016d;
                            int[] iArr = this.f57014b;
                            int[] iArr2 = this.f57015c;
                            int i28 = (i27 + 7) >> 3;
                            int i29 = 0;
                            while (i29 < i28) {
                                long j17 = j16;
                                long j18 = jArr4[i29] & (-9187201950435737472L);
                                jArr4[i29] = (-72340172838076674L) & ((~j18) + (j18 >>> 7));
                                i29++;
                                i23 = i23;
                                c5 = c5;
                                j16 = j17;
                            }
                            j10 = j16;
                            i12 = i23;
                            int lastIndex = ArraysKt.getLastIndex(jArr4);
                            int i30 = lastIndex - 1;
                            long j19 = 72057594037927935L;
                            jArr4[i30] = (jArr4[i30] & 72057594037927935L) | (-72057594037927936L);
                            jArr4[lastIndex] = jArr4[0];
                            int i31 = 0;
                            while (i31 != i27) {
                                int i32 = i31 >> 3;
                                int i33 = (i31 & 7) << 3;
                                long j20 = (jArr4[i32] >> i33) & j10;
                                if (j20 != 128 && j20 == 254) {
                                    int iHashCode2 = Integer.hashCode(iArr[i31]) * i26;
                                    int i34 = iHashCode2 ^ (iHashCode2 << 16);
                                    int i35 = i34 >>> 7;
                                    int iD2 = d(i35);
                                    int i36 = i35 & i27;
                                    long j21 = j19;
                                    if (((iD2 - i36) & i27) / 8 == ((i31 - i36) & i27) / 8) {
                                        jArr2 = jArr4;
                                        jArr2[i32] = ((~(j10 << i33)) & jArr4[i32]) | (((long) (i34 & 127)) << i33);
                                        jArr2[ArraysKt.getLastIndex(jArr2)] = (jArr2[0] & j21) | Long.MIN_VALUE;
                                    } else {
                                        jArr2 = jArr4;
                                        int i37 = iD2 >> 3;
                                        long j22 = jArr2[i37];
                                        int i38 = (iD2 & 7) << 3;
                                        if (((j22 >> i38) & j10) == 128) {
                                            jArr2[i37] = ((~(j10 << i38)) & j22) | (((long) (i34 & 127)) << i38);
                                            jArr2[i32] = (jArr2[i32] & (~(j10 << i33))) | (128 << i33);
                                            iArr[iD2] = iArr[i31];
                                            iArr[i31] = 0;
                                            iArr2[iD2] = iArr2[i31];
                                            iArr2[i31] = 0;
                                        } else {
                                            jArr2[i37] = ((~(j10 << i38)) & j22) | (((long) (i34 & 127)) << i38);
                                            int i39 = iArr[iD2];
                                            iArr[iD2] = iArr[i31];
                                            iArr[i31] = i39;
                                            int i40 = iArr2[iD2];
                                            iArr2[iD2] = iArr2[i31];
                                            iArr2[i31] = i40;
                                            i31--;
                                        }
                                        jArr2[ArraysKt.getLastIndex(jArr2)] = (jArr2[0] & j21) | Long.MIN_VALUE;
                                    }
                                    i31++;
                                    jArr4 = jArr2;
                                    j19 = j21;
                                } else {
                                    i31++;
                                }
                            }
                            this.f56978f = e1.a(this.f57016d) - this.f57017e;
                        }
                        iD = d(i16);
                    } else {
                        j11 = 128;
                    }
                    j10 = 255;
                    i12 = 1;
                    int iB = e1.b(this.f57016d);
                    long[] jArr5 = this.f57013a;
                    int[] iArr3 = this.f57014b;
                    int[] iArr4 = this.f57015c;
                    int i41 = this.f57016d;
                    e(iB);
                    long[] jArr6 = this.f57013a;
                    int[] iArr5 = this.f57014b;
                    int[] iArr6 = this.f57015c;
                    int i42 = this.f57016d;
                    int i43 = 0;
                    while (i43 < i41) {
                        if (((jArr5[i43 >> 3] >> ((i43 & 7) << 3)) & 255) < j11) {
                            int i44 = iArr3[i43];
                            int iHashCode3 = Integer.hashCode(i44) * i26;
                            int i45 = iHashCode3 ^ (iHashCode3 << 16);
                            int iD3 = d(i45 >>> 7);
                            jArr = jArr6;
                            long j23 = i45 & 127;
                            int i46 = iD3 >> 3;
                            int i47 = (iD3 & 7) << 3;
                            long j24 = (jArr[i46] & (~(255 << i47))) | (j23 << i47);
                            jArr[i46] = j24;
                            jArr[(((iD3 - 7) & i42) + (i42 & 7)) >> 3] = j24;
                            iArr5[iD3] = i44;
                            iArr6[iD3] = iArr4[i43];
                        } else {
                            jArr = jArr6;
                        }
                        i43++;
                        jArr5 = jArr5;
                        jArr6 = jArr;
                    }
                    iD = d(i16);
                }
                this.f57017e++;
                int i48 = this.f56978f;
                long[] jArr7 = this.f57013a;
                int i49 = iD >> 3;
                long j25 = jArr7[i49];
                int i50 = (iD & 7) << 3;
                this.f56978f = i48 - (((j25 >> i50) & j10) == j11 ? i12 : 0);
                int i51 = this.f57016d;
                long j26 = (j25 & (~(j10 << i50))) | (j13 << i50);
                jArr7[i49] = j26;
                jArr7[(((iD - 7) & i51) + (i51 & 7)) >> 3] = j26;
                iNumberOfTrailingZeros = ~iD;
                break;
            }
            i20 = i24 + 8;
            i19 = (i19 + i20) & i18;
            i13 = i10;
            i14 = i26;
        }
        if (iNumberOfTrailingZeros < 0) {
            iNumberOfTrailingZeros = ~iNumberOfTrailingZeros;
        }
        this.f57014b[iNumberOfTrailingZeros] = i10;
        this.f57015c[iNumberOfTrailingZeros] = i11;
    }

    public /* synthetic */ d0() {
        this(6);
    }
}
