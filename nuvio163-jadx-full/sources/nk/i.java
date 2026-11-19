package nk;

/* JADX INFO: loaded from: classes5.dex */
public final class i implements ek.d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f42904b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ek.d f42905c;

    public i(ek.d dVar, byte[] bArr) {
        this(dVar, bArr, 0, bArr.length);
    }

    public i(ek.d dVar, byte[] bArr, int i10, int i11) {
        byte[] bArr2 = new byte[i11];
        this.f42904b = bArr2;
        this.f42905c = dVar;
        System.arraycopy(bArr, i10, bArr2, 0, i11);
    }
}
