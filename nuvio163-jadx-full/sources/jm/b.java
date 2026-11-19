package jm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f27581e;

    public b(byte[] bArr, String str) {
        super(true, str);
        if (bArr.length != 1088) {
            throw new IllegalArgumentException("'keyData' has invalid length");
        }
        this.f27581e = d.b(bArr);
    }
}
