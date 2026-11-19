package nk;

/* JADX INFO: loaded from: classes5.dex */
public class h implements ek.d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f42903b;

    public h(byte[] bArr) {
        this(bArr, 0, bArr.length);
    }

    public h(byte[] bArr, int i10, int i11) {
        byte[] bArr2 = new byte[i11];
        this.f42903b = bArr2;
        System.arraycopy(bArr, i10, bArr2, 0, i11);
    }
}
