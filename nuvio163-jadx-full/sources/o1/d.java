package o1;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes.dex */
public class d implements Iterator, KMappedMarker {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f43412b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Map f43413c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f43414e;

    public d(Map map, Object obj) {
        this.f43412b = obj;
        this.f43413c = map;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f43414e < this.f43413c.size();
    }

    @Override // java.util.Iterator
    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Object obj = this.f43412b;
        this.f43414e++;
        Object obj2 = this.f43413c.get(obj);
        if (obj2 != null) {
            this.f43412b = ((a) obj2).f43403b;
            return obj;
        }
        throw new ConcurrentModificationException("Hash code of an element (" + obj + ") has changed after it was added to the persistent set.");
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }
}
