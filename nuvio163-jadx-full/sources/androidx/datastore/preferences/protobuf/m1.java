package androidx.datastore.preferences.protobuf;

import java.util.ListIterator;

/* JADX INFO: loaded from: classes3.dex */
public final class m1 implements ListIterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public ListIterator f2520b;

    @Override // java.util.ListIterator
    public final void add(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final boolean hasNext() {
        return this.f2520b.hasNext();
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        return this.f2520b.hasPrevious();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final Object next() {
        return (String) this.f2520b.next();
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        return this.f2520b.nextIndex();
    }

    @Override // java.util.ListIterator
    public final Object previous() {
        return (String) this.f2520b.previous();
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        return this.f2520b.previousIndex();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator
    public final void set(Object obj) {
        throw new UnsupportedOperationException();
    }
}
