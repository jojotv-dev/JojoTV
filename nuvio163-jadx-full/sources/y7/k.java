package y7;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes.dex */
public abstract class k implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public l f65118b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public l f65119c = null;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f65120e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ m f65121i;

    public k(m mVar) {
        this.f65121i = mVar;
        this.f65118b = mVar.f65137n.f65125i;
        this.f65120e = mVar.f65136m;
    }

    public final l a() {
        l lVar = this.f65118b;
        m mVar = this.f65121i;
        if (lVar == mVar.f65137n) {
            throw new NoSuchElementException();
        }
        if (mVar.f65136m != this.f65120e) {
            throw new ConcurrentModificationException();
        }
        this.f65118b = lVar.f65125i;
        this.f65119c = lVar;
        return lVar;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f65118b != this.f65121i.f65137n;
    }

    @Override // java.util.Iterator
    public Object next() {
        return a();
    }

    @Override // java.util.Iterator
    public final void remove() {
        l lVar = this.f65119c;
        if (lVar == null) {
            throw new IllegalStateException();
        }
        m mVar = this.f65121i;
        mVar.c(lVar, true);
        this.f65119c = null;
        this.f65120e = mVar.f65136m;
    }
}
