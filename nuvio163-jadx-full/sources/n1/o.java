package n1;

import java.util.Iterator;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public abstract class o implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object[] f39448b = n.f39443e.f39447d;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f39449c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f39450e;

    public final void a(int i10, int i11, Object[] objArr) {
        this.f39448b = objArr;
        this.f39449c = i10;
        this.f39450e = i11;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f39450e < this.f39449c;
    }

    @Override // java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
