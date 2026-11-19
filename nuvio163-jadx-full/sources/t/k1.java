package t;

import java.util.Iterator;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public final class k1 implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f57023b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ i1 f57024c;

    public k1(i1 i1Var) {
        this.f57024c = i1Var;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f57023b < this.f57024c.g();
    }

    @Override // java.util.Iterator
    public final Object next() {
        int i10 = this.f57023b;
        this.f57023b = i10 + 1;
        return this.f57024c.h(i10);
    }

    @Override // java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
