package r7;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public abstract class a extends a3 implements ListIterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f51192b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f51193c;

    public a(int i10, int i11) {
        l3.c.x(i11, i10);
        this.f51192b = i10;
        this.f51193c = i11;
    }

    public abstract Object a(int i10);

    @Override // java.util.ListIterator
    public final void add(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Iterator, java.util.ListIterator
    public final boolean hasNext() {
        return this.f51193c < this.f51192b;
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        return this.f51193c > 0;
    }

    @Override // java.util.Iterator, java.util.ListIterator
    public final Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int i10 = this.f51193c;
        this.f51193c = i10 + 1;
        return a(i10);
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        return this.f51193c;
    }

    @Override // java.util.ListIterator
    public final Object previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        int i10 = this.f51193c - 1;
        this.f51193c = i10;
        return a(i10);
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        return this.f51193c - 1;
    }

    @Override // java.util.ListIterator
    public final void set(Object obj) {
        throw new UnsupportedOperationException();
    }
}
