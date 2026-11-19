package nk;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends h {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final byte[] f42890c = {1, 1, 1, 1, 1, 1, 1, 1, 31, 31, 31, 31, 14, 14, 14, 14, -32, -32, -32, -32, -15, -15, -15, -15, -2, -2, -2, -2, -2, -2, -2, -2, 1, -2, 1, -2, 1, -2, 1, -2, 31, -32, 31, -32, 14, -15, 14, -15, 1, -32, 1, -32, 1, -15, 1, -15, 31, -2, 31, -2, 14, -2, 14, -2, 1, 31, 1, 31, 1, 14, 1, 14, -32, -2, -32, -2, -15, -2, -15, -2, -2, 1, -2, 1, -2, 1, -2, 1, -32, 31, -32, 31, -15, 14, -15, 14, -32, 1, -32, 1, -15, 1, -15, 1, -2, 31, -2, 31, -2, 14, -2, 14, 31, 1, 31, 1, 14, 1, 14, 1, -2, -32, -2, -32, -2, -15, -2, -15};

    public static boolean a(int i10, byte[] bArr) {
        if (i10 > bArr.length - 8) {
            throw new IllegalArgumentException("key material too short.");
        }
        for (int i11 = 0; i11 < 16; i11++) {
            if (rn.d.f(bArr, 8, i10, f42890c, i11 * 8)) {
                return true;
            }
        }
        return false;
    }

    public static void b(byte[] bArr) {
        for (int i10 = 0; i10 < bArr.length; i10++) {
            byte b5 = bArr[i10];
            bArr[i10] = (byte) (((((b5 >> 7) ^ ((((((b5 >> 1) ^ (b5 >> 2)) ^ (b5 >> 3)) ^ (b5 >> 4)) ^ (b5 >> 5)) ^ (b5 >> 6))) ^ 1) & 1) | (b5 & 254));
        }
    }
}
