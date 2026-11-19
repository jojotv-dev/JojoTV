package bj;

import io.sentry.cache.tape.d;
import io.sentry.cache.tape.h;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: loaded from: classes5.dex */
public final class a implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f6247b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f6248c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Iterable f6249e;

    public a(c cVar) {
        this.f6249e = cVar;
        this.f6248c = cVar.f6258i;
    }

    public void a() {
        b bVar;
        c cVar = (c) this.f6249e;
        ReentrantReadWriteLock.WriteLock writeLock = cVar.f6256c.writeLock();
        try {
            writeLock.lock();
            do {
                b bVar2 = (b) this.f6248c;
                bVar = bVar2.f6250a;
                this.f6248c = bVar;
                b bVar3 = cVar.f6258i;
                if (bVar2 == bVar3) {
                    cVar.f6258i = bVar3.f6250a;
                }
                b bVar4 = bVar2.f6251b;
                if (bVar4 != null) {
                    bVar4.f6250a = bVar;
                    b bVar5 = bVar2.f6250a;
                    if (bVar5 != null) {
                        bVar5.f6251b = bVar4;
                    }
                } else if (bVar != null) {
                    bVar.f6251b = null;
                }
                if (bVar != null) {
                }
            } while (bVar.a() == null);
        } finally {
            writeLock.unlock();
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        switch (this.f6247b) {
            case 0:
                return ((b) this.f6248c) != null;
            case 1:
                b bVar = (b) this.f6248c;
                if (bVar == null) {
                    return false;
                }
                if (bVar.a() == null) {
                    a();
                    if (((b) this.f6248c) == null) {
                        return false;
                    }
                }
                return true;
            default:
                return ((h) this.f6248c).hasNext();
        }
    }

    @Override // java.util.Iterator
    public final Object next() {
        switch (this.f6247b) {
            case 0:
                b bVar = (b) this.f6248c;
                if (bVar == null) {
                    return null;
                }
                Object objA = bVar.a();
                this.f6248c = ((b) this.f6248c).f6250a;
                return objA;
            case 1:
                b bVar2 = (b) this.f6248c;
                if (bVar2 == null) {
                    return null;
                }
                Object objA2 = bVar2.a();
                if (objA2 == null) {
                    a();
                    return next();
                }
                this.f6248c = ((b) this.f6248c).f6250a;
                return objA2;
            default:
                return ((d) this.f6249e).f25425e.d((byte[]) ((h) this.f6248c).next());
        }
    }

    @Override // java.util.Iterator
    public final void remove() {
        switch (this.f6247b) {
            case 0:
                b bVar = (b) this.f6248c;
                if (bVar != null) {
                    b bVar2 = bVar.f6250a;
                    ((c) this.f6249e).remove(bVar.a());
                    this.f6248c = bVar2;
                    break;
                }
                break;
            case 1:
                b bVar3 = (b) this.f6248c;
                if (bVar3 != null) {
                    b bVar4 = bVar3.f6250a;
                    ((c) this.f6249e).remove(bVar3.a());
                    this.f6248c = bVar4;
                    break;
                }
                break;
            default:
                ((h) this.f6248c).remove();
                break;
        }
    }

    public a(c cVar, byte b5) {
        this.f6249e = cVar;
        this.f6248c = cVar.f6258i;
    }

    public a(d dVar, h hVar) {
        this.f6249e = dVar;
        this.f6248c = hVar;
    }
}
