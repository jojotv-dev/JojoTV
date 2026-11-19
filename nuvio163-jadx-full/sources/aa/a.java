package aa;

/* JADX INFO: loaded from: classes4.dex */
public final class a extends d {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final byte[] f729c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f730e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f731i;

    public a(long j10, byte[] bArr, int i10) {
        this.f729c = bArr;
        this.f736b = j10;
        this.f730e = 0;
        this.f731i = i10;
    }

    @Override // aa.d
    public final int g() {
        return this.f731i;
    }

    @Override // aa.d
    public final int l(byte[] bArr) {
        int length = bArr.length;
        int i10 = this.f731i;
        if (length > i10) {
            length = i10;
        }
        System.arraycopy(this.f729c, this.f730e, bArr, 0, length);
        this.f730e += length;
        this.f731i -= length;
        return length;
    }

    @Override // aa.d
    public final boolean n() {
        return this.f731i > 0;
    }

    @Override // aa.d
    public final void A(int i10) {
    }
}
