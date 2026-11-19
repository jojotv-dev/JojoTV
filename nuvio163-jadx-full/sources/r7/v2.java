package r7;

import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public abstract class v2 implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Iterator f51366b;

    public v2(Iterator it) {
        it.getClass();
        this.f51366b = it;
    }

    public abstract Object a(Object obj);

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f51366b.hasNext();
    }

    @Override // java.util.Iterator
    public final Object next() {
        return a(this.f51366b.next());
    }

    @Override // java.util.Iterator
    public final void remove() {
        this.f51366b.remove();
    }
}
