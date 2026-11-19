package m1;

import java.util.ListIterator;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public abstract class a implements ListIterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f34698b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f34699c;

    public a(int i10, int i11) {
        this.f34698b = i10;
        this.f34699c = i11;
    }

    @Override // java.util.ListIterator
    public void add(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final boolean hasNext() {
        return this.f34698b < this.f34699c;
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        return this.f34698b > 0;
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        return this.f34698b;
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        return this.f34698b - 1;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    @Override // java.util.ListIterator
    public void set(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
