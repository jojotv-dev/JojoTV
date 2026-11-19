package ik;

import ek.k;

/* JADX INFO: loaded from: classes5.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final jk.c f24205a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f24206b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public byte[] f24207c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public byte[] f24208d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f24209e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public byte[] f24210f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f24211g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final byte[] f24212h;

    public b(jk.c cVar) {
        this.f24205a = cVar;
        int i10 = cVar.f27553b;
        this.f24206b = i10;
        this.f24212h = new byte[i10];
    }

    public final int a(int i10, byte[] bArr) {
        int i11 = this.f24211g;
        int i12 = i11 + 16;
        if (i12 < 0 || i12 >= this.f24209e) {
            throw new k(h1.f.j(this.f24209e, " bytes", new StringBuilder("Current KDFCTR may only be used for ")));
        }
        int i13 = this.f24206b;
        if (i11 % i13 == 0) {
            b();
        }
        int i14 = this.f24211g % i13;
        int iMin = Math.min(i13 - i14, 16);
        byte[] bArr2 = this.f24212h;
        System.arraycopy(bArr2, i14, bArr, 0, iMin);
        this.f24211g += iMin;
        int i15 = 16 - iMin;
        while (i15 > 0) {
            b();
            int iMin2 = Math.min(i13, i15);
            System.arraycopy(bArr2, 0, bArr, iMin, iMin2);
            this.f24211g += iMin2;
            i15 -= iMin2;
            iMin += iMin2;
        }
        return 16;
    }

    public final void b() {
        int i10 = (this.f24211g / this.f24206b) + 1;
        byte[] bArr = this.f24210f;
        int length = bArr.length;
        if (length != 1) {
            if (length != 2) {
                if (length != 3) {
                    if (length != 4) {
                        throw new IllegalStateException("Unsupported size of counter i");
                    }
                    bArr[0] = (byte) (i10 >>> 24);
                }
                bArr[bArr.length - 3] = (byte) (i10 >>> 16);
            }
            bArr[bArr.length - 2] = (byte) (i10 >>> 8);
        }
        bArr[bArr.length - 1] = (byte) i10;
        byte[] bArr2 = this.f24207c;
        int length2 = bArr2.length;
        jk.c cVar = this.f24205a;
        cVar.update(bArr2, 0, length2);
        byte[] bArr3 = this.f24210f;
        cVar.update(bArr3, 0, bArr3.length);
        byte[] bArr4 = this.f24208d;
        cVar.update(bArr4, 0, bArr4.length);
        cVar.doFinal(this.f24212h, 0);
    }
}
