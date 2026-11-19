package jm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f27582e;

    public c(byte[] bArr, String str) {
        super(false, str);
        if (bArr.length != 1056) {
            throw new IllegalArgumentException("'keyData' has invalid length");
        }
        this.f27582e = d.b(bArr);
    }
}
