package x0;

/* JADX INFO: loaded from: classes.dex */
public abstract class s0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final float f62911a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final float f62912b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e3.c0 f62913c = new e3.c0("SelectionHandleInfo");

    static {
        float f10 = 25;
        f62911a = f10;
        f62912b = f10;
    }

    public static final long a(long j10) {
        float fIntBitsToFloat = Float.intBitsToFloat((int) (j10 >> 32));
        return (((long) Float.floatToRawIntBits(Float.intBitsToFloat((int) (j10 & 4294967295L)) - 1.0f)) & 4294967295L) | (Float.floatToRawIntBits(fIntBitsToFloat) << 32);
    }
}
