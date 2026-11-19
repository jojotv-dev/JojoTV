package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import w.h0;

/* JADX INFO: loaded from: classes3.dex */
class BigSignificand {
    private static final long LONG_MASK = 4294967295L;
    private int firstNonZeroInt;
    private final int numInts;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    private final int[] f7489x;

    public BigSignificand(long j10) {
        if (j10 <= 0 || j10 >= 2147483647L) {
            throw new IllegalArgumentException(h0.j(j10, "numBits="));
        }
        int i10 = (((int) ((j10 + 63) >>> 6)) + 1) << 1;
        this.numInts = i10;
        this.f7489x = new int[i10];
        this.firstNonZeroInt = i10;
    }

    private void x(int i10, int i11) {
        this.f7489x[i10] = i11;
    }

    public void add(int i10) {
        if (i10 == 0) {
            return;
        }
        long j10 = ((long) i10) & LONG_MASK;
        int i11 = this.numInts;
        while (true) {
            i11--;
            if (j10 == 0) {
                this.firstNonZeroInt = Math.min(this.firstNonZeroInt, i11 + 1);
                return;
            } else {
                long jX = (((long) x(i11)) & LONG_MASK) + j10;
                x(i11, (int) jX);
                j10 = jX >>> 32;
            }
        }
    }

    public void fma(int i10, int i11) {
        long j10 = ((long) i10) & LONG_MASK;
        long j11 = i11;
        int i12 = this.numInts;
        while (true) {
            i12--;
            if (i12 < this.firstNonZeroInt) {
                break;
            }
            long jX = ((((long) x(i12)) & LONG_MASK) * j10) + j11;
            x(i12, (int) jX);
            j11 = jX >>> 32;
        }
        if (j11 != 0) {
            x(i12, (int) j11);
            this.firstNonZeroInt = i12;
        }
    }

    public BigInteger toBigInteger() {
        byte[] bArr = new byte[this.f7489x.length << 2];
        IntBuffer intBufferAsIntBuffer = ByteBuffer.wrap(bArr).asIntBuffer();
        int i10 = 0;
        while (true) {
            int[] iArr = this.f7489x;
            if (i10 >= iArr.length) {
                return new BigInteger(bArr);
            }
            intBufferAsIntBuffer.put(i10, iArr[i10]);
            i10++;
        }
    }

    private int x(int i10) {
        return this.f7489x[i10];
    }
}
