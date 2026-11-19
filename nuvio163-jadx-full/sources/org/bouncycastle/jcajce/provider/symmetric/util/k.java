package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidKeyException;

/* JADX INFO: loaded from: classes5.dex */
public final class k extends InvalidKeyException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Exception f44085b;

    public k(String str, Exception exc) {
        super(str);
        this.f44085b = exc;
    }

    @Override // java.lang.Throwable
    public final Throwable getCause() {
        return this.f44085b;
    }
}
