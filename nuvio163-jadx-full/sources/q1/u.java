package q1;

import g1.t2;
import g1.u2;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class u implements t2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f45495b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final h1.e f45496c = new h1.e(new u2[16]);

    public u(Set set) {
        this.f45495b = set;
    }

    @Override // g1.t2
    public final void a() {
        h1.e eVar = this.f45496c;
        Object[] objArr = eVar.f20586b;
        int i10 = eVar.f20588e;
        for (int i11 = 0; i11 < i10; i11++) {
            t2 t2VarA = ((u2) objArr[i11]).a();
            this.f45495b.remove(t2VarA);
            t2VarA.a();
        }
    }

    @Override // g1.t2
    public final void b() {
    }

    @Override // g1.t2
    public final void c() {
    }
}
