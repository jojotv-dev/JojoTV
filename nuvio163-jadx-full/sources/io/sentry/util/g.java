package io.sentry.util;

/* JADX INFO: loaded from: classes.dex */
public final class g {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final f f26259b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public volatile Object f26258a = null;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final a f26260c = new a();

    public g(f fVar) {
        this.f26259b = fVar;
    }

    public final Object a() {
        if (this.f26258a == null) {
            a aVar = this.f26260c;
            aVar.g();
            try {
                if (this.f26258a == null) {
                    this.f26258a = this.f26259b.e();
                }
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
        return this.f26258a;
    }

    public final void b(Object obj) {
        a aVar = this.f26260c;
        aVar.g();
        try {
            this.f26258a = obj;
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
}
