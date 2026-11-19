package q7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public final class n implements m, Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final transient Object f45762b = new Object();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final m f45763c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public volatile transient boolean f45764e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public transient Object f45765i;

    public n(m mVar) {
        this.f45763c = mVar;
    }

    @Override // q7.m
    public final Object get() {
        if (!this.f45764e) {
            synchronized (this.f45762b) {
                try {
                    if (!this.f45764e) {
                        Object obj = this.f45763c.get();
                        this.f45765i = obj;
                        this.f45764e = true;
                        return obj;
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        return this.f45765i;
    }

    public final String toString() {
        Object obj;
        StringBuilder sb2 = new StringBuilder("Suppliers.memoize(");
        if (this.f45764e) {
            obj = "<supplier that returned " + this.f45765i + ">";
        } else {
            obj = this.f45763c;
        }
        sb2.append(obj);
        sb2.append(")");
        return sb2.toString();
    }
}
