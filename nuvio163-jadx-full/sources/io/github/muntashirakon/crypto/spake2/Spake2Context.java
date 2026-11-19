package io.github.muntashirakon.crypto.spake2;

import javax.security.auth.Destroyable;
import u4.i;

/* JADX INFO: loaded from: classes5.dex */
public class Spake2Context implements Destroyable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f24334b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final byte[] f24335c = new byte[32];

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f24336e;

    static {
        System.loadLibrary("spake2");
    }

    public Spake2Context(byte[] bArr, byte[] bArr2) {
        long jAllocNewContext = allocNewContext(i.b(1), bArr, bArr2);
        this.f24334b = jAllocNewContext;
        if (jAllocNewContext == 0) {
            throw new UnsupportedOperationException("Could not allocate native context");
        }
    }

    private static native long allocNewContext(int i10, byte[] bArr, byte[] bArr2);

    private static native void destroy(long j10);

    private static native byte[] generateMessage(long j10, byte[] bArr);

    private static native byte[] processMessage(long j10, byte[] bArr);

    public final byte[] a(byte[] bArr) {
        if (this.f24336e) {
            throw new IllegalStateException("The context was destroyed.");
        }
        byte[] bArrGenerateMessage = generateMessage(this.f24334b, bArr);
        if (bArrGenerateMessage == null) {
            throw new IllegalStateException("Generated empty message");
        }
        System.arraycopy(bArrGenerateMessage, 0, this.f24335c, 0, 32);
        return bArrGenerateMessage;
    }

    public final byte[] b(byte[] bArr) {
        if (this.f24336e) {
            throw new IllegalStateException("The context was destroyed.");
        }
        byte[] bArrProcessMessage = processMessage(this.f24334b, bArr);
        if (bArrProcessMessage != null) {
            return bArrProcessMessage;
        }
        throw new IllegalStateException("No key was returned");
    }

    @Override // javax.security.auth.Destroyable
    public final void destroy() {
        this.f24336e = true;
        destroy(this.f24334b);
    }

    @Override // javax.security.auth.Destroyable
    public final boolean isDestroyed() {
        return this.f24336e;
    }
}
