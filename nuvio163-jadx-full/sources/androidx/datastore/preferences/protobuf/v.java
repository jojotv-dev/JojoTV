package androidx.datastore.preferences.protobuf;

/* JADX INFO: loaded from: classes.dex */
public abstract class v implements Cloneable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final x f2579b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public x f2580c;

    public v(x xVar) {
        this.f2579b = xVar;
        if (xVar.g()) {
            throw new IllegalArgumentException("Default instance must be immutable.");
        }
        this.f2580c = xVar.i();
    }

    public final x a() {
        x xVarB = b();
        xVarB.getClass();
        if (x.f(xVarB, true)) {
            return xVarB;
        }
        throw new j1();
    }

    public final x b() {
        if (!this.f2580c.g()) {
            return this.f2580c;
        }
        x xVar = this.f2580c;
        xVar.getClass();
        x0 x0Var = x0.f2582c;
        x0Var.getClass();
        x0Var.a(xVar.getClass()).c(xVar);
        xVar.h();
        return this.f2580c;
    }

    public final void c() {
        if (this.f2580c.g()) {
            return;
        }
        x xVarI = this.f2579b.i();
        x xVar = this.f2580c;
        x0 x0Var = x0.f2582c;
        x0Var.getClass();
        x0Var.a(xVarI.getClass()).a(xVarI, xVar);
        this.f2580c = xVarI;
    }

    public final Object clone() {
        v vVar = (v) this.f2579b.c(5);
        vVar.f2580c = b();
        return vVar;
    }
}
