package rn;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object[] f53958b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f53959c = 0;

    public a(Object[] objArr) {
        this.f53958b = objArr;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f53959c < this.f53958b.length;
    }

    @Override // java.util.Iterator
    public final Object next() {
        int i10 = this.f53959c;
        Object[] objArr = this.f53958b;
        if (i10 != objArr.length) {
            this.f53959c = i10 + 1;
            return objArr[i10];
        }
        throw new NoSuchElementException("Out of elements: " + this.f53959c);
    }

    @Override // java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException("Cannot remove element from an Array.");
    }
}
