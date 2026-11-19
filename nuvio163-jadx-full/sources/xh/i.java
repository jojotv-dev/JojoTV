package xh;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes5.dex */
public final class i implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f63972b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Map f63973c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f63974e;

    public i(Map hashMap, Object obj) {
        Intrinsics.checkNotNullParameter(hashMap, "hashMap");
        this.f63972b = obj;
        this.f63973c = hashMap;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final a next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Object obj = this.f63973c.get(this.f63972b);
        if (obj != null) {
            a aVar = (a) obj;
            this.f63974e++;
            this.f63972b = aVar.f63942c;
            return aVar;
        }
        throw new ConcurrentModificationException("Hash code of a key (" + this.f63972b + ") has changed after it was added to the persistent map.");
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f63974e < this.f63973c.size();
    }

    @Override // java.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
