package x2;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class l2 implements w2.y1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f63186b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final List f63187c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Float f63188e = null;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public Float f63189i = null;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public e3.k f63190m = null;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public e3.k f63191n = null;

    public l2(ArrayList arrayList, int i10) {
        this.f63186b = i10;
        this.f63187c = arrayList;
    }

    @Override // w2.y1
    public final boolean V() {
        return this.f63187c.contains(this);
    }

    public final e3.k a() {
        return this.f63190m;
    }

    public final Float b() {
        return this.f63188e;
    }

    public final Float c() {
        return this.f63189i;
    }

    public final int d() {
        return this.f63186b;
    }

    public final e3.k e() {
        return this.f63191n;
    }

    public final void f(e3.k kVar) {
        this.f63190m = kVar;
    }

    public final void g(Float f10) {
        this.f63188e = f10;
    }

    public final void h(Float f10) {
        this.f63189i = f10;
    }

    public final void i(e3.k kVar) {
        this.f63191n = kVar;
    }
}
