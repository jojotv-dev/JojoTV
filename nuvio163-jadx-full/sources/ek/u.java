package ek;

/* JADX INFO: loaded from: classes5.dex */
public abstract class u extends m implements v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final a f15778a;

    public u(a aVar) {
        this.f15778a = aVar;
    }

    public abstract byte e(byte b5);

    public int f(byte[] bArr, int i10, int i11, byte[] bArr2, int i12) {
        int i13 = i10 + i11;
        if (i13 > bArr.length) {
            throw new k("input buffer too small");
        }
        if (i12 + i11 > bArr2.length) {
            throw new r("output buffer too short");
        }
        while (i10 < i13) {
            bArr2[i12] = e(bArr[i10]);
            i12++;
            i10++;
        }
        return i11;
    }
}
