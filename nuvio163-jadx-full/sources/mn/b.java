package mn;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public byte[] f39239e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public byte[] f39240i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public byte[] f39241m;

    public final byte[] getEncoded() {
        byte[] bArr = this.f39239e;
        byte[] bArr2 = this.f39240i;
        byte[] bArr3 = this.f39241m;
        if (bArr == null) {
            return d.d(bArr2, bArr3);
        }
        if (bArr2 == null) {
            return d.d(bArr, bArr3);
        }
        if (bArr3 == null) {
            return d.d(bArr, bArr2);
        }
        byte[] bArr4 = new byte[bArr.length + bArr2.length + bArr3.length];
        System.arraycopy(bArr, 0, bArr4, 0, bArr.length);
        int length = bArr.length;
        System.arraycopy(bArr2, 0, bArr4, length, bArr2.length);
        System.arraycopy(bArr3, 0, bArr4, length + bArr2.length, bArr3.length);
        return bArr4;
    }
}
