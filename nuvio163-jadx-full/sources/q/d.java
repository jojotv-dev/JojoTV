package q;

import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class d extends e implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public c f45272b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f45273c = true;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ f f45274e;

    public d(f fVar) {
        this.f45274e = fVar;
    }

    @Override // q.e
    public final void a(c cVar) {
        c cVar2 = this.f45272b;
        if (cVar == cVar2) {
            c cVar3 = cVar2.f45271i;
            this.f45272b = cVar3;
            this.f45273c = cVar3 == null;
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (this.f45273c) {
            return this.f45274e.f45275b != null;
        }
        c cVar = this.f45272b;
        return (cVar == null || cVar.f45270e == null) ? false : true;
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (this.f45273c) {
            this.f45273c = false;
            this.f45272b = this.f45274e.f45275b;
        } else {
            c cVar = this.f45272b;
            this.f45272b = cVar != null ? cVar.f45270e : null;
        }
        return this.f45272b;
    }
}
