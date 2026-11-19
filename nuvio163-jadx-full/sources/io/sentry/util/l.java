package io.sentry.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes.dex */
public final class l implements Serializable {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final AtomicLong f26264e = new AtomicLong(System.nanoTime());

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f26265b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f26266c;

    public l() {
        long jA = a();
        long jA2 = (a() << 1) | 1;
        this.f26266c = jA2;
        this.f26265b = jA2 + jA;
    }

    public static long a() {
        AtomicLong atomicLong;
        long j10;
        long j11;
        do {
            atomicLong = f26264e;
            j10 = atomicLong.get();
            long j12 = (j10 >> 12) ^ j10;
            long j13 = j12 ^ (j12 << 25);
            j11 = (j13 ^ (j13 >> 27)) * 2685821657736338717L;
        } while (!atomicLong.compareAndSet(j10, j11));
        return j11;
    }

    public final void b(byte[] bArr) {
        for (int i10 = 0; i10 < bArr.length; i10++) {
            long j10 = (this.f26265b * 6364136223846793005L) + this.f26266c;
            this.f26265b = j10;
            bArr[i10] = (byte) ((((j10 >>> 22) ^ j10) >>> ((int) ((j10 >>> 61) + 22))) >>> 24);
        }
    }

    public final double c() {
        long j10 = this.f26265b * 6364136223846793005L;
        long j11 = this.f26266c;
        long j12 = j10 + j11;
        long j13 = (((j12 >>> 22) ^ j12) >>> ((int) ((j12 >>> 61) + 22))) & 4294967295L;
        long j14 = (j12 * 6364136223846793005L) + j11;
        this.f26265b = j14;
        return (((j13 >>> 6) << 27) + (((((j14 >>> 22) ^ j14) >>> ((int) ((j14 >>> 61) + 22))) & 4294967295L) >>> 5)) / 9.007199254740992E15d;
    }
}
