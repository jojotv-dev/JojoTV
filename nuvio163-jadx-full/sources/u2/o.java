package u2;

/* JADX INFO: loaded from: classes.dex */
public final class o implements m {
    @Override // u2.m
    public final long a(long j10, long j11) {
        long jFloatToRawIntBits = (((long) Float.floatToRawIntBits(1.0f)) << 32) | (4294967295L & ((long) Float.floatToRawIntBits(1.0f)));
        int i10 = s1.f58206a;
        return jFloatToRawIntBits;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof o) && Float.compare(1.0f, 1.0f) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(1.0f);
    }

    public final String toString() {
        return "FixedScale(value=1.0)";
    }
}
