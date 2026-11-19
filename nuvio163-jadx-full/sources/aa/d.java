package aa;

import java.io.Closeable;

/* JADX INFO: loaded from: classes4.dex */
public abstract class d implements Closeable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f736b;

    public abstract void A(int i10);

    public abstract int g();

    public abstract int l(byte[] bArr);

    public abstract boolean n();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}
