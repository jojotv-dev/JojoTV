package org.bouncycastle.jcajce.provider.symmetric.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class j extends ByteArrayOutputStream {
    public final void g() {
        Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
        reset();
    }

    public final byte[] l() {
        return ((ByteArrayOutputStream) this).buf;
    }
}
