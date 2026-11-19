package kk;

import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends ek.m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f31472a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f31473b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public byte[] f31474c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f31475d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final ek.a f31476e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f31477f;

    public b(ek.a aVar) {
        this.f31476e = aVar;
        int iC = aVar.c();
        this.f31475d = iC;
        this.f31472a = new byte[iC];
        this.f31473b = new byte[iC];
        this.f31474c = new byte[iC];
    }

    @Override // ek.a
    public final void a(boolean z2, ek.d dVar) {
        boolean z10 = this.f31477f;
        this.f31477f = z2;
        boolean z11 = dVar instanceof nk.i;
        byte[] bArr = this.f31472a;
        if (z11) {
            nk.i iVar = (nk.i) dVar;
            byte[] bArr2 = iVar.f42904b;
            if (bArr2.length != this.f31475d) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
            dVar = iVar.f42905c;
        } else {
            Arrays.fill(bArr, (byte) 0);
        }
        reset();
        if (dVar != null) {
            this.f31476e.a(z2, dVar);
        } else if (z10 != z2) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
    }

    @Override // ek.a
    public final int b(byte[] bArr, int i10, int i11, byte[] bArr2) {
        boolean z2 = this.f31477f;
        ek.a aVar = this.f31476e;
        int i12 = this.f31475d;
        if (z2) {
            if (i10 + i12 > bArr.length) {
                throw new ek.k("input buffer too short");
            }
            for (int i13 = 0; i13 < i12; i13++) {
                byte[] bArr3 = this.f31473b;
                bArr3[i13] = (byte) (bArr3[i13] ^ bArr[i10 + i13]);
            }
            int iB = aVar.b(this.f31473b, 0, i11, bArr2);
            byte[] bArr4 = this.f31473b;
            System.arraycopy(bArr2, i11, bArr4, 0, bArr4.length);
            return iB;
        }
        if (i10 + i12 > bArr.length) {
            throw new ek.k("input buffer too short");
        }
        System.arraycopy(bArr, i10, this.f31474c, 0, i12);
        int iB2 = aVar.b(bArr, i10, i11, bArr2);
        for (int i14 = 0; i14 < i12; i14++) {
            int i15 = i11 + i14;
            bArr2[i15] = (byte) (bArr2[i15] ^ this.f31473b[i14]);
        }
        byte[] bArr5 = this.f31473b;
        this.f31473b = this.f31474c;
        this.f31474c = bArr5;
        return iB2;
    }

    @Override // ek.a
    public final int c() {
        return this.f31476e.c();
    }

    @Override // ek.a
    public final String getAlgorithmName() {
        return this.f31476e.getAlgorithmName() + "/CBC";
    }

    @Override // ek.a
    public final void reset() {
        byte[] bArr = this.f31473b;
        byte[] bArr2 = this.f31472a;
        System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
        Arrays.fill(this.f31474c, (byte) 0);
        this.f31476e.reset();
    }
}
