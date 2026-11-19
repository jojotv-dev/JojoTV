package ek;

/* JADX INFO: loaded from: classes5.dex */
public abstract class m implements a {
    public final int d(byte[] bArr, int i10, int i11, byte[] bArr2, int i12) {
        int iC = c();
        int i13 = i11 * iC;
        if (bArr == bArr2) {
            bArr = new byte[i13];
            System.arraycopy(bArr2, i10, bArr, 0, i13);
            i10 = 0;
        }
        int iB = 0;
        for (int i14 = 0; i14 != i11; i14++) {
            iB += b(bArr, i10, i12 + iB, bArr2);
            i10 += iC;
        }
        return iB;
    }
}
