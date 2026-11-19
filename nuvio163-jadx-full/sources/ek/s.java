package ek;

/* JADX INFO: loaded from: classes5.dex */
public abstract class s {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public byte[] f15775a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f15776b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f15777c;

    public static byte[] a(char[] cArr) {
        if (cArr == null || cArr.length <= 0) {
            return new byte[0];
        }
        byte[] bArr = new byte[(cArr.length + 1) * 2];
        for (int i10 = 0; i10 != cArr.length; i10++) {
            int i11 = i10 * 2;
            char c5 = cArr[i10];
            bArr[i11] = (byte) (c5 >>> '\b');
            bArr[i11 + 1] = (byte) c5;
        }
        return bArr;
    }

    public abstract nk.h b(int i10);

    public abstract nk.h c(int i10);

    public abstract nk.i d(int i10, int i11);

    public final void e(int i10, byte[] bArr, byte[] bArr2) {
        this.f15775a = bArr;
        this.f15776b = bArr2;
        this.f15777c = i10;
    }
}
