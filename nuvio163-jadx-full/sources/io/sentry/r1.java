package io.sentry;

/* JADX INFO: loaded from: classes5.dex */
public final class r1 implements w0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Runtime f26094a = Runtime.getRuntime();

    @Override // io.sentry.w0
    public final void a() {
    }

    @Override // io.sentry.w0
    public final void b(k3 k3Var) {
        Runtime runtime = this.f26094a;
        k3Var.f25679b = Long.valueOf(runtime.totalMemory() - runtime.freeMemory());
    }
}
