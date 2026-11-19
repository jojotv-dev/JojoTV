package t9;

import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes4.dex */
public final class b implements DirectoryStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f57259b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f57260c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f57261e;

    public b(ArrayList arrayList) {
        this.f57259b = arrayList;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        this.f57260c = true;
    }

    @Override // java.nio.file.DirectoryStream, java.lang.Iterable
    public final Iterator iterator() {
        if (this.f57260c) {
            throw new IllegalStateException("closed");
        }
        if (this.f57261e) {
            throw new IllegalStateException("iterator already taken");
        }
        this.f57261e = true;
        return this.f57259b.iterator();
    }
}
