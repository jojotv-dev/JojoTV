package io.sentry;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public final class i7 implements Queue, Collection, Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final h f25617b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final io.sentry.util.a f25618c = new io.sentry.util.a();

    public i7(h hVar) {
        this.f25617b = hVar;
    }

    @Override // java.util.Queue, java.util.Collection
    public final boolean add(Object obj) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zAdd = this.f25617b.add(obj);
            aVar.close();
            return zAdd;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean addAll(Collection collection) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zAddAll = this.f25617b.addAll(collection);
            aVar.close();
            return zAddAll;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final void clear() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            this.f25617b.clear();
            aVar.close();
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean contains(Object obj) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zContains = this.f25617b.contains(obj);
            aVar.close();
            return zContains;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean containsAll(Collection collection) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zContainsAll = this.f25617b.containsAll(collection);
            aVar.close();
            return zContainsAll;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Queue
    public final Object element() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object objElement = this.f25617b.element();
            aVar.close();
            return objElement;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zEquals = this.f25617b.equals(obj);
            aVar.close();
            return zEquals;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final int hashCode() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            int iHashCode = this.f25617b.hashCode();
            aVar.close();
            return iHashCode;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean isEmpty() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zIsEmpty = this.f25617b.isEmpty();
            aVar.close();
            return zIsEmpty;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection, java.lang.Iterable
    public final Iterator iterator() {
        return this.f25617b.iterator();
    }

    @Override // java.util.Queue
    public final boolean offer(Object obj) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zOffer = this.f25617b.offer(obj);
            aVar.close();
            return zOffer;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Queue
    public final Object peek() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object objPeek = this.f25617b.peek();
            aVar.close();
            return objPeek;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Queue
    public final Object poll() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object objPoll = this.f25617b.poll();
            aVar.close();
            return objPoll;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Queue
    public final Object remove() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object objRemove = this.f25617b.remove();
            aVar.close();
            return objRemove;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean removeAll(Collection collection) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zRemoveAll = this.f25617b.removeAll(collection);
            aVar.close();
            return zRemoveAll;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean retainAll(Collection collection) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zRetainAll = this.f25617b.retainAll(collection);
            aVar.close();
            return zRetainAll;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final int size() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            int size = this.f25617b.size();
            aVar.close();
            return size;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final Object[] toArray() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object[] array = this.f25617b.toArray();
            aVar.close();
            return array;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    public final String toString() {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            String string = this.f25617b.toString();
            aVar.close();
            return string;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final boolean remove(Object obj) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            boolean zRemove = this.f25617b.remove(obj);
            aVar.close();
            return zRemove;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    @Override // java.util.Collection
    public final Object[] toArray(Object[] objArr) {
        io.sentry.util.a aVar = this.f25618c;
        aVar.g();
        try {
            Object[] array = this.f25617b.toArray(objArr);
            aVar.close();
            return array;
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }
}
