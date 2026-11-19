package r7;

import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public final class t1 extends a3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f51357b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f51358c;

    public t1(Object obj) {
        this.f51357b = obj;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return !this.f51358c;
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (this.f51358c) {
            throw new NoSuchElementException();
        }
        this.f51358c = true;
        return this.f51357b;
    }
}
