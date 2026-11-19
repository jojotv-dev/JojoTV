package io.sentry;

/* JADX INFO: loaded from: classes.dex */
public final class q4 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final q4 f26080c = new q4();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f26081a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final io.sentry.util.a f26082b = new io.sentry.util.a();

    public final void a() {
        io.sentry.util.a aVar = this.f26082b;
        aVar.g();
        try {
            if (!this.f26081a) {
                this.f26081a = true;
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
}
