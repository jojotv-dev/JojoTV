package wh;

import java.util.Iterator;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes5.dex */
public abstract class p implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object[] f62576b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f62577c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f62578e;

    public p() {
        o oVar = o.f62571e;
        this.f62576b = o.f62571e.f62575d;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f62578e < this.f62577c;
    }

    @Override // java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
