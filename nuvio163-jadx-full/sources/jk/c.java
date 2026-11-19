package jk;

import ek.n;
import ek.o;
import ek.q;
import java.util.Hashtable;
import nk.h;
import rn.e;

/* JADX INFO: loaded from: classes5.dex */
public final class c implements q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f27552a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f27553b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f27554c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public e f27555d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public e f27556e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final byte[] f27557f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final byte[] f27558g;

    static {
        Hashtable hashtable = new Hashtable();
        hashtable.put("GOST3411", 32);
        hashtable.put("MD2", 16);
        hashtable.put("MD4", 64);
        hashtable.put("MD5", 64);
        hashtable.put("RIPEMD128", 64);
        hashtable.put("RIPEMD160", 64);
        hashtable.put("SHA-1", 64);
        hashtable.put("SHA-224", 64);
        hashtable.put("SHA-256", 64);
        hashtable.put("SHA-384", 128);
        hashtable.put("SHA-512", 128);
        hashtable.put("Tiger", 64);
        hashtable.put("Whirlpool", 64);
    }

    public c(n nVar) {
        int byteLength = ((o) nVar).getByteLength();
        this.f27552a = nVar;
        int digestSize = nVar.getDigestSize();
        this.f27553b = digestSize;
        this.f27554c = byteLength;
        this.f27557f = new byte[byteLength];
        this.f27558g = new byte[byteLength + digestSize];
    }

    /* JADX WARN: Type inference failed for: r7v1, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final int doFinal(byte[] bArr, int i10) {
        ?? r9 = this.f27552a;
        byte[] bArr2 = this.f27558g;
        int i11 = this.f27554c;
        r9.doFinal(bArr2, i11);
        e eVar = this.f27556e;
        if (eVar != null) {
            ((e) r9).reset(eVar);
            r9.update(bArr2, i11, r9.getDigestSize());
        } else {
            r9.update(bArr2, 0, bArr2.length);
        }
        int iDoFinal = r9.doFinal(bArr, 0);
        while (i11 < bArr2.length) {
            bArr2[i11] = 0;
            i11++;
        }
        e eVar2 = this.f27555d;
        if (eVar2 != null) {
            ((e) r9).reset(eVar2);
            return iDoFinal;
        }
        byte[] bArr3 = this.f27557f;
        r9.update(bArr3, 0, bArr3.length);
        return iDoFinal;
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final String getAlgorithmName() {
        return this.f27552a.getAlgorithmName() + "/HMAC";
    }

    @Override // ek.q
    public final int getMacSize() {
        return this.f27553b;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final void init(ek.d dVar) {
        ?? r9 = this.f27552a;
        r9.reset();
        byte[] bArr = ((h) dVar).f42903b;
        int length = bArr.length;
        byte[] bArr2 = this.f27557f;
        int i10 = this.f27554c;
        if (length > i10) {
            r9.update(bArr, 0, length);
            r9.doFinal(bArr2, 0);
            length = this.f27553b;
        } else {
            System.arraycopy(bArr, 0, bArr2, 0, length);
        }
        while (length < bArr2.length) {
            bArr2[length] = 0;
            length++;
        }
        byte[] bArr3 = this.f27558g;
        System.arraycopy(bArr2, 0, bArr3, 0, i10);
        for (int i11 = 0; i11 < i10; i11++) {
            bArr2[i11] = (byte) (bArr2[i11] ^ 54);
        }
        for (int i12 = 0; i12 < i10; i12++) {
            bArr3[i12] = (byte) (bArr3[i12] ^ 92);
        }
        e eVarCopy = ((e) r9).copy();
        this.f27556e = eVarCopy;
        ((n) eVarCopy).update(bArr3, 0, i10);
        r9.update(bArr2, 0, bArr2.length);
        this.f27555d = ((e) r9).copy();
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final void reset() {
        e eVar = this.f27555d;
        ?? r9 = this.f27552a;
        if (eVar != null) {
            ((e) r9).reset(eVar);
            return;
        }
        r9.reset();
        byte[] bArr = this.f27557f;
        r9.update(bArr, 0, bArr.length);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final void update(byte b5) {
        this.f27552a.update(b5);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [ek.n, java.lang.Object] */
    @Override // ek.q
    public final void update(byte[] bArr, int i10, int i11) {
        this.f27552a.update(bArr, i10, i11);
    }
}
