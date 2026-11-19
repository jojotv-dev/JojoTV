package sp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes5.dex */
public final class e implements qp.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f56920b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile qp.b f56921c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Boolean f56922e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public Method f56923i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public rp.a f56924m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final Queue f56925n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final boolean f56926o;

    public e(String str, LinkedBlockingQueue linkedBlockingQueue, boolean z2) {
        this.f56920b = str;
        this.f56925n = linkedBlockingQueue;
        this.f56926o = z2;
    }

    @Override // qp.b
    public final void A(String str) {
        F().A(str);
    }

    @Override // qp.b
    public final boolean B(int i10) {
        return F().B(i10);
    }

    @Override // qp.b
    public final void C(Object obj, String str) {
        F().C(obj, str);
    }

    @Override // qp.b
    public final void D(Exception exc, String str) {
        F().D(exc, str);
    }

    @Override // qp.b
    public final void E(String str, IOException iOException) {
        F().E(str, iOException);
    }

    public final qp.b F() {
        if (this.f56921c != null) {
            return this.f56921c;
        }
        if (this.f56926o) {
            return b.f56915b;
        }
        if (this.f56924m == null) {
            Queue queue = this.f56925n;
            rp.a aVar = new rp.a();
            aVar.f53973c = this;
            aVar.f53972b = this.f56920b;
            aVar.f53974e = queue;
            this.f56924m = aVar;
        }
        return this.f56924m;
    }

    public final boolean G() {
        Boolean bool = this.f56922e;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            this.f56923i = this.f56921c.getClass().getMethod("log", rp.b.class);
            this.f56922e = Boolean.TRUE;
        } catch (NoSuchMethodException unused) {
            this.f56922e = Boolean.FALSE;
        }
        return this.f56922e.booleanValue();
    }

    public final boolean H() {
        return this.f56921c instanceof b;
    }

    public final boolean I() {
        return this.f56921c == null;
    }

    public final void J(rp.b bVar) {
        if (G()) {
            try {
                this.f56923i.invoke(this.f56921c, bVar);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException unused) {
            }
        }
    }

    public final void K(qp.b bVar) {
        this.f56921c = bVar;
    }

    @Override // qp.b
    public final boolean a() {
        return F().a();
    }

    @Override // qp.b
    public final boolean b() {
        return F().b();
    }

    @Override // qp.b
    public final void c(String str) {
        F().c(str);
    }

    @Override // qp.b
    public final void d(Long l7, IOException iOException) {
        F().d(l7, iOException);
    }

    @Override // qp.b
    public final boolean e() {
        return F().e();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && e.class == obj.getClass() && this.f56920b.equals(((e) obj).f56920b);
    }

    @Override // qp.b
    public final boolean f() {
        return F().f();
    }

    @Override // qp.b
    public final void g(String str, Object... objArr) {
        F().g(str, objArr);
    }

    @Override // qp.b
    public final String getName() {
        return this.f56920b;
    }

    @Override // qp.b
    public final void h(Object obj, String str, Object obj2) {
        F().h(obj, str, obj2);
    }

    public final int hashCode() {
        return this.f56920b.hashCode();
    }

    @Override // qp.b
    public final void i(Object obj, String str) {
        F().i(obj, str);
    }

    @Override // qp.b
    public final void j(String str, Object... objArr) {
        F().j(str, objArr);
    }

    @Override // qp.b
    public final void k(Object obj, String str, Object obj2) {
        F().k(obj, str, obj2);
    }

    @Override // qp.b
    public final void l(Object... objArr) {
        F().l(objArr);
    }

    @Override // qp.b
    public final void m(Object obj, String str) {
        F().m(obj, str);
    }

    @Override // qp.b
    public final void n(String str, Throwable th2) {
        F().n(str, th2);
    }

    @Override // qp.b
    public final void o(Object obj, String str) {
        F().o(obj, str);
    }

    @Override // qp.b
    public final boolean p() {
        return F().p();
    }

    @Override // qp.b
    public final void q(Object obj, String str) {
        F().q(obj, str);
    }

    @Override // qp.b
    public final void r(String str) {
        F().r(str);
    }

    @Override // qp.b
    public final void s(Object obj, String str, Object obj2) {
        F().s(obj, str, obj2);
    }

    @Override // qp.b
    public final void t(Object... objArr) {
        F().t(objArr);
    }

    @Override // qp.b
    public final void u(Object... objArr) {
        F().u(objArr);
    }

    @Override // qp.b
    public final void v(Object obj, String str, Object obj2) {
        F().v(obj, str, obj2);
    }

    @Override // qp.b
    public final void w(String str, Throwable th2) {
        F().w(str, th2);
    }

    @Override // qp.b
    public final void x(String str) {
        F().x(str);
    }

    @Override // qp.b
    public final void y(String str) {
        F().y(str);
    }

    @Override // qp.b
    public final void z(Exception exc, String str) {
        F().z(exc, str);
    }
}
