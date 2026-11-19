package yk;

import javax.crypto.spec.IvParameterSpec;

/* JADX INFO: loaded from: classes5.dex */
public final class a extends IvParameterSpec {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f65387a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f65388b;

    public a(byte[] bArr, int i10) {
        super(bArr);
        this.f65388b = i10;
        this.f65387a = rn.d.b(null);
    }
}
