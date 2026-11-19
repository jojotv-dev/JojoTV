package androidx.datastore.preferences.protobuf;

import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public final class h1 implements Iterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f2476b = -1;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f2477c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Iterator f2478e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ c1 f2479i;

    public h1(c1 c1Var) {
        this.f2479i = c1Var;
    }

    public final Iterator a() {
        if (this.f2478e == null) {
            this.f2478e = this.f2479i.f2437e.entrySet().iterator();
        }
        return this.f2478e;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        int i10 = this.f2476b + 1;
        c1 c1Var = this.f2479i;
        return i10 < c1Var.f2436c.size() || (!c1Var.f2437e.isEmpty() && a().hasNext());
    }

    @Override // java.util.Iterator
    public final Object next() {
        this.f2477c = true;
        int i10 = this.f2476b + 1;
        this.f2476b = i10;
        c1 c1Var = this.f2479i;
        return i10 < c1Var.f2436c.size() ? (Map.Entry) c1Var.f2436c.get(this.f2476b) : (Map.Entry) a().next();
    }

    @Override // java.util.Iterator
    public final void remove() {
        if (!this.f2477c) {
            throw new IllegalStateException("remove() was called before next()");
        }
        this.f2477c = false;
        int i10 = c1.f2434o;
        c1 c1Var = this.f2479i;
        c1Var.b();
        if (this.f2476b >= c1Var.f2436c.size()) {
            a().remove();
            return;
        }
        int i11 = this.f2476b;
        this.f2476b = i11 - 1;
        c1Var.g(i11);
    }
}
