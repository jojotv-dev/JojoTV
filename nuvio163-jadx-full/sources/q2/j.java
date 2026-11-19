package q2;

import kotlin.UShort;

/* JADX INFO: loaded from: classes3.dex */
public final class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f45549a;

    public /* synthetic */ j(long j10) {
        this.f45549a = j10;
    }

    public static final /* synthetic */ j a(long j10) {
        return new j(j10);
    }

    public static long b(long j10, long j11) {
        return ((j10 & 2147483647L) << 1) | 1 | (((long) ((((short) Float.intBitsToFloat((int) (j11 & 4294967295L))) & UShort.MAX_VALUE) | (((short) Float.intBitsToFloat((int) (j11 >> 32))) << 16))) << 32);
    }

    public static final boolean c(long j10) {
        return (j10 & 1) != 0;
    }

    public static final long d(long j10) {
        int i10 = (int) (j10 >>> 32);
        return (((long) Float.floatToRawIntBits((short) (i10 & 65535))) & 4294967295L) | (Float.floatToRawIntBits((short) (i10 >>> 16)) << 32);
    }

    public static final long e(long j10) {
        return (j10 >> 1) & 2147483647L;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof j) {
            return this.f45549a == ((j) obj).f45549a;
        }
        return false;
    }

    public final /* synthetic */ long f() {
        return this.f45549a;
    }

    public final int hashCode() {
        return Long.hashCode(this.f45549a);
    }

    public final String toString() {
        return "IndirectPointerEventData(packedValue=" + this.f45549a + ')';
    }
}
