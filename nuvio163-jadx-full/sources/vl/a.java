package vl;

import ek.n;
import ek.t;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final t f61066a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f61067b;

    public a(t tVar, int i10) {
        this.f61066a = tVar;
        this.f61067b = i10;
    }

    @Override // ek.n
    public final int doFinal(byte[] bArr, int i10) {
        t tVar = this.f61066a;
        byte[] bArr2 = new byte[tVar.getDigestSize()];
        tVar.doFinal(bArr2, 0);
        int i11 = this.f61067b;
        System.arraycopy(bArr2, 0, bArr, i10, i11);
        return i11;
    }

    @Override // ek.n
    public final String getAlgorithmName() {
        return this.f61066a.getAlgorithmName() + "/" + (this.f61067b * 8);
    }

    @Override // ek.n
    public final int getDigestSize() {
        return this.f61067b;
    }

    @Override // ek.n
    public final void reset() {
        this.f61066a.reset();
    }

    @Override // ek.n
    public final void update(byte b5) {
        this.f61066a.update(b5);
    }

    @Override // ek.n
    public final void update(byte[] bArr, int i10, int i11) {
        this.f61066a.update(bArr, i10, i11);
    }
}
