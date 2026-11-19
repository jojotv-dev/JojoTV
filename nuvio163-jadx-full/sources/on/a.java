package on;

import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes5.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final byte[][] f43902a = (byte[][]) Array.newInstance((Class<?>) Byte.TYPE, 256, 256);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final byte[] f43903b = new byte[256];

    static {
        long j10;
        int i10 = 1;
        long j11 = 72340172838076673L;
        while (true) {
            j10 = 506097522914230528L;
            if (i10 > 255) {
                break;
            }
            for (int i11 = 0; i11 < 256; i11 += 8) {
                rn.d.w(b(j11, j10), f43902a[i10], i11);
                j10 += 578721382704613384L;
            }
            j11 += 72340172838076673L;
            i10++;
        }
        for (int i12 = 0; i12 < 256; i12 += 8) {
            long jC = c(j10);
            long jC2 = c(jC);
            long jB = b(b(jC2, jC), c(jC2));
            rn.d.w(b(jC, c(b(c(c(c(jB))), jB))), f43903b, i12);
            j10 += 578721382704613384L;
        }
    }

    public static long a(long j10, long j11) {
        long j12 = j10 & j11;
        long j13 = (((((j10 << 1) & j11) ^ ((j11 << 1) & j10)) & (-6148914691236517206L)) ^ j12) ^ ((j12 & (-6148914691236517206L)) >>> 1);
        long j14 = 3689348814741910323L & j13;
        long j15 = ((j10 ^ (j10 << 2)) & (-3689348814741910324L)) ^ ((j13 & (-3689348814741910324L)) >>> 2);
        long j16 = ((j11 ^ (j11 << 2)) & (-3689348814741910324L)) ^ 2459565876494606882L;
        long j17 = (((j15 << 1) & j16) ^ ((j16 << 1) & j15)) & (-6148914691236517206L);
        long j18 = j15 & j16;
        return ((((j18 & (-6148914691236517206L)) >>> 1) ^ (j18 ^ j17)) ^ (j14 << 2)) ^ j14;
    }

    public static long b(long j10, long j11) {
        long jA = a(j10, j11);
        long j12 = 1085102592571150095L & jA;
        return (a(((j10 ^ (j10 << 4)) & (-1085102592571150096L)) ^ ((jA & (-1085102592571150096L)) >>> 4), ((j11 ^ (j11 << 4)) & (-1085102592571150096L)) ^ 578721382704613384L) ^ (j12 << 4)) ^ j12;
    }

    public static long c(long j10) {
        long j11 = j10 ^ ((j10 & (-6148914691236517206L)) >>> 1);
        long j12 = (-8608480567731124088L) & j11;
        long j13 = j11 ^ (((((4919131752989213764L & j11) << 1) ^ j12) ^ (j12 >>> 1)) >>> 2);
        long j14 = (-4557430888798830400L) & j13;
        long j15 = j14 >>> 2;
        long j16 = (((3472328296227680304L & j13) << 2) ^ j14) ^ j15;
        long j17 = (-6148914691236517206L) & j16;
        return j13 ^ ((((j17 >>> 1) ^ (((6148914691236517205L & j16) << 1) ^ j17)) ^ j15) >>> 4);
    }
}
