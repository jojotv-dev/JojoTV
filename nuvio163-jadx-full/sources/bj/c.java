package bj;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: loaded from: classes5.dex */
public final class c implements Set {

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final AtomicLong f6254n = new AtomicLong();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f6255b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ReentrantReadWriteLock f6256c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final AbstractMap f6257e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public b f6258i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ int f6259m;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(AbstractMap abstractMap) {
        this(abstractMap, (byte) 0);
        this.f6259m = 1;
    }

    public final b a(Object obj, b bVar) {
        b bVar2;
        b bVar3;
        switch (this.f6259m) {
            case 0:
                if (bVar != null) {
                    bVar2 = new b(bVar, 0);
                    bVar2.f6253d = obj;
                } else {
                    bVar2 = new b(0);
                    bVar2.f6253d = obj;
                }
                return bVar2;
            default:
                if (bVar != null) {
                    bVar3 = new b(bVar, 1);
                    bVar3.f6253d = new WeakReference(obj);
                } else {
                    bVar3 = new b(1);
                    bVar3.f6253d = new WeakReference(obj);
                }
                return bVar3;
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean add(Object obj) {
        boolean z2 = false;
        if (obj == null) {
            return false;
        }
        ReentrantReadWriteLock.WriteLock writeLock = this.f6256c.writeLock();
        try {
            writeLock.lock();
            AbstractMap abstractMap = this.f6257e;
            if (!abstractMap.containsKey(obj)) {
                b bVarA = a(obj, this.f6258i);
                this.f6258i = bVarA;
                abstractMap.put(obj, bVarA);
                z2 = true;
            }
            return z2;
        } finally {
            writeLock.unlock();
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean addAll(Collection collection) {
        boolean z2;
        ReentrantReadWriteLock.WriteLock writeLock = this.f6256c.writeLock();
        try {
            writeLock.lock();
            boolean z10 = false;
            for (Object obj : collection) {
                if (obj != null) {
                    AbstractMap abstractMap = this.f6257e;
                    if (abstractMap.containsKey(obj)) {
                        z2 = false;
                    } else {
                        b bVarA = a(obj, this.f6258i);
                        this.f6258i = bVarA;
                        abstractMap.put(obj, bVarA);
                        z2 = true;
                    }
                    z10 |= z2;
                }
            }
            writeLock.unlock();
            return z10;
        } catch (Throwable th2) {
            writeLock.unlock();
            throw th2;
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final void clear() {
        ReentrantReadWriteLock.WriteLock writeLock = this.f6256c.writeLock();
        try {
            writeLock.lock();
            this.f6258i = null;
            this.f6257e.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean contains(Object obj) {
        ReentrantReadWriteLock.ReadLock lock = this.f6256c.readLock();
        try {
            lock.lock();
            b bVar = (b) this.f6257e.get(obj);
            lock.unlock();
            return (bVar == null || bVar.a() == null) ? false : true;
        } catch (Throwable th2) {
            lock.unlock();
            throw th2;
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean containsAll(Collection collection) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.f6255b == ((c) obj).f6255b;
    }

    @Override // java.util.Set, java.util.Collection
    public final int hashCode() {
        long j10 = this.f6255b;
        return 31 + ((int) (j10 ^ (j10 >>> 32)));
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean isEmpty() {
        return this.f6258i == null;
    }

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    public final Iterator iterator() {
        switch (this.f6259m) {
            case 0:
                return new a(this);
            default:
                return new a(this, (byte) 0);
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean remove(Object obj) {
        AbstractMap abstractMap = this.f6257e;
        if (!contains(obj)) {
            return false;
        }
        ReentrantReadWriteLock.WriteLock writeLock = this.f6256c.writeLock();
        try {
            writeLock.lock();
            b bVar = (b) abstractMap.get(obj);
            if (bVar == null) {
                return false;
            }
            b bVar2 = this.f6258i;
            if (bVar != bVar2) {
                b bVar3 = bVar.f6251b;
                if (bVar3 != null) {
                    bVar3.f6250a = bVar.f6250a;
                    b bVar4 = bVar.f6250a;
                    if (bVar4 != null) {
                        bVar4.f6251b = bVar3;
                    }
                } else {
                    b bVar5 = bVar.f6250a;
                    if (bVar5 != null) {
                        bVar5.f6251b = null;
                    }
                }
            } else {
                this.f6258i = bVar2.f6250a;
            }
            abstractMap.remove(obj);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean removeAll(Collection collection) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // java.util.Set, java.util.Collection
    public final boolean retainAll(Collection collection) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // java.util.Set, java.util.Collection
    public final int size() {
        return this.f6257e.size();
    }

    @Override // java.util.Set, java.util.Collection
    public final Object[] toArray() {
        return this.f6257e.entrySet().toArray();
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public c() {
        this(new HashMap(), (byte) 0);
        this.f6259m = 0;
    }

    @Override // java.util.Set, java.util.Collection
    public final Object[] toArray(Object[] objArr) {
        return this.f6257e.entrySet().toArray(objArr);
    }

    public c(AbstractMap abstractMap, byte b5) {
        this.f6255b = f6254n.getAndIncrement();
        this.f6256c = new ReentrantReadWriteLock();
        this.f6257e = abstractMap;
    }
}
