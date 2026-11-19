package u2;

/* JADX INFO: loaded from: classes.dex */
public final class b1 implements z1, m {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final b1 f58073c = new b1(0);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f58074b;

    public /* synthetic */ b1(int i10) {
        this.f58074b = i10;
    }

    @Override // u2.m
    public long a(long j10, long j11) {
        switch (this.f58074b) {
            case 1:
                float fMax = Math.max(Float.intBitsToFloat((int) (j11 >> 32)) / Float.intBitsToFloat((int) (j10 >> 32)), Float.intBitsToFloat((int) (j11 & 4294967295L)) / Float.intBitsToFloat((int) (j10 & 4294967295L)));
                long jFloatToRawIntBits = (((long) Float.floatToRawIntBits(fMax)) << 32) | (((long) Float.floatToRawIntBits(fMax)) & 4294967295L);
                int i10 = s1.f58206a;
                return jFloatToRawIntBits;
            case 2:
                float fIntBitsToFloat = Float.intBitsToFloat((int) (j11 >> 32)) / Float.intBitsToFloat((int) (j10 >> 32));
                float fIntBitsToFloat2 = Float.intBitsToFloat((int) (j11 & 4294967295L)) / Float.intBitsToFloat((int) (j10 & 4294967295L));
                long jFloatToRawIntBits2 = (((long) Float.floatToRawIntBits(fIntBitsToFloat)) << 32) | (((long) Float.floatToRawIntBits(fIntBitsToFloat2)) & 4294967295L);
                int i11 = s1.f58206a;
                return jFloatToRawIntBits2;
            case 3:
                float fC = d0.c(j10, j11);
                long jFloatToRawIntBits3 = (((long) Float.floatToRawIntBits(fC)) << 32) | (((long) Float.floatToRawIntBits(fC)) & 4294967295L);
                int i12 = s1.f58206a;
                return jFloatToRawIntBits3;
            default:
                if (Float.intBitsToFloat((int) (j10 >> 32)) <= Float.intBitsToFloat((int) (j11 >> 32)) && Float.intBitsToFloat((int) (j10 & 4294967295L)) <= Float.intBitsToFloat((int) (j11 & 4294967295L))) {
                    long jFloatToRawIntBits4 = (((long) Float.floatToRawIntBits(1.0f)) << 32) | (((long) Float.floatToRawIntBits(1.0f)) & 4294967295L);
                    int i13 = s1.f58206a;
                    return jFloatToRawIntBits4;
                }
                float fC2 = d0.c(j10, j11);
                long jFloatToRawIntBits5 = (((long) Float.floatToRawIntBits(fC2)) << 32) | (((long) Float.floatToRawIntBits(fC2)) & 4294967295L);
                int i14 = s1.f58206a;
                return jFloatToRawIntBits5;
        }
    }

    @Override // u2.z1
    public boolean p(Object obj, Object obj2) {
        return false;
    }

    @Override // u2.z1
    public void r(t.l1 l1Var) {
        l1Var.clear();
    }

    public String toString() {
        switch (this.f58074b) {
            case 5:
                return "ReusedSlotId";
            default:
                return super.toString();
        }
    }
}
