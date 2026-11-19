package q;

import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class b extends e implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public c f45265b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public c f45266c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f45267e;

    public b(c cVar, c cVar2, int i10) {
        this.f45267e = i10;
        this.f45265b = cVar2;
        this.f45266c = cVar;
    }

    @Override // q.e
    public final void a(c cVar) {
        c cVar2;
        c cVarB = null;
        if (this.f45265b == cVar && cVar == this.f45266c) {
            this.f45266c = null;
            this.f45265b = null;
        }
        c cVar3 = this.f45265b;
        if (cVar3 == cVar) {
            switch (this.f45267e) {
                case 0:
                    cVar2 = cVar3.f45271i;
                    break;
                default:
                    cVar2 = cVar3.f45270e;
                    break;
            }
            this.f45265b = cVar2;
        }
        c cVar4 = this.f45266c;
        if (cVar4 == cVar) {
            c cVar5 = this.f45265b;
            if (cVar4 != cVar5 && cVar5 != null) {
                cVarB = b(cVar4);
            }
            this.f45266c = cVarB;
        }
    }

    public final c b(c cVar) {
        switch (this.f45267e) {
            case 0:
                return cVar.f45270e;
            default:
                return cVar.f45271i;
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.f45266c != null;
    }

    @Override // java.util.Iterator
    public final Object next() {
        c cVar = this.f45266c;
        c cVar2 = this.f45265b;
        this.f45266c = (cVar == cVar2 || cVar2 == null) ? null : b(cVar);
        return cVar;
    }
}
