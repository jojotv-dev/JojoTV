package ek;

/* JADX INFO: loaded from: classes5.dex */
public class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public byte[] f15751a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f15752b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f15753c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public a f15754d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public m f15755e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f15756f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public boolean f15757g;

    public int a(byte[] bArr, int i10) {
        byte[] bArr2;
        int i11;
        int iD;
        int iC;
        byte[] bArr3 = this.f15751a;
        m mVar = this.f15755e;
        try {
            int i12 = this.f15752b;
            if (i10 + i12 > bArr.length) {
                throw new r("output buffer too short for doFinal()");
            }
            int i13 = 0;
            if (i12 != 0) {
                if (mVar != null) {
                    int iC2 = i12 / mVar.c();
                    bArr2 = bArr;
                    i11 = i10;
                    iD = mVar.d(this.f15751a, 0, iC2, bArr2, i11);
                    iC = iC2 * mVar.c();
                } else {
                    bArr2 = bArr;
                    i11 = i10;
                    iD = 0;
                    iC = 0;
                }
                if (this.f15752b == iC) {
                    i13 = iD;
                } else {
                    if (!this.f15756f) {
                        throw new k("data not block size aligned");
                    }
                    this.f15754d.b(bArr3, iC, iC, bArr3);
                    System.arraycopy(bArr3, iC, bArr2, i11 + iD, this.f15752b - iC);
                    int i14 = (this.f15752b - iC) + iD;
                    this.f15752b = 0;
                    i13 = i14;
                }
            }
            g();
            return i13;
        } catch (Throwable th2) {
            g();
            throw th2;
        }
    }

    public int b() {
        return this.f15754d.c();
    }

    public int c(int i10) {
        byte[] bArr = this.f15751a;
        int i11 = i10 + this.f15752b;
        return i11 - ((this.f15757g && this.f15753c) ? (i11 % bArr.length) - (this.f15754d.c() + 2) : i11 % bArr.length);
    }

    public void d(boolean z2, d dVar) {
        this.f15753c = z2;
        g();
        this.f15754d.a(z2, dVar);
    }

    public final int e(int i10, byte[] bArr) {
        this.f15752b = 0;
        m mVar = this.f15755e;
        if (mVar == null) {
            return this.f15754d.b(this.f15751a, 0, i10, bArr);
        }
        byte[] bArr2 = this.f15751a;
        return mVar.d(bArr2, 0, bArr2.length / mVar.c(), bArr, i10);
    }

    public int f(byte[] bArr, int i10, int i11, byte[] bArr2, int i12) {
        byte[] bArr3;
        int i13;
        m mVar = this.f15755e;
        byte[] bArr4 = this.f15751a;
        if (i11 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int iB = b();
        int iC = c(i11);
        if (iC > 0 && iC + i12 > bArr2.length) {
            throw new r("output buffer too short");
        }
        int length = bArr4.length;
        int i14 = this.f15752b;
        int i15 = length - i14;
        int iB2 = 0;
        if (i11 > i15) {
            if (i14 != 0) {
                System.arraycopy(bArr, i10, bArr4, i14, i15);
                i10 += i15;
                i11 -= i15;
            }
            if (bArr == bArr2) {
                bArr = new byte[i11];
                System.arraycopy(bArr2, i10, bArr, 0, i11);
                i13 = 0;
            } else {
                i13 = i10;
            }
            byte[] bArr5 = bArr;
            int iE = this.f15752b != 0 ? e(i12, bArr2) : 0;
            if (mVar != null) {
                int iC2 = (mVar.c() / iB) * (i11 / mVar.c());
                if (iC2 > 0) {
                    bArr3 = bArr2;
                    iE += mVar.d(bArr5, i13, iC2, bArr3, i12 + iE);
                    int i16 = iC2 * iB;
                    i11 -= i16;
                    i13 += i16;
                } else {
                    bArr3 = bArr2;
                }
                iB2 = iE;
                i10 = i13;
            } else {
                bArr3 = bArr2;
                iB2 = iE;
                i10 = i13;
                while (i11 > bArr4.length) {
                    iB2 += this.f15754d.b(bArr5, i10, i12 + iB2, bArr3);
                    i11 -= iB;
                    i10 += iB;
                }
            }
            bArr = bArr5;
        } else {
            bArr3 = bArr2;
        }
        System.arraycopy(bArr, i10, bArr4, this.f15752b, i11);
        int i17 = this.f15752b + i11;
        this.f15752b = i17;
        return i17 == bArr4.length ? e(i12 + iB2, bArr3) + iB2 : iB2;
    }

    public void g() {
        int i10 = 0;
        while (true) {
            byte[] bArr = this.f15751a;
            if (i10 >= bArr.length) {
                this.f15752b = 0;
                this.f15754d.reset();
                return;
            } else {
                bArr[i10] = 0;
                i10++;
            }
        }
    }
}
